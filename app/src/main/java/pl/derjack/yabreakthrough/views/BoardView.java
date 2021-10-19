package pl.derjack.yabreakthrough.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.Nullable;

import java.util.List;

import pl.derjack.yabreakthrough.R;
import pl.derjack.yabreakthrough.game.Game;

public class BoardView extends View {
    private static final String TAG = "BoardView";
    private static final int N = 8;

    private Square[][] squares;
    private Bitmap whitePiece;
    private Bitmap blackPiece;
    private Paint paint;

    private int block;
    private int xMargin, yMargin;
    private Game game;

    private Square hilightedSquare;
    private ValueAnimator valueAnimator;
    private Bitmap animatedPiece;
    private Rect animatedRect;

    private BoardListener boardListener;

    public BoardView(Context context) {
        super(context);
        init();
    }

    public BoardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BoardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setBoardListener(BoardListener boardListener) {
        this.boardListener = boardListener;
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        animatedRect = new Rect();
        whitePiece = BitmapFactory.decodeResource(getResources(), R.drawable.pawn_white);
        blackPiece = BitmapFactory.decodeResource(getResources(), R.drawable.pawn_black);
        squares = new Square[N][N];
        for (int row = N-1; row >= 0; row--) {
            for (int col = 0; col < N; col++) {
                squares[row][col] = new Square(row,col);
            }
        }
        setBackgroundColor(0xff7f7f7f);
        refreshBoardState();
    }

    public void setGame(Game game) {
        this.game = game;
        refreshBoardState();
    }

    private void refreshBoardState() {
        hilightedSquare = null;
        for (int row = N-1; row >= 0; row--) {
            for (int col = 0; col < N; col++) {
                squares[row][col].hilighted = false;
                squares[row][col].targeted = false;
                squares[row][col].lastMove = false;
                if (game == null) {
                    squares[row][col].setPiece(null);
                } else {
                    int pawn = game.getPawn(row, col);
                    squares[row][col].setPiece(pawn == Game.ONE ? whitePiece : pawn == Game.TWO ? blackPiece : null);
                }
            }
        }
        if (game != null) {
            for (int[] lastSquare : game.getLastSquares()) {
                squares[lastSquare[0]][lastSquare[1]].lastMove = true;
            }
        }
        invalidate();
    }

    public void animate(Square from, Square to) {
        animatedPiece = from.getPiece();
        from.setPiece(null);
        PropertyValuesHolder X = PropertyValuesHolder.ofInt("x", xMargin + from.getX(), xMargin + to.getX());
        PropertyValuesHolder Y = PropertyValuesHolder.ofInt("y", yMargin + from.getY(), yMargin + to.getY());
        valueAnimator = new ValueAnimator();
        valueAnimator.setValues(X,Y);
        valueAnimator.setDuration(750L);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int x = (int) animation.getAnimatedValue("x");
                int y = (int) animation.getAnimatedValue("y");
                animatedRect.left = x;
                animatedRect.top = y;
                animatedRect.right = x + block;
                animatedRect.bottom = y + block;
                invalidate();
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (boardListener != null) {
                    boardListener.onAnimationStarted();
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animatedPiece = null;
                refreshBoardState();
                if (boardListener != null) {
                    boardListener.onAnimationFinished();
                }
            }
        });
        valueAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d(TAG,"onDraw");
        super.onDraw(canvas);

        for (Square[] squaresRow : squares) {
            for (Square square : squaresRow) {
                square.draw(canvas, paint);
            }
        }

        if (animatedPiece != null) {
            canvas.drawBitmap(animatedPiece, null, animatedRect, paint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d(TAG,"onMeasure");
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Log.d(TAG,"onSizeChanged "+w+" "+h+" "+oldw+" "+oldh);
        super.onSizeChanged(w, h, oldw, oldh);

        if (w > h) {
            block = h / N;
            xMargin = (w - h) / 2;
            yMargin = (h - N * block) / 2;
        } else {
            block = w / N;
            xMargin = (w - N * block) / 2;
            yMargin = (h - w) / 2;
        }

        for (Square[] squaresRow : squares) {
            for (Square square : squaresRow) {
                square.onSizeChanged(block, xMargin, yMargin);
            }
        }
    }

    public void clickAndAnimate(int rowFrom, int colFrom, int rowTo, int colTo) {
        if (boardListener != null) {
            boardListener.clicked(rowFrom, colFrom, rowTo, colTo);
        }
        Log.d("dupa.cycki",rowFrom+" "+colFrom+" "+rowTo+" "+colTo);
        animate(squares[rowFrom][colFrom],squares[rowTo][colTo]);
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        Log.d(TAG, "onTouchEvent "+motionEvent);
        if (game != null && game.isOver()) {
            return true;
        }
        if (valueAnimator != null && valueAnimator.isRunning()) {
            return true;
        }
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            int col = (int) ((motionEvent.getX() - xMargin) / block);
            int row = (N - 1) - (int) ((motionEvent.getY() - yMargin) / block);
            Log.d(TAG, "onTouchEvent "+col+" "+row);
            if (col >= 0 && col < N && row >= 0 && row < N) {
                final Square square = squares[row][col];
                if (square.targeted) {
                    if (boardListener != null) {
                        boardListener.clicked(hilightedSquare.row, hilightedSquare.col, square.row, square.col);
                    }
                    animate(hilightedSquare,square);
                } else if (square.hilighted) {
                    refreshBoardState();
                } else {
                    int pawn = game.getPawn(row, col);
                    if (pawn == game.getCurrentPlayer()) {
                        List<int[]> targets = game.getTargets(row, col);
                        if (!targets.isEmpty()) {
                            refreshBoardState();
                            hilightedSquare = square;
                            square.hilighted = true;
                            for (int[] target : targets) {
                                squares[target[0]][target[1]].targeted = true;
                            }
                        } else {
                            refreshBoardState();
                        }
                    } else {
                        refreshBoardState();
                    }
                }
                invalidate();
            }
        }
        return true;
    }

    public interface BoardListener {
        void clicked(int rowFrom, int colFrom, int rowTo, int colTo);
        void onAnimationStarted();
        void onAnimationFinished();
    }


}
