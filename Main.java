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

class TikTacToe{
    final int width = 3;

    private int filled_cells;
    private char player;
    private char computer;
    private char[][] FIELD;
    private boolean player_move;

    public TikTacToe() {
        player_move = true;

        initEmptyField();
        printField();
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

    public void makeMove() throws GameFinishedException{
        while (true) {
            System.out.print("Enter the coordinates: ");
            if (makeMove(new Scanner(System.in).nextLine().split(" ")) == 0){
                break;
            }
        }
    }

    public void gameModeEasy() throws GameFinishedException{
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

    private int makeMove(String[] strings) throws GameFinishedException{
        int line, column;
        try {
            line = Integer.parseInt(strings[0]);
            column = Integer.parseInt(strings[1]);
        } catch (Exception e) {
            if (player_move) {
                System.out.println("You should enter numbers!");
            }
            return 1;
        }

        if (line < 1 || line > 3 ||
                column < 1 || column > 3){
            if (player_move) {
                System.out.println("Coordinates should be from 1 to 3!");
            }
            return 1;
        }

        line--; column--;
        if (FIELD[line][column] == Cells.O || FIELD[line][column] == Cells.X){
            if (player_move) {
                System.out.println("This cell is occupied! Choose another one!");
            }
            return 1;
        }

        FIELD[line][column] = (player_move) ? player : computer;
        filled_cells++;
        player_move = !player_move;
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

        if (count_o < count_x){
            player = Cells.O;
            computer = Cells.X;
        } else {
            player = Cells.X;
            computer = Cells.O;
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

        player = Cells.X;
        computer = Cells.O;
    }
}

public class Main {
    public static void main(String[] args) {
        try{
            TikTacToe tikTacToe = new TikTacToe();

            while (true) {
                tikTacToe.makeMove();
                tikTacToe.gameModeEasy();
            }
        } catch (GameFinishedException e){
            System.out.println(e.getMessage());
        }
    }
}

