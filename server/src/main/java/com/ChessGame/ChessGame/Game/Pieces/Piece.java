package com.ChessGame.ChessGame.Game.Pieces;

import com.ChessGame.ChessGame.Game.Board.*;

import java.util.List;


    public abstract class Piece {
        private boolean white;
        private boolean moved = false;

        public Piece(boolean W) {
            this.white = W;
        }

        public void setWhite(boolean W) {
            this.white = white;
        }

        public void setMoved() {
            this.moved = true;
        }

        public boolean isWhite() {
            return this.white;
        }

        public boolean isMoved() {
            return this.moved;
        }

        public abstract String getType();

        public abstract List<Square> getValidMoves(Square[][] board, int row, int col);

    }

