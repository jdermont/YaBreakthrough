package pl.derjack.yabreakthrough.game;

public class Action {
    public long move;
    public int player;
    public boolean capture;
    public int row;

    public Action(long move, int player, boolean capture, int row) {
        this.move = move;
        this.player = player;
        this.capture = capture;
        this.row = row;
    }

    // BoardView specific
    public static Action createAction(int rowFrom, int colFrom, int rowTo, int colTo) {
        long from = 1L << (8*rowFrom + (7-colFrom));
        long to = 1L << (8*rowTo + (7-colTo));
        return new Action(from|to, -1, false, -1);
    }

    public int[] toCoords() {
        int i = 0;
        while ((move & (1L<<i)) == 0) {
            i++;
        }
        int colFrom = 7 - i % 8; // messy; 7 - for horizontal flip
        int rowFrom = i / 8;
        i++;
        while ((move & (1L<<i)) == 0) {
            i++;
        }
        int colTo = 7 - i % 8;
        int rowTo = i / 8;

        if (player == Board.ONE) {
            return new int[] { rowFrom, colFrom, rowTo, colTo };
        } else {
            return new int[] { rowTo, colTo, rowFrom, colFrom };
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Action)) {
            return false;
        }
        Action action = (Action) o;
        return move == action.move;
    }

    @Override
    public int hashCode() {
        return (int)((move >>> 32) ^ move);
    }

    @Override
    public String toString() {
        int i = 0;
        while ((move & (1L<<i)) == 0) {
            i++;
        }
        char c = (char)((7 - i % 8) + 'a'); // messy; 7 - for horizontal flip
        char r = (char)((i / 8) + '1');
        i++;
        while ((move & (1L<<i)) == 0) {
            i++;
        }
        char c2 = (char)((7 - i % 8) + 'a');
        char r2 = (char)((i / 8) + '1');
        StringBuilder sb = new StringBuilder();
        if (player == Board.ONE) {
            sb.append(c).append(r);
            sb.append(c2).append(r2);
        } else {
            sb.append(c2).append(r2);
            sb.append(c).append(r);
        }
        return sb.toString();
    }
}
