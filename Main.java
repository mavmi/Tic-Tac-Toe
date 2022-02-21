package com.company;

import java.util.Random;
import java.util.Scanner;

class BadInputException extends Exception{
    public BadInputException(){
        this("Incorrect input!");
    }
    public BadInputException(String msg){
        super(msg);
    }
}

class GameFinishedException extends Exception{
    public GameFinishedException(String state){
        super(state);
    }
}

class Cells{
    public static final char X = 'X';
    public static final char O = 'O';
    public static final char EMPTY_IN = '_';
    public static final char EMPTY_OUT = ' ';
}

enum TableState{
    NOT_FINISHED,
    DRAW,
    X_WINS,
    O_WINS
}

enum Players{
    PLAYER,
    AI_EASY
}

class TikTacToe{
    final int width = 3;

    private int filled_cells;
    private char[][] FIELD;
    private boolean playerOneMove;
    private Players[] players;

    public TikTacToe() {
        playerOneMove = true;
        players = new Players[2];

        initEmptyField();
    }

    public void parseCommand(){
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Input command: ");

            String[] argv = scanner.nextLine().split(" ");
            try {
                if (argv[0].equals("start") && argv.length == 3) {
                    for (int i = 1; i < 3; i++){
                        if (argv[i].equals("user")){
                            players[i - 1] = Players.PLAYER;
                        } else if (argv[i].equals("easy")){
                            players[i - 1] = Players.AI_EASY;
                        } else {
                            throw new BadInputException("Bad parameters!");
                        }
                    }
                } else if (argv[0].equals("exit") && argv.length == 1) {
                    System.exit(0);
                } else {
                    throw new BadInputException("Bad parameters!");
                }
                break;
            } catch (BadInputException e){
                System.out.println(e.getMessage());
            }
        }
    }

    public void start() throws GameFinishedException{
        printField();

        int player_num = 0;
        while (true){
            if (players[player_num] == Players.PLAYER){
                makeMove();
            } else if (players[player_num] == Players.AI_EASY){
                gameModeEasy();
            }

            player_num = (player_num == 0) ? 1 : 0;
        }
    }

    public void printField(){
        System.out.println("---------");
        for (int i = 0; i < width; i++){
            System.out.print('|');
            for (int j = 0; j < width; j++){
                System.out.print(' ');
                System.out.print(FIELD[i][j]);
            }
            System.out.println(" |");
        }
        System.out.println("---------");
    }

    private void gameModeEasy() throws GameFinishedException{
        System.out.println("Making move level \"easy\"");
        Random random = new Random();

        while (true){
            if (makeMove(new String[]{
                    Integer.toString(random.nextInt(3) + 1),
                    Integer.toString(random.nextInt(3) + 1)}) == 0) {
                break;
            }
        }
    }

    private void makeMove() throws GameFinishedException{
        while (true) {
            System.out.print("Enter the coordinates: ");
            if (makeMove(new Scanner(System.in).nextLine().split(" ")) == 0){
                break;
            }
        }
    }

    private int makeMove(String[] strings) throws GameFinishedException{
        int line, column;
        int player_num = (playerOneMove) ? 0 : 1;

        try {
            line = Integer.parseInt(strings[0]);
            column = Integer.parseInt(strings[1]);
        } catch (Exception e) {
            if (players[player_num] == Players.PLAYER) {
                System.out.println("You should enter numbers!");
            }
            return 1;
        }

        if (line < 1 || line > 3 ||
                column < 1 || column > 3){
            if (players[player_num] == Players.PLAYER) {
                System.out.println("Coordinates should be from 1 to 3!");
            }
            return 1;
        }

        line--; column--;
        if (FIELD[line][column] == Cells.O || FIELD[line][column] == Cells.X){
            if (players[player_num] == Players.PLAYER) {
                System.out.println("This cell is occupied! Choose another one!");
            }
            return 1;
        }

        FIELD[line][column] = (playerOneMove) ? Cells.X : Cells.O;
        filled_cells++;
        playerOneMove = !playerOneMove;
        printField();

        TableState tableState = getTableState();
        if (tableState != TableState.NOT_FINISHED){
            throw new GameFinishedException(tableStateToString(tableState));
        }

        return 0;
    }

    private String tableStateToString(TableState tableState){
        if (tableState == TableState.DRAW){
            return "Draw";
        } else if (tableState == TableState.NOT_FINISHED){
            return "Game not finished";
        } else if (tableState == TableState.X_WINS){
            return "X wins";
        } else if (tableState == TableState.O_WINS){
            return "O wins";
        }
        return null;
    }

    private TableState getTableState(){
        for (int i = 0; i < width; i++){
            if (FIELD[i][0] != Cells.EMPTY_OUT &&
                    FIELD[i][0] == FIELD[i][1] && FIELD[i][0] == FIELD[i][2]){
                return getWinner(FIELD[i][0]);
            }
            if (FIELD[0][i] != Cells.EMPTY_OUT &&
                    FIELD[0][i] == FIELD[1][i] && FIELD[0][i] == FIELD[2][i]){
                return getWinner(FIELD[0][i]);
            }
        }
        if (FIELD[0][0] != Cells.EMPTY_OUT &&
                FIELD[0][0] == FIELD[1][1] && FIELD[0][0] == FIELD[2][2]){
            return getWinner(FIELD[0][0]);
        }
        if (FIELD[2][0] != Cells.EMPTY_OUT &&
                FIELD[2][0] == FIELD[1][1] && FIELD[2][0] == FIELD[0][2]){
            return getWinner(FIELD[2][0]);
        }

        if (filled_cells == 9){
            return TableState.DRAW;
        }
        return TableState.NOT_FINISHED;
    }

    private TableState getWinner(char c){
        return (c == Cells.X) ? TableState.X_WINS : TableState.O_WINS;
    }

    private void parseField() throws BadInputException{
        System.out.print("Enter the cells: ");
        String input = new Scanner(System.in).nextLine();

        filled_cells = 0;
        int count_x = 0, count_o = 0;
        final int required_size = 9;

        if (input.length() != required_size){
            throw new BadInputException("User input has wrong size");
        }

        FIELD = new char[3][3];
        for (int i = 0; i < required_size; i++){
            char c = input.charAt(i);
            if (c != Cells.EMPTY_IN && c != Cells.O && c != Cells.X){
                throw new BadInputException("User input has wrong symbol");
            }
            FIELD[i / width][i % width] = (c == Cells.EMPTY_IN) ? Cells.EMPTY_OUT : c;
            if (c != Cells.EMPTY_IN){
                filled_cells++;
            }
            if (c == Cells.O){
                count_o++;
            } else if (c == Cells.X){
                count_x++;
            }
        }
    }

    private void initEmptyField(){
        filled_cells = 0;
        FIELD = new char[3][3];

        for (int i = 0; i < width; i++){
            for (int j = 0; j < width; j++){
                FIELD[i][j] = Cells.EMPTY_OUT;
            }
        }
    }
}

public class Main {
    public static void main(String[] args) {
        try{
            TikTacToe tikTacToe = new TikTacToe();
            tikTacToe.parseCommand();
            tikTacToe.start();
        } catch (GameFinishedException e){
            System.out.println(e.getMessage());
        }
    }
}

