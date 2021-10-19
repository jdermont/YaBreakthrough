package pl.derjack.yabreakthrough.cpu;

import pl.derjack.yabreakthrough.game.Game;

public interface Evaluator {
    int INF = 1000000000;
    int FINISH_THRESHOLD = 100000;

    long getThinkingTimeInMillis();
    int evaluateGame(int forPlayer, Game game);
}
