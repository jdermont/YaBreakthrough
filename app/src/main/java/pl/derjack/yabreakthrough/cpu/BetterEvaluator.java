package pl.derjack.yabreakthrough.cpu;

import pl.derjack.yabreakthrough.game.Board;
import pl.derjack.yabreakthrough.game.Game;

public class BetterEvaluator implements Evaluator {
    private static final int PIECE_VALUE = 10;
    private static final int[] ROW_VALUE = { 5, 1, 2, 3, 4, 5, 5 };

    @Override
    public long getThinkingTimeInMillis() {
        return 1000L;
    }

    @Override
    public int evaluateGame(int forPlayer, Game game) {
        int currentPlayer = game.getCurrentPlayer();
        int rounds = game.getRounds();
        long oneBoard = game.getOneBoard();
        long twoBoard = game.getTwoBoard();

        if (currentPlayer == Game.ONE) {
            if ((oneBoard & (Board.TOP_ROW>>>8)) != 0) {
                int output = INF - rounds - 1;
                return forPlayer == Game.ONE ? output : -output;
            } else if ((twoBoard & (Board.BOTTOM_ROW<<8)) != 0) {
                long t = ((twoBoard >>> 7) & Board.RIGHT_MASK) | ((twoBoard >>> 9) & Board.LEFT_MASK);
                if ((oneBoard&t&Board.BOTTOM_ROW) == 0) {
                    int output = -INF + rounds + 2;
                    return forPlayer == Game.ONE ? output : -output;
                }
            }
        } else {
            if ((twoBoard & (Board.BOTTOM_ROW<<8)) != 0) {
                int output = INF - rounds - 1;
                return forPlayer == Game.TWO ? output : -output;
            } else if ((oneBoard & (Board.TOP_ROW>>>8)) != 0) {
                long t = ((oneBoard << 7) & Board.LEFT_MASK) | ((oneBoard << 9) & Board.RIGHT_MASK);
                if ((twoBoard&t&Board.TOP_ROW) == 0) {
                    int output = -INF + rounds + 2;
                    return forPlayer == Game.TWO ? output : -output;
                }
            }
        }

        int a = PIECE_VALUE * SimpleEvaluator.popCount(oneBoard);
        for (int row = 0; row < 7; row++) {
            a += ROW_VALUE[row] * SimpleEvaluator.popCount(oneBoard & (255L<<(8*row)));
        }
        int b = PIECE_VALUE * SimpleEvaluator.popCount(oneBoard);
        for (int row = 0; row < 7; row++) {
            b += ROW_VALUE[row] * SimpleEvaluator.popCount(twoBoard & (255L<<(8*(7-row))));
        }

        return forPlayer == Game.ONE ? a - b : b - a;
    }
}
