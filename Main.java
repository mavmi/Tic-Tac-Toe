package com.company;

import javax.swing.plaf.synth.SynthEditorPaneUI;
import java.util.Scanner;

class BadInputException extends Exception{
    public BadInputException(){
        this("Incorrect input!");
    }
    public BadInputException(String msg){
        super(msg);
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

    public TikTacToe() throws BadInputException{
        parseField();
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

    public void makeMove(){
        while (true) {
            System.out.print("Enter the coordinates: ");
            String[] strings = new Scanner(System.in).nextLine().split(" ");

            int line, column;
            try {
                line = Integer.parseInt(strings[0]);
                column = Integer.parseInt(strings[1]);
            } catch (Exception e) {
                System.out.println("You should enter numbers!");
                continue;
            }

            if (line < 1 || line > 3 ||
                    column < 1 || column > 3){
                System.out.println("Coordinates should be from 1 to 3!");
                continue;
            }

            line--; column--;
            if (FIELD[line][column] == Cells.O || FIELD[line][column] == Cells.X){
                System.out.println("This cell is occupied! Choose another one!");
                continue;
            }

            FIELD[line][column] = player;
            filled_cells++;
            printField();
            printTableState(getTableState());

            return;
        }
    }

    private void printTableState(TableState tableState){
        if (tableState == TableState.DRAW){
            System.out.println("Draw");
        } else if (tableState == TableState.NOT_FINISHED){
            System.out.println("Game not finished");
        } else if (tableState == TableState.X_WINS){
            System.out.println("X wins");
        } else if (tableState == TableState.O_WINS){
            System.out.println("O wins");
        }
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
}

public class Main {
    public static void main(String[] args) {
	    try{
            TikTacToe tikTacToe = new TikTacToe();
            tikTacToe.printField();
            tikTacToe.makeMove();
        } catch (BadInputException e){
            System.out.println(e.getMessage());
        }
    }
}
