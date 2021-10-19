package pl.derjack.yabreakthrough.cpu;

import pl.derjack.yabreakthrough.game.Action;

public class Move implements Comparable<Move> {
    public Action move;
    public int score;

    public Move(Action move) {
        this.move = move;
    }

    @Override
    public int compareTo(Move o) {
        return Integer.compare(score, o.score);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Move{");
        sb.append("move=").append(move);
        sb.append(", score=").append(score);
        sb.append('}');
        return sb.toString();
    }
}
