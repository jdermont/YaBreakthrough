package pl.derjack.yabreakthrough.cpu;

import pl.derjack.yabreakthrough.game.Game;

public class SimpleEvaluator implements Evaluator {

    public static int popCount(long a) {
        int count = 0;
        while (a != 0) {
            count++;
            a &= a-1;
        }
        return count;
    }

    @Override
    public long getThinkingTimeInMillis() {
        return 750L;
    }

    @Override
    public int evaluateGame(int forPlayer, Game game) {
        int a = popCount(game.getOneBoard());
        int b = popCount(game.getTwoBoard());
        return forPlayer == Game.ONE ? a - b : b - a;
    }

}
