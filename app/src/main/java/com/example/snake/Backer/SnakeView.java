package com.example.snake.Backer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.snake.Activity.SnakeActivity;
import com.example.snake.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SnakeView extends View {
    private final int DIRECTION_L = 0x1001;
    private final int DIRECTION_T = 0x0011;
    private final int DIRECTION_R = 0x0110;
    private final int DIRECTION_B = 0x1100;

    private final int CANVAS_REFRESH_INTERVAL = 300;
    private final int CANVAS_REFRESH_INTERVAL_DIFFICULTY = 150;

    private final int APPLE_STROKE_WIDTH = 50;
    private final int SNAKE_STROKE_WIDTH = 50;

    private final int MOVE_DISTANCE = 50;

    private final Paint applePaint = new Paint();
    private final Paint snakePaint = new Paint();
    private final Paint textPaint = new Paint();
    private final Paint borderPaint = new Paint();

    private final Point rat = new Point();
    private final List<Point> snakeBody = new ArrayList<>();

    public int currDirection;
    public int lastDirection;

    private int hCount;
    private int vCount;

    int width;
    int height;

    private int score;     //记录分数
    private boolean appleGone = false;  //果实上一步有没有移动
    private int hBorder;   //游戏水平边界
    private int vBorder;   //游戏垂直边界
    private boolean gameOver = false;  //游戏结束

    private boolean controlMode_CK = true;
    private boolean difficulty = false; //难度选择

    private SnakeActivity snakeActivity;  //绑定SnakeActivity对象

    private Random random = new Random();

    public void setSnakeActivity(SnakeActivity snakeActivity) {
        this.snakeActivity = snakeActivity;
    }

    public void setControlMode_CK(boolean controlMode_CK) {
        this.controlMode_CK = controlMode_CK;
    }

    public void setDifficulty(boolean difficulty) {
        this.difficulty = difficulty;
    }

    private final Runnable refresh = new Runnable() {
        @Override
        public void run() {
            updateSnake();
            SnakeView.this.invalidate();
        }
    };

    public SnakeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SnakeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        lastDirection = DIRECTION_R;
        currDirection = DIRECTION_R;

        int temp = random.nextInt(3);
        switch (temp) {
            case 0:
                currDirection = DIRECTION_T;
                break;
            case 1:
                currDirection = DIRECTION_R;
                break;
            case 2:
                currDirection = DIRECTION_B;
                break;
        }

        initBound();
        initPaint();
        initSnake();
        initRat();
    }

    private void initBound() {
        width = ScreenUtils.getScreenWidth(getContext());
        height = ScreenUtils.getScreenHeight(getContext());
        hCount = width / MOVE_DISTANCE;
        vCount = height / MOVE_DISTANCE;
        hBorder = (width - hCount * MOVE_DISTANCE) / 2;
        vBorder = (height - vCount * MOVE_DISTANCE) / 2;
    }

    private void initPaint() {
        applePaint.setAntiAlias(true);
        applePaint.setColor(Color.RED);
        applePaint.setStyle(Paint.Style.FILL);
        applePaint.setStrokeWidth(APPLE_STROKE_WIDTH);

        snakePaint.setAntiAlias(true);
        snakePaint.setColor(Color.rgb(38, 163, 130));
        snakePaint.setStyle(Paint.Style.FILL);
        snakePaint.setStrokeWidth(SNAKE_STROKE_WIDTH);

        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.WHITE);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setStrokeWidth(2);
        textPaint.setTextSize(MOVE_DISTANCE);

        borderPaint.setAntiAlias(true);
        borderPaint.setColor(Color.BLACK);
        borderPaint.setStyle(Paint.Style.STROKE);
    }

    private void initSnake() {
        score = 0;

        Point head = new Point(300 + hBorder, 500 + vBorder);
        Point body = new Point(250 + hBorder, 500 + vBorder);
        Point tail = new Point(200 + hBorder, 500 + vBorder);
        snakeBody.clear();
        snakeBody.add(head);
        snakeBody.add(body);
        snakeBody.add(tail);
    }

    private void initRat() {
        updateRat();
    }

    private void updateSnake() {
        Point head = snakeBody.get(0);
        Point temp = new Point(head);
        switch (currDirection) {
            case DIRECTION_L:
                temp.x -= MOVE_DISTANCE;
                break;
            case DIRECTION_T:
                temp.y -= MOVE_DISTANCE;
                break;
            case DIRECTION_R:
                temp.x += MOVE_DISTANCE;
                break;
            case DIRECTION_B:
                temp.y += MOVE_DISTANCE;
                break;
        }
        if (snakeBody.contains(temp)) {
            //蛇的头部撞到了自己，GAME OVER!
            gameOver = true;
            return;
        }
        if (temp.x < hBorder + MOVE_DISTANCE || temp.x > MOVE_DISTANCE * hCount + hBorder - MOVE_DISTANCE
                || temp.y < vBorder + MOVE_DISTANCE || temp.y > MOVE_DISTANCE * vCount + vBorder - MOVE_DISTANCE) {
            //蛇的头部撞到了墙，GAME OVER!
            gameOver = true;
            return;
        }

        snakeBody.add(0, temp);
        lastDirection = currDirection;

        if (snakeBody.contains(rat)) {
            score = score + 1;
            updateRat();
        } else {
            snakeBody.remove(snakeBody.size() - 1);
            ratRun();
        }
    }

    private void updateRat() {
        int x = (random.nextInt(hCount - 2)) * MOVE_DISTANCE + hBorder + MOVE_DISTANCE;
        int y = (random.nextInt(vCount - 2)) * MOVE_DISTANCE + vBorder + MOVE_DISTANCE;
        rat.set(x, y);
        if (snakeBody.contains(rat)) {
            updateRat();
        }
    }

    public void ratRun() {
        if (appleGone) {
            appleGone = false;
        } else {
            appleGone = true;
            int x = rat.x;
            int y = rat.y;
            int temp = random.nextInt(4);
            switch (temp) {
                case 0:
                    x -= MOVE_DISTANCE;
                    break;
                case 1:
                    y -= MOVE_DISTANCE;
                    break;
                case 2:
                    x += MOVE_DISTANCE;
                    break;
                case 3:
                    y += MOVE_DISTANCE;
                    break;
                default:
                    break;
            }
            if (!snakeBody.contains(new Point(x, y)) && x > hBorder + MOVE_DISTANCE && x < hCount * MOVE_DISTANCE + hBorder - MOVE_DISTANCE
                    && y > vBorder + MOVE_DISTANCE && y < vCount * MOVE_DISTANCE + vBorder - MOVE_DISTANCE) {
                rat.set(x, y);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制游戏界面边框
        borderPaint.setStrokeWidth(MOVE_DISTANCE + vBorder);
        canvas.drawLine(0, 0, 0, height, borderPaint);  //左边框
        canvas.drawLine(width, 0, width, height, borderPaint);   //右边框
        borderPaint.setStrokeWidth(MOVE_DISTANCE + hBorder);
        canvas.drawLine(0, 0, width, 0, borderPaint);   //上边框
        canvas.drawLine(0, height, width, height, borderPaint);   //下边框

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inDensity = getResources().getDisplayMetrics().densityDpi;
        opts.inTargetDensity = getResources().getDisplayMetrics().densityDpi;

        int index = 0;
        for (Point point : snakeBody) {
            //蛇头
            if (index == 0) {
                Bitmap sHead = BitmapFactory.decodeResource(getResources(), R.drawable.snakehead, opts);
                int wHead = sHead.getWidth();
                int hHead = sHead.getHeight();
                Matrix matrix = new Matrix();
                switch (lastDirection) {
                    case DIRECTION_L:
                        matrix.postRotate(90);
                        break;
                    case DIRECTION_T:
                        matrix.postRotate(180);
                        break;
                    case DIRECTION_R:
                        matrix.postRotate(270);
                        break;
                    case DIRECTION_B:
                        matrix.postRotate(0);
                        break;
                }
                Bitmap newHead = Bitmap.createBitmap(sHead, 0, 0, wHead, hHead, matrix, true);
                canvas.drawBitmap(newHead, new Rect(0, 0, wHead, hHead), new Rect(point.x - 25, point.y - 25, point.x + 25, point.y + 25), snakePaint);
            } else {
                canvas.drawCircle(point.x, point.y, 23, snakePaint);
            }
            index++;
        }
        Bitmap ratBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.rat, opts);
        int wRat = ratBitmap.getWidth();
        int hRat = ratBitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postRotate(0);
        Bitmap newRat = Bitmap.createBitmap(ratBitmap, 0, 0, wRat, hRat, matrix, true);
        canvas.drawBitmap(newRat, new Rect(0, 0, wRat, hRat), new Rect(rat.x - 25, rat.y - 25, rat.x + 25, rat.y + 25), applePaint);

        String str = String.valueOf(score);
        canvas.drawText("得分：" + str, 50, 50, textPaint);

        if (gameOver) {
            this.removeCallbacks(refresh);
            boolean win = false;
            if (score == (hCount - 2) * (vCount - 2) - 3) {
                win = true;
            }
            snakeActivity.gameOver(score, win);
        } else {
            if (difficulty) {
                this.postDelayed(refresh, CANVAS_REFRESH_INTERVAL_DIFFICULTY);
            } else {
                this.postDelayed(refresh, CANVAS_REFRESH_INTERVAL);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_DOWN || !controlMode_CK) {
            return super.onTouchEvent(event);
        }
        float x = event.getX();
        float y = event.getY();
        int snakeX = snakeBody.get(0).x;
        int snakeY = snakeBody.get(0).y;
        if (Math.abs(x - snakeX) >= Math.abs(y - snakeY)) {
            if (x > snakeX) {
                currDirection = DIRECTION_R;
            } else {
                currDirection = DIRECTION_L;
            }
        } else {
            if (y > snakeY) {
                currDirection = DIRECTION_B;
            } else {
                currDirection = DIRECTION_T;
            }
        }
        if ((currDirection & lastDirection) == 0) {
            currDirection = lastDirection;
        }
        return super.onTouchEvent(event);
    }
}