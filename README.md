# 20503-Java-Project

To make the project run follow the steps:

Part 1 -server
1) Download mysql from
   https://dev.mysql.com/downloads/installer/
2) Install from the link and at the end of the instillation 
    you will be asked to enter a password, remember it.
3) Create a new DataBase in MySQL  named chessdb.
4) In the server folder under the folder resources you will find a file named application.properties,
    in the file change the 3 lines of code:
   spring.datasource.url=jdbc:mysql://localhost:3306/chessdb ( the part of chessdb change to a different database if you wish to)
   
   spring.datasource.username="YOUR USERNAME"(usually its root if you didn't change it)
   
    spring.datasource.password="YOUR PASSWORD"(change to the database password)
6) After installing and changing the code you can run the ChessGameApplication .
 
Part 2 -client (android studio emulator)
1) Download android studio from
   https://developer.android.com/studio
2) Install it and run the client file in the code.
3) After choosing the file, give the program a bit of time to read the open project and install all it needs.
4) After opening the file in top under tools you will see device manager, open it.
5) A slide will open, press the + and choose a type of emulator to run and install it for the project, we will need to emulator to run simultaneously. 
6) After installing at least 2 different emulators at the top you will have the type of emulator that you will run after pressing start under it press select multiple devices, and you may choose how many emulators you will open in one click.
7) After choosing you can press the run button after that all the emulators that you choose will start to run wait for them to open, during the first time opening it may take a little bit of time.
8)When everything is done and the start screen is shown on your emulators, you can register/login and start playing.
