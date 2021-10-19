package pl.derjack.yabreakthrough.cpu;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import pl.derjack.yabreakthrough.game.Action;
import pl.derjack.yabreakthrough.game.Game;

public class Cpu {
    private int player;
    private Game game;
    private Evaluator evaluator;
    private Random random;

    private long start;
    private long duration;
    private volatile boolean cancelled;

    public int steps;

    private Map<Long,TTEntry> tt = new HashMap<>();
    private Map<Integer,Action> killerMoves = new HashMap<>();

    public Cpu() {
        this(new SimpleEvaluator());
    }

    public Cpu(Evaluator evaluator) {
        this.random = new Random();
        this.evaluator = evaluator;
        this.game = new Game();
    }

    public int getPlayer() {
        return player;
    }

    public void setPlayer(int player) {
        this.player = player;
    }

    public void updateGameState(Game game) {
        this.game.setGame(game);
    }

    public Move getBestMove() {
        start = System.currentTimeMillis();
        steps = 0;
        duration = evaluator.getThinkingTimeInMillis();
        cancelled = false;

        List<Action> actions = game.getAvailableMoves();
        List<Move> moves = new ArrayList<>(actions.size());
        for (Action action : actions) {
            moves.add(new Move(action));
        }
        for (Move move : moves) {
            game.makeMove(move.move);
            steps++;
            if (game.isOver()) {
                move.score = game.getWinner() == player ? Evaluator.INF - game.getRounds() : -Evaluator.INF + game.getRounds();
            } else {
                move.score = evaluator.evaluateGame(player, game);
            }
            game.undoMove();
        }

        Move bestMove = getBestMove(moves);
        if (Math.abs(bestMove.score) > Evaluator.FINISH_THRESHOLD) {
            return bestMove;
        }

        killerMoves.clear();
        for (int i=1; i < 42; i++) {
            tt.clear();
            for (Move move : moves) {
                if (Math.abs(move.score) > Evaluator.FINISH_THRESHOLD) {
                    continue;
                }
                game.makeMove(move.move);
                steps++;
                try {
                    move.score = -getScore(-1, i-1, -Evaluator.INF, Evaluator.INF);
                } catch (TimeoutException e) {
                    game.undoMove();
                    Log.d("dupa.cycki", "level "+i);
                    return bestMove;
                }
                game.undoMove();
                if (move.score > Evaluator.FINISH_THRESHOLD) {
                    return move;
                }
                if (move.score > bestMove.score) {
                    bestMove = move;
                }
            }
            bestMove = getBestMove(moves);
            if (Math.abs(bestMove.score) > Evaluator.FINISH_THRESHOLD) {
                break;
            }
        }

        return bestMove;
    }

    private Move getBestMove(List<Move> moves) {
        Collections.sort(moves);
        Collections.reverse(moves);

        int maxScore = moves.get(0).score;
        int n = 0;
        for (Move move : moves) {
            if (move.score < maxScore) {
                break;
            }
            n++;
        }

        return moves.get(random.nextInt(n));
    }

    private static final Comparator<Action> comparator = new Comparator<Action>() {
        @Override
        public int compare(Action o1, Action o2) {
            if (o1.capture == o2.capture) {
                return Integer.compare(o2.row, o1.row);
            }
            return Boolean.compare(o2.capture, o1.capture);
        }
    };

    private int getScore(int color, int depth, int alpha, int beta) throws TimeoutException {
        if (depth > 0) {
            if (cancelled || System.currentTimeMillis() - start >= duration) {
                throw new TimeoutException();
            }
        }

        int alphaOrig = alpha;

        long hash = 0L;
        if (depth > 0) {
            hash = game.getHash();
            if (tt.containsKey(hash)) {
                TTEntry ttEntry = tt.get(hash);
                if (ttEntry.flag == TTEntry.EXACT) {
                    return ttEntry.value;
                } else if (ttEntry.flag == TTEntry.LOWER) {
                    alpha = Math.max(alpha, ttEntry.value);
                } else {
                    beta = Math.min(beta, ttEntry.value);
                }
                if (alpha >= beta) {
                    return ttEntry.value;
                }
            }
        }

        int output = -Evaluator.INF;
        List<Action> moves = game.getAvailableMoves();
        if (depth > 0) {
            Collections.sort(moves,comparator);
        }
        Action killerMove = killerMoves.get(game.getRounds());
        if (killerMove != null && moves.contains(killerMove)) {
            Collections.swap(moves, moves.indexOf(killerMove), 0);
        }

        Action bestMove = null;
        for (Action move : moves) {
            int score;
            game.makeMove(move);
            steps++;
            if (game.isOver()) {
                score = game.getWinner() == player ? Evaluator.INF - game.getRounds() : -Evaluator.INF + game.getRounds();
                score *= color;
            } else if (depth > 0) {
                try {
                    score = -getScore(-color, depth-1, -beta, -alpha);
                } catch (TimeoutException e) {
                    game.undoMove();
                    throw e;
                }
            } else {
                score = color * evaluator.evaluateGame(player, game);
            }
            game.undoMove();
            if (score > output) {
                bestMove = move;
            }
            output = Math.max(score, output);
            alpha = Math.max(output, alpha);
            if (alpha >= beta) {
                break;
            }
        }

        killerMoves.put(game.getRounds(),bestMove);

        if (depth > 0) {
            TTEntry ttEntry = new TTEntry();
            ttEntry.value = output;

            if (output <= alphaOrig) {
                ttEntry.flag = TTEntry.UPPER;
            } else if (output >= beta) {
                ttEntry.flag = TTEntry.LOWER;
            } else {
                ttEntry.flag = TTEntry.EXACT;
            }

            tt.put(hash,ttEntry);
        }

        return output;
    }

    public void cancel() {
        cancelled = true;
    }

}
