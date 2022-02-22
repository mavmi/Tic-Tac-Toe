package com.company;

import java.util.ArrayList;
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

class Minimax{
    public int x;
    public int y;
    public int result;

    public Minimax(int x, int y, int result){
        this.x = x;
        this.y = y;
        this.result = result;
    }
}

enum TableState{
    NOT_FINISHED,
    DRAW,
    X_WINS,
    O_WINS
}

enum Players{
    PLAYER,
    AI_EASY,
    AI_MEDIUM,
    AI_HARD
}

class TikTacToe{
    final int width = 3;

    private int filled_cells;
    private int player_num;
    private char[][] FIELD;
    private Players[] players;

    public TikTacToe() {
        players = new Players[2];

        initEmptyField();
        parseCommand();
    }

    public void start() throws GameFinishedException{
        printField();

        player_num = 0;
        while (true){
            if (players[player_num] == Players.PLAYER){
                makeMove();
            } else if (players[player_num] == Players.AI_EASY){
                System.out.println("Making move level \"easy\"");
                gameModeEasy();
            } else if (players[player_num] == Players.AI_MEDIUM){
                System.out.println("Making move level \"medium\"");
                gameModeMedium();
            } else if (players[player_num] == Players.AI_HARD){
                System.out.println("Making move level \"hard\"");
                gameModeHard();
            }

            player_num = nextPlayer(player_num);
        }
    }

    private void parseCommand(){
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Input command: ");

            String[] argv = scanner.nextLine().split(" ");
            try {
                if (argv[0].equals("start") && argv.length == 3) {
                    for (int i = 1; i < 3; i++){
                        switch (argv[i]) {
                            case "user":
                                players[i - 1] = Players.PLAYER;
                                break;
                            case "easy":
                                players[i - 1] = Players.AI_EASY;
                                break;
                            case "medium":
                                players[i - 1] = Players.AI_MEDIUM;
                                break;
                            case "hard":
                                players[i - 1] = Players.AI_HARD;
                                break;
                            default:
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
        Random random = new Random();

        while (true){
            if (makeMove(random.nextInt(3) + 1,random.nextInt(3) + 1) == 0) {
                break;
            }
        }
    }

    private void gameModeMedium() throws GameFinishedException{
        char[] chars = new char[]{
            getPlayerChar(),
            getEnemyChar()
        };
        for (int I = 0; I < chars.length; I++){
            for (int i = 0; i < width; i++){
                int[] positions = new int[]{0, 1, 2};
                for (int j = 0; j < 3; j++){
                    if (FIELD[i][positions[0]] == FIELD[i][positions[1]] &&
                            FIELD[i][positions[0]] == chars[I] && FIELD[i][positions[2]] == Cells.EMPTY_OUT) {
                        makeMove(i + 1,positions[2] + 1);
                        return;
                    }
                    if (FIELD[positions[0]][i] == FIELD[positions[1]][i] &&
                            FIELD[positions[0]][i] == chars[I] && FIELD[positions[2]][i] == Cells.EMPTY_OUT) {
                        makeMove(positions[2] + 1, i + 1);
                        return;
                    }

                    int tmp = positions[2];
                    positions[2] = positions[1];
                    positions[1] = positions[0];
                    positions[0] = tmp;
                }
            }

            int[][][] positions = new int[][][]{
                {
                    {0, 0},
                    {1, 1},
                    {2, 2}
                },
                {
                    {0, 2},
                    {1, 1},
                    {2, 0}
                }
            };
            for (int L = 0; L < positions.length; L++) {
                for (int i = 0; i < 3; i++) {
                    if (FIELD[positions[L][0][0]][positions[L][0][1]] == FIELD[positions[L][1][0]][positions[L][1][1]] &&
                            FIELD[positions[L][0][0]][positions[L][0][1]] == chars[I] &&
                            FIELD[positions[L][2][0]][positions[L][2][1]] == Cells.EMPTY_OUT) {
                        makeMove(positions[L][2][0] + 1, positions[L][2][1] + 1);
                        return;
                    }

                    int[] tmp0 = positions[L][0];
                    int[] tmp1 = positions[L][1];
                    int[] tmp2 = positions[L][2];
                    positions[L][0] = tmp1;
                    positions[L][1] = tmp2;
                    positions[L][2] = tmp0;
                }
            }
        }
        gameModeEasy();
    }

    private void gameModeHard() throws GameFinishedException{
        ArrayList<Minimax> results = minimax(copyField(), player_num);

        Minimax max = results.get(0);
        for (Minimax minimax : results){
            if (minimax.result > max.result){
                max = minimax;
            }
        }
        makeMove(max.x + 1, max.y + 1);
    }

    private ArrayList<Minimax> minimax(char[][] field, int playerNum) {
        ArrayList<Minimax> results = new ArrayList<>();

        for (int i = 0; i < width; i++){
            for (int j = 0; j < width; j++){
                if (field[i][j] == Cells.EMPTY_OUT){
                    char[][] field_cpy = copyField(field);
                    field_cpy[i][j] = getPlayerChar(playerNum);

                    TableState tableState = getTableState(field_cpy);
                    if (tableState == TableState.NOT_FINISHED){
                        ArrayList<Minimax> return_value = minimax(field_cpy, nextPlayer(playerNum));
                        for (Minimax minimax : return_value){
                            results.add(new Minimax(i, j, minimax.result));
                        }
                    } else if (tableState == TableState.DRAW) {
                        results.add(new Minimax(i, j, 0));
                    } else if (tableState == TableState.X_WINS && player_num == 0 ||
                                tableState == TableState.O_WINS && player_num == 1){
                        results.add(new Minimax(i, j, 10));
                    } else {
                        results.add(new Minimax(i, j, -10));
                    }
                }
            }
        }

        return results;
    }

    private char[][] copyField(){
        return copyField(FIELD);
    }

    private char[][] copyField(char[][] field){
        char[][] fieldCpy = new char[width][width];

        for (int i = 0; i < width; i++){
            for (int j = 0; j < width; j++){
                fieldCpy[i][j] = field[i][j];
            }
        }

        return fieldCpy;
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

        try {
            line = Integer.parseInt(strings[0]);
            column = Integer.parseInt(strings[1]);
        } catch (Exception e) {
            if (players[player_num] == Players.PLAYER) {
                System.out.println("You should enter numbers!");
            }
            return 1;
        }

        return makeMove(line, column);
    }

    private int makeMove(int line, int column) throws GameFinishedException{
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

        FIELD[line][column] = getPlayerChar();
        filled_cells++;
        printField();

        TableState tableState = getTableState();
        if (tableState != TableState.NOT_FINISHED){
            throw new GameFinishedException(tableStateToString(tableState));
        }

        return 0;
    }

    private char getPlayerChar(){
        return getPlayerChar(player_num);
    }

    private char getPlayerChar(int playerNum){
        return (playerNum == 0) ? Cells.X : Cells.O;
    }

    private char getEnemyChar(){
        return getEnemyChar(player_num);
    }

    private char getEnemyChar(int playerNum){
        return (playerNum == 0) ? Cells.O : Cells.X;
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
        return getTableState(FIELD);
    }

    private TableState getTableState(char[][] field){
        for (int i = 0; i < width; i++){
            if (field[i][0] != Cells.EMPTY_OUT &&
                    field[i][0] == field[i][1] && field[i][0] == field[i][2]){
                return getWinner(field[i][0]);
            }
            if (field[0][i] != Cells.EMPTY_OUT &&
                    field[0][i] == field[1][i] && field[0][i] == field[2][i]){
                return getWinner(field[0][i]);
            }
        }
        if (field[0][0] != Cells.EMPTY_OUT &&
                field[0][0] == field[1][1] && field[0][0] == field[2][2]){
            return getWinner(field[0][0]);
        }
        if (field[2][0] != Cells.EMPTY_OUT &&
                field[2][0] == field[1][1] && field[2][0] == field[0][2]){
            return getWinner(field[2][0]);
        }

        if (filled_cells == 9){
            return TableState.DRAW;
        }
        return TableState.NOT_FINISHED;
    }

    private int nextPlayer(int playerNum){
        return (playerNum == 0) ? 1 : 0;
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
            tikTacToe.start();
        } catch (GameFinishedException e){
            System.out.println(e.getMessage());
        }
    }
}
