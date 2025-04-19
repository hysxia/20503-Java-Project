package com.gamechess.chessboard;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.gamechess.chessboard.pieces.*;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int BOARD_SIZE = 8; //8x8
    private static final int SQUARE_SIZE = 130; // Each square’s width/height in px

    // Two boards: one oriented for White, one for Black.
    private GridLayout chessBoardWhite;
    private GridLayout chessBoardBlack;

    // ImageView grids
    private ImageView[][] whiteSquares = new ImageView[BOARD_SIZE][BOARD_SIZE];
    private ImageView[][] blackSquares = new ImageView[BOARD_SIZE][BOARD_SIZE];

    // Logical piece arrays
    private Piece[][] whitePieceBoard = new Piece[BOARD_SIZE][BOARD_SIZE];
    private Piece[][] blackPieceBoard = new Piece[BOARD_SIZE][BOARD_SIZE];

    // Game info
    private Long currentGameId = null;
    private GameService gameService;
    private boolean isGameOver = false;

    private Handler gameUpdateHandler = new Handler();// Decides when to poll
    private Runnable gameUpdateRunnable; //Executes the polling logic

    // Player references
    private String whiteUser;
    private String blackUser;
    private boolean isUserWhite;


    // UI references
    private Button btnOfferDraw; //Offer Draw Button
    private Button btnResign; //Resign Button

    // For selecting & moving pieces
    private Piece selectedPiece = null;

    // Used to prevent repeated “opponent draw” popup
    private boolean drawDialogShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //grids for the black and white boards
        chessBoardWhite = findViewById(R.id.chessBoardWhite);
        chessBoardBlack = findViewById(R.id.chessBoardBlack);
        //array for the visual board for each user
        createChessBoard(chessBoardWhite, whiteSquares, false);
        createChessBoard(chessBoardBlack, blackSquares, true);

        btnOfferDraw = findViewById(R.id.btn_offer_draw);
        btnResign = findViewById(R.id.btn_resign);

        btnOfferDraw.setOnClickListener(v -> proposeDraw());
        btnResign.setOnClickListener(v -> resignGame());


        gameService = new GameService();
        String username = UserSession.getUsername();
        currentGameId = getIntent().getLongExtra("GAME_ID", -1);
        if (currentGameId == -1) {
            Toast.makeText(this, "No GAME_ID passed to MainActivity", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        //Initialize and start game state polling
        gameUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                if (currentGameId != null) {
                    //Get latest game state from server
                    gameService.getGame(currentGameId, new GameService.GameCallback<Game>() {
                        @Override
                        public void onSuccess(Game game) {
                            runOnUiThread(() -> updateUIFromServer(game));//Update UI with server data
                        }
                        @Override
                        public void onFailure(String errorMessage) {
                            Log.e("MainActivity", "Error fetching game: " + errorMessage);
                        }
                    });
                }

                gameUpdateHandler.postDelayed(this, 200);//Keep polling every 200ms
            }
        };

        gameUpdateHandler.postDelayed(gameUpdateRunnable, 500); //First poll after 500ms


    }

    //Setting up the chessboard UI for the user
    private void createChessBoard(GridLayout layout,
                                  ImageView[][] boardSquares,
                                  boolean isBlackPerspective) {

        layout.setColumnCount(BOARD_SIZE);
        layout.setRowCount(BOARD_SIZE);

        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {

                int displayRow = isBlackPerspective ? (BOARD_SIZE - 1 - row) : row;
                int displayCol = isBlackPerspective ? (BOARD_SIZE - 1 - col) : col;

                ImageView square = new ImageView(this);
                if ((row + col) % 2 == 0) {
                    square.setBackgroundColor(getResources().getColor(android.R.color.white));
                } else {
                    square.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_dark));
                }

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = SQUARE_SIZE;
                params.height = SQUARE_SIZE;
                params.rowSpec = GridLayout.spec(displayRow);
                params.columnSpec = GridLayout.spec(displayCol);
                square.setLayoutParams(params);


                square.setTag(new int[]{row, col});

                square.setOnTouchListener(this::onSquareTouch);


                layout.addView(square);
                boardSquares[row][col] = square;
            }
        }
    }

    //Handle the user touches on the chessboard
    private boolean onSquareTouch(View view, MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_DOWN) return false;


        GridLayout myBoardLayout = isUserWhite ? chessBoardWhite : chessBoardBlack;
        if (view.getParent() != myBoardLayout) {

            return true;
        }


        int[] rc = (int[]) view.getTag();
        int row = rc[0];
        int col = rc[1];


        Piece[][] pieceBoard = isUserWhite ? whitePieceBoard : blackPieceBoard;

        if (selectedPiece == null) {
            // If no piece is selected, try selecting one
            if (pieceBoard[row][col] != null) {
                // Check if the piece belongs to the user’s color
                Piece piece = pieceBoard[row][col];
                boolean isWhitePiece = piece.getId().charAt(0) == 'w';
                if (isWhitePiece == isUserWhite) {
                    selectedPiece = piece;
                    highlightLegalMoves(piece);
                } else {
                    Toast.makeText(this, "You can only select your own pieces!", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            // We already have a piece selected => attempt the move
            handleMove(selectedPiece, row, col);
            clearHighlights();
            selectedPiece = null;
        }
        return true;
    }

    //Attempting to move a piece to a new square
    private void handleMove(Piece piece, int destRow, int destCol) {
        if (currentGameId == null) {
            Toast.makeText(this, "No active game ID!", Toast.LENGTH_SHORT).show();
            return;
        }
        String username = UserSession.getUsername();
        int startRow = piece.getRow();
        int startCol = piece.getCol();

        // Make the move on the server
        gameService.makeMove(currentGameId, username, startRow, startCol, destRow, destCol,
                new GameService.GameCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        switch (result) {
                            case "MOVE_OK":

                                break;
                            case "CHECKMATE":
                                movePieceLocally(piece, destRow, destCol);
                                break;
                            case "CHECK":
                                movePieceLocally(piece, destRow, destCol);
                                Toast.makeText(MainActivity.this, "Check!", Toast.LENGTH_SHORT).show();
                                break;
                            case "STALEMATE":
                                Toast.makeText(MainActivity.this, "Draw by stalemate!", Toast.LENGTH_LONG).show();
                                break;
                            case "DRAW_BY_50_MOVE_RULE":
                                Toast.makeText(MainActivity.this, "Draw by 50-move rule!", Toast.LENGTH_LONG).show();
                                break;
                            case "PROMOTION_REQUIRED":
                                showPromotionDialog(piece, destRow, destCol);
                                break;
                            case "INVALID_MOVE":
                                Toast.makeText(MainActivity.this, "Invalid move!", Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                Toast.makeText(MainActivity.this, "Unknown result: " + result, Toast.LENGTH_SHORT).show();
                        }
                        // After local changes, we fetch the real server state
                        fetchUpdatedGameState();
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(MainActivity.this, "Move error: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }
    // Update piece location locally
    private void movePieceLocally(Piece piece, int targetRow, int targetCol) {
        // Remove from old location
        Piece[][] pieceBoard = isUserWhite ? whitePieceBoard : blackPieceBoard;
        ImageView[][] squares = isUserWhite ? whiteSquares : blackSquares;

        int oldRow = piece.getRow();
        int oldCol = piece.getCol();

        pieceBoard[oldRow][oldCol] = null;
        squares[oldRow][oldCol].setImageDrawable(null);

        // Update piece’s position
        piece.setPosition(targetRow, targetCol);

        // Place in new location
        pieceBoard[targetRow][targetCol] = piece;
        int drawableId = getDrawableIdByPiece(piece);
        squares[targetRow][targetCol].setImageResource(drawableId);
    }

    //Show promotion dialog for the pawn piece
    private void showPromotionDialog(Piece pawn, int destRow, int destCol) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Promote Pawn");
        builder.setItems(new CharSequence[]{"Queen", "Rook", "Bishop", "Knight"}, (dialog, which) -> {
            String promotionPieceType;
            switch (which) {
                case 0: promotionPieceType = "Queen";  break;
                case 1: promotionPieceType = "Rook";   break;
                case 2: promotionPieceType = "Bishop"; break;
                default:promotionPieceType = "Knight"; break;
            }
            promotePawn(pawn, destRow, destCol, promotionPieceType);
        });
        builder.show();
    }
    //Handle the pawn promotion
    private void promotePawn(Piece pawn, int destRow, int destCol, String promotionPieceType) {
        if (currentGameId == null) return;

        String username = UserSession.getUsername();
        gameService.promotePawn(currentGameId, username, promotionPieceType, destRow, destCol,
                new GameService.GameCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        if ("PROMOTION_OK".equals(result)) {
                            // We do a local move if we want
                            movePieceLocally(pawn, destRow, destCol);
                            fetchUpdatedGameState();
                        } else {
                            Toast.makeText(MainActivity.this, "Promotion failed: " + result, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(MainActivity.this, "Promotion error: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    //Highlight all legal moves for a piece
    private void highlightLegalMoves(Piece piece) {
        if (currentGameId == null) {
            Toast.makeText(this, "No Game ID yet", Toast.LENGTH_SHORT).show();
            return;
        }
        boolean forWhite = (piece.getId().charAt(0) == 'w');
        if (forWhite != isUserWhite) {
            // Just a safety check
            return;
        }
        gameService.getLegalMoves(currentGameId, piece.getRow(), piece.getCol(), forWhite,
                new GameService.GameCallback<List<Square>>() {
                    @Override
                    public void onSuccess(List<Square> legalMoves) {
                        for (Square sq : legalMoves) {
                            int r = sq.getRow();
                            int c = sq.getCol();
                            ImageView[][] squares = isUserWhite ? whiteSquares : blackSquares;
                            squares[r][c].setBackgroundColor(Color.YELLOW);
                        }
                    }
                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(MainActivity.this, "Legal moves error: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }
    //Clear highlight from squares
    private void clearHighlights() {
        ImageView[][] squares = isUserWhite ? whiteSquares : blackSquares;
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if ((i + j) % 2 == 0) {
                    squares[i][j].setBackgroundColor(getResources().getColor(android.R.color.white));
                } else {
                    squares[i][j].setBackgroundColor(getResources().getColor(android.R.color.holo_blue_dark));
                }
            }
        }
    }

    //Update the board UI based on the server side
    private void updateUIFromServer(Game game) {
        if (game == null) return;

        // Identify who is white/black from the server
        this.whiteUser = game.getWhitePlayer() != null ? game.getWhitePlayer().getUsername() : null;
        this.blackUser = game.getBlackPlayer() != null ? game.getBlackPlayer().getUsername() : null;

        // Figure out if the current user is White
        String myUsername = UserSession.getUsername();
        isUserWhite = (whiteUser != null && whiteUser.equals(myUsername));
        // White board view
        TextView whitePlayerWhiteView = findViewById(R.id.white_player_white_view);
        TextView blackPlayerWhiteView = findViewById(R.id.black_player_white_view);

        // Black board view
        TextView whitePlayerBlackView = findViewById(R.id.white_player_black_view);
        TextView blackPlayerBlackView = findViewById(R.id.black_player_black_view);

        // Set player names for white board
        whitePlayerWhiteView.setText(whiteUser);
        blackPlayerWhiteView.setText(blackUser);
        //Set player names for black board
        whitePlayerBlackView.setText(whiteUser);
        blackPlayerBlackView.setText(blackUser);


        // Possibly hide the opposite board
        if (isUserWhite) {
            // Show white board
            chessBoardWhite.setVisibility(View.VISIBLE);
            whitePlayerWhiteView.setVisibility(View.VISIBLE);
            blackPlayerWhiteView.setVisibility(View.VISIBLE);

            // Hide black board
            chessBoardBlack.setVisibility(View.GONE);
            whitePlayerBlackView.setVisibility(View.GONE);
            blackPlayerBlackView.setVisibility(View.GONE);
        } else {
            // Show black board
            chessBoardBlack.setVisibility(View.VISIBLE);
            whitePlayerBlackView.setVisibility(View.VISIBLE);
            blackPlayerBlackView.setVisibility(View.VISIBLE);

            // Hide white board
            chessBoardWhite.setVisibility(View.GONE);
            whitePlayerWhiteView.setVisibility(View.GONE);
            blackPlayerWhiteView.setVisibility(View.GONE);
        }

        // Check if game is finished
        if (!isGameOver && game.isFinished()) {
            isGameOver = true;
            handleGameOverDisplay(game);
            return;
        }

        // If the opponent offered a draw, show pop-up (once)
        boolean myOpponentOfferedDraw = (isUserWhite && game.isBlackDrawOffered())
                || (!isUserWhite && game.isWhiteDrawOffered());
        if (myOpponentOfferedDraw && !drawDialogShown) {
            showOpponentDrawDialog();
            drawDialogShown = true;
        } else if (!myOpponentOfferedDraw) {
            drawDialogShown = false;
        }

        //Updates who plays now
        boolean isWhiteTurn = game.getCurrentTurn().equals(whiteUser);
        updateTurnIndicator(isWhiteTurn);



        // Now update the boards visually from game.getBoardState()
        String[][] boardState = game.getBoardState();
        if (boardState == null) return;

        // Clear local piece arrays
        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                whitePieceBoard[r][c] = null;
                blackPieceBoard[r][c] = null;
            }
        }

        //Rebuild piece arrays from server codes
        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                String code = boardState[r][c];
                if (code != null && !code.isEmpty()) {
                    Piece piece = determinePieceTypeFromServer(code, r, c);
                    whitePieceBoard[r][c] = piece;
                    blackPieceBoard[r][c] = piece;
                }
            }
        }

        //Refresh images on both boards
        refreshBoard(whiteSquares, whitePieceBoard);
        refreshBoard(blackSquares, blackPieceBoard);
    }
    //Update the turnindicator that show which turn is it
    private void updateTurnIndicator(boolean isWhiteTurn) {
        TextView turnIndicator = findViewById(R.id.turn_indicator);
        if(isWhiteTurn) {
            turnIndicator.setText("It's " + this.whiteUser + "'s turn");
        } else {
            turnIndicator.setText("It's " + this.blackUser + "'s turn");
        }
    }



    // Redraw the given squares array from the pieceBoard data
    private void refreshBoard(ImageView[][] squares, Piece[][] pieceBoard) {
        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                squares[r][c].setImageDrawable(null); // clear old image
                Piece p = pieceBoard[r][c];
                if (p != null) {
                    int drawableId = getDrawableIdByPiece(p);
                    squares[r][c].setImageResource(drawableId);
                }
            }
        }
    }


    //Retrieve the updated game state from the server
    private void fetchUpdatedGameState() {
        if (currentGameId == null) return;
        gameService.getGame(currentGameId, new GameService.GameCallback<Game>() {
            @Override
            public void onSuccess(Game game) {
                runOnUiThread(() -> updateUIFromServer(game));
            }
            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(MainActivity.this, "Update error: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Propose a draw to the opponent
    private void proposeDraw() {
        if (currentGameId == null) return;

        String username = UserSession.getUsername();
        gameService.proposeDraw(currentGameId, username, new GameService.GameCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if ("DRAW_OFFERED".equals(result)) {
                    Toast.makeText(MainActivity.this, "Draw offer sent!", Toast.LENGTH_SHORT).show();
                } else if ("DRAW_AGREED".equals(result)) {
                    // The game ended in a draw
                    endGame("Game ended in a draw!", "Draw");
                } else {
                    Toast.makeText(MainActivity.this, "Draw offer result: " + result, Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(MainActivity.this, "Draw offer error: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
    //Show dialog for offering a draw
    private void showOpponentDrawDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Draw Offer")
                .setMessage("Your opponent has offered a draw. Accept?")
                .setPositiveButton("Accept", (dialog, which) -> proposeDraw())
                .setNegativeButton("Decline", (dialog, which) -> {
                    declineDrawOffer();
                })
                .show();
    }
    //Decline the draw offering
    private void declineDrawOffer() {
        if (currentGameId == null) return;

        String username = UserSession.getUsername();
        gameService.declineDraw(currentGameId, username, new GameService.GameCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Toast.makeText(MainActivity.this, "Draw declined: " + result, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(MainActivity.this, "Decline draw error: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Resign from the game
    private void resignGame() {
        if (currentGameId == null) return;

        String username = UserSession.getUsername();
        gameService.resignGame(currentGameId, username, new GameService.GameCallback<String>() {
            @Override
            public void onSuccess(String result) {

                if ("PLAYER_RESIGNED".equals(result)) {
                    String displayMessage;
                    if (isUserWhite) {
                        displayMessage = "You resigned. Black wins!";
                        endGame(displayMessage, "Black");
                    } else {
                        displayMessage = "You resigned. White wins!";
                        endGame(displayMessage, "White");
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Resign result: " + result, Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(MainActivity.this, "Resign error: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Display game over messages
    private void handleGameOverDisplay(Game game) {
        String message;

        Game.EndReason endReason = game.getEndReason();
        if(game.isDraw()){
            message = "Game ended in a draw!";
        }
        else if(endReason == Game.EndReason.RESIGNATION){
            if(!isUserWhite){
                message = "White resigned. You won!";
            }
            else{
                message = "Black resigned. You won!";
            }
        }
        else {
            // If the current turn was the losing side
            boolean iAmWinner = !game.getCurrentTurn().equals(UserSession.getUsername());
            String winnerColor = iAmWinner ? (isUserWhite ? "White" : "Black") : (isUserWhite ? "Black" : "White");
            message = "Game over! " + winnerColor + " wins.";
        }


        endGame(message, "");
    }
    //finalization of the ending of the game
    private void endGame(String displayMessage, String resultToSave) {
        isGameOver = true;
        new AlertDialog.Builder(this)
                .setTitle("Game Over")
                .setMessage(displayMessage)
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> {
                    // Return to home
                    Intent intent = new Intent(MainActivity.this, HomeScreenActivity.class);
                    startActivity(intent);
                    finish();
                })
                .show();
    }
    //Determinate drawable id for the pieces
    private int getDrawableIdByPiece(Piece piece) {
        // color is piece.getId().charAt(0) => 'w' or 'b'
        char color = piece.getId().charAt(0);
        if (piece instanceof Pawn) {
            return color == 'w' ? R.drawable.blackpawn : R.drawable.whitepawn;
        } else if (piece instanceof Rook) {
            return color == 'w' ? R.drawable.whiterock : R.drawable.blackrock;
        } else if (piece instanceof Knight) {
            return color == 'w' ? R.drawable.whiteknight : R.drawable.blackknight;
        } else if (piece instanceof Bishop) {
            return color == 'w' ? R.drawable.whitebishop : R.drawable.blackbishop;
        } else if (piece instanceof Queen) {
            return color == 'w' ? R.drawable.whitequeen : R.drawable.blackqueen;
        } else if (piece instanceof King) {
            return color == 'w' ? R.drawable.whiteking : R.drawable.blackking;
        }
        return 0;
    }
    //Convert pieces code from the server to piece objects
    private Piece determinePieceTypeFromServer(String pieceCode, int row, int col) {
        // pieceCode might be "wP", "bK", etc.
        if (pieceCode == null || pieceCode.isEmpty()) return null;

        char color = pieceCode.charAt(0); // 'w' or 'b'
        char type = pieceCode.charAt(1); // 'P', 'R', 'N', 'B', 'Q', 'K'
        String id = color + "" + type + "_" + row + "_" + col;

        switch (type) {
            case 'P': return new Pawn(id, row, col);
            case 'R': return new Rook(id, row, col);
            case 'N': return new Knight(id, row, col);
            case 'B': return new Bishop(id, row, col);
            case 'Q': return new Queen(id, row, col);
            case 'K': return new King(id, row, col);
            default:  return null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Stop polling
        gameUpdateHandler.removeCallbacks(gameUpdateRunnable); //Stop polling when activity ends
    }
}