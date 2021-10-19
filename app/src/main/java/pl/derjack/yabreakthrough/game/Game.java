package pl.derjack.yabreakthrough.game;

import java.util.ArrayList;
import java.util.List;

public class Game {
    public static final int NONE = Board.NONE;
    public static final int ONE = Board.ONE;
    public static final int TWO = Board.TWO;

    private Board board;
    private int currentPlayer;

    private List<Action> moveHistory;
    private int rounds;

    public Game() {
        board = new Board();
        currentPlayer = ONE;
        moveHistory = new ArrayList<>();
    }

    // without move history
    public void setGame(Game game) {
        this.board.setBoard(game.board);
        this.currentPlayer = game.currentPlayer;
        this.moveHistory.clear();
        this.rounds = game.rounds;
    }

    public List<Action> getAvailableMoves() {
        return board.getActions(currentPlayer);
    }

    public void makeMove(Action move) {
        board.makeAction(move, currentPlayer);
        moveHistory.add(move);
        rounds++;
        changePlayer();
    }

    public void undoMove() {
        changePlayer();
        Action move = moveHistory.remove(moveHistory.size()-1);
        board.undoAction(move, currentPlayer);
        rounds--;
    }

    private void changePlayer() {
        currentPlayer = currentPlayer == ONE ? TWO : ONE;
    }

    public long getHash() {
        return board.getHash() ^ currentPlayer;
    }

    public boolean isOver() {
        return board.isOver();
    }

    public int getWinner() {
        return board.getWinner();
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public int getRounds() {
        return rounds;
    }

    public long getOneBoard() {
        return board.oneBoard;
    }

    public long getTwoBoard() {
        return board.twoBoard;
    }

    @Override
    public String toString() {
        return board.toString() + currentPlayer + "\n";
    }

    // BoardView specifics
    public int getPawn(int row, int col) {
        col = 7 - col;
        int i = 8 * row + col;
        if ((board.oneBoard & (1L<<i)) != 0) {
            return ONE;
        } else if ((board.twoBoard & (1L<<i)) != 0) {
            return TWO;
        }
        return NONE;
    }

    public List<int[]> getTargets(int row, int col) {
        List<int[]> output = new ArrayList<>();
        if (getPawn(row, col) == currentPlayer) {
            for (long target : board.getTargets(row * 8 + (7 - col), currentPlayer)) {
                for (int i=0; i < 64; i++) {
                    if ((target & (1L << i)) != 0) {
                        output.add(new int[] { i/8, 7 - i%8 });
                        break;
                    }
                }

            }
        }
        return output;
    }

    public void makeMove(int rowFrom, int colFrom, int rowTo, int colTo) {
       Action action = Action.createAction(rowFrom, colFrom, rowTo, colTo);
       makeMove(action);
    }

    public List<int[]> getLastSquares() {
        List<int[]> output = new ArrayList<>();
        if (!moveHistory.isEmpty()) {
            Action lastAction = moveHistory.get(moveHistory.size()-1);
            for (int i=0; i < 64; i++) {
                if ((lastAction.move & (1L<<i)) != 0) {
                    int row = i/8;
                    int col = 7 - i%8;
                    output.add(new int[] { row, col });
                }
            }
        }
        return output;
    }

}
