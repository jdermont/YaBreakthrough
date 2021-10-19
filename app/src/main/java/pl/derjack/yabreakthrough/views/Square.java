package pl.derjack.yabreakthrough.views;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class Square {
    private static final int COLOR_HILIGHT = 0xff00ff00;
    private static final int COLOR_LIGHT = 0xffffce9e;
    private static final int COLOR_DARK = 0xffd18b47;
    private static final int COLOR_LAST_MOVE = 0x4000ff00;
    private static final int COLOR_BLACK = 0xff000000;
    private static final int COLOR_CIRCLE = 0xff00ff00;

    public int row;
    public int col;
    public boolean hilighted;
    public boolean targeted;
    public boolean lastMove;

    private int block;
    private int xMargin, yMargin;
    private Rect rect;
    private Bitmap piece;

    public Square(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getX() {
        return col * block;
    }

    public int getY() {
        return (7-row) * block;
    }

    public Bitmap getPiece() {
        return piece;
    }

    public void setPiece(Bitmap piece) {
        this.piece = piece;
    }

    public void draw(Canvas canvas, Paint paint) {
        paint.setStyle(Paint.Style.FILL);
        if (hilighted) {
            paint.setColor(COLOR_HILIGHT);
        } else {
            paint.setColor((row+col)%2 == 0 ? COLOR_DARK : COLOR_LIGHT);
        }
        canvas.drawRect(rect, paint);
        if (lastMove) {
            paint.setColor(COLOR_LAST_MOVE);
            canvas.drawRect(rect, paint);
        }
        if (piece != null) {
            paint.setColor(COLOR_BLACK);
            canvas.drawBitmap(piece, null, rect, paint);
        }
        if (targeted) {
            paint.setColor(COLOR_CIRCLE);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(xMargin+getX()+block/2, yMargin+getY()+block/2, block/8, paint);
            paint.setColor(COLOR_BLACK);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(xMargin+getX()+block/2, yMargin+getY()+block/2, block/8, paint);
        }
    }

    public void onSizeChanged(int block, int xMargin, int yMargin) {
        this.block = block;
        this.xMargin = xMargin;
        this.yMargin = yMargin;
        rect = new Rect(xMargin + getX(), yMargin + getY(), xMargin + getX() + block, yMargin + getY() + block);
    }
}
