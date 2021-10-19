package pl.derjack.yabreakthrough.game;

import java.util.ArrayList;
import java.util.List;

public class Board {
    public static final int NONE = 0;
    public static final int ONE = 1;
    public static final int TWO = 2;

    public static final long LEFT_MASK = 0x7f7f7f7f7f7f7f7fL;
    public static final long RIGHT_MASK = 0xfefefefefefefefeL;
    public static final long TOP_ROW = 0xff00000000000000L;
    public static final long BOTTOM_ROW = 0x00000000000000ffL;

    public long oneBoard;
    public long twoBoard;

    public Board() {
        oneBoard = 65535L;
        twoBoard = 65535L << 48;
    }

    public void setBoard(Board board) {
        this.oneBoard = board.oneBoard;
        this.twoBoard = board.twoBoard;
    }

    public List<Action> getActions(int forPlayer) {
        List<Action> actions = new ArrayList<>();
        if (forPlayer == ONE) {
            for (int i=0; i < 56; i++) {
                long from = oneBoard & (1L<<i);
                if (from != 0) {
                    int row = i/8;
                    long to1 = (from << 7) & LEFT_MASK;
                    if (to1 != 0 && (to1 & oneBoard) == 0) {
                        actions.add(new Action(from|to1, ONE, (to1&twoBoard) != 0, row));
                    }
                    long to2 = (from << 8);
                    if ((to2 & oneBoard) == 0 && (to2 & twoBoard) == 0) {
                        actions.add(new Action(from|to2, ONE, false, row));
                    }
                    long to3 = (from << 9) & RIGHT_MASK;
                    if (to3 != 0 && (to3 & oneBoard) == 0) {
                        actions.add(new Action(from|to3, ONE, (to3&twoBoard) != 0, row));
                    }
                }
            }
        } else {
            for (int i=8; i < 64; i++) {
                long from = twoBoard & (1L<<i);
                if (from != 0) {
                    int row = 7 - i/8;
                    long to1 = (from >>> 7) & RIGHT_MASK;
                    if (to1 != 0 && (to1 & twoBoard) == 0) {
                        actions.add(new Action(from|to1, TWO, (to1&oneBoard) != 0, row));
                    }
                    long to2 = (from >>> 8);
                    if ((to2 & twoBoard) == 0 && (to2 & oneBoard) == 0) {
                        actions.add(new Action(from|to2, TWO, false, row));
                    }
                    long to3 = (from >>> 9) & LEFT_MASK;
                    if (to3 != 0 && (to3 & twoBoard) == 0) {
                        actions.add(new Action(from|to3, TWO, (to3&oneBoard) != 0, row));
                    }
                }
            }
        }
        return actions;
    }

    public void makeAction(Action action, int forPlayer) {
        if (forPlayer == ONE) {
            oneBoard ^= action.move;
            twoBoard &= ~action.move;
        } else {
            twoBoard ^= action.move;
            oneBoard &= ~action.move;
        }
    }

    public void undoAction(Action action, int forPlayer) {
        if (forPlayer == ONE) {
            oneBoard ^= action.move;
            if (action.capture) {
                twoBoard |= action.move&(~oneBoard);
            }
        } else {
            twoBoard ^= action.move;
            if (action.capture) {
                oneBoard |= action.move&(~twoBoard);
            }
        }
    }

    public boolean isOver() {
        return ((oneBoard & TOP_ROW) != 0) || ((twoBoard & BOTTOM_ROW) != 0) || oneBoard == 0L || twoBoard == 0L;
    }


    public int getWinner() {
        if (oneBoard == 0L) return TWO;
        if (twoBoard == 0L) return ONE;
        if ((oneBoard & TOP_ROW) != 0) return ONE;
        return TWO;
    }

    public long getHash() {
        return (123456785L * oneBoard) ^ (9876543216969L * twoBoard);
    }

    // BoardView specific
    public List<Long> getTargets(int index, int forPlayer) {
        List<Long> targets = new ArrayList<>();
        long from = 1L << index;
        if (forPlayer == ONE) {
            long to1 = (from << 7) & LEFT_MASK;
            if (to1 != 0 && (to1 & oneBoard) == 0) {
                targets.add(to1);
            }
            long to2 = (from << 8);
            if ((to2 & oneBoard) == 0 && (to2 & twoBoard) == 0) {
                targets.add(to2);
            }
            long to3 = (from << 9) & RIGHT_MASK;
            if (to3 != 0 && (to3 & oneBoard) == 0) {
                targets.add(to3);
            }
        } else {
            long to1 = (from >>> 7) & RIGHT_MASK;
            if (to1 != 0 && (to1 & twoBoard) == 0) {
                targets.add(to1);
            }
            long to2 = (from >>> 8);
            if ((to2 & twoBoard) == 0 && (to2 & oneBoard) == 0) {
                targets.add(to2);
            }
            long to3 = (from >>> 9) & LEFT_MASK;
            if (to3 != 0 && (to3 & twoBoard) == 0) {
                targets.add(to3);
            }
        }
        return targets;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i=0; i < 64; i++) {
            if ((oneBoard & (Long.MIN_VALUE >>> i)) != 0) {
                sb.append('1');
            } else if ((twoBoard & (Long.MIN_VALUE >>> i)) != 0) {
                sb.append('2');
            } else {
                sb.append('.');
            }
            if ((i+1) % 8 == 0) {
                sb.append('\n');
            }
        }
        return sb.toString();
    }
}