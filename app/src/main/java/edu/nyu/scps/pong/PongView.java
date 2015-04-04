package edu.nyu.scps.pong;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;


public class PongView extends View {
    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    RectF bounds = new RectF(); //of the PongView
    RectF paddle = new RectF();
    RectF ball = new RectF();
    RectF paddle2 = new RectF();	//the paddle at the right end of the PongView

    //horizontal and vertical motion of ball in pixels per 1/60 seconds
    float dx = 6;  //positive number moves to right
    float dy = 6;  //positive number moves down
    int player1 = 0;
    int player2 = 0;

    public PongView(Context context) {
        super(context);
        setBackgroundColor(Color.BLACK);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);    //vs. STROKE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            //Turn off hardware acceleration,
            //because it would not call onDraw as often as it should.
            setLayerType(LAYER_TYPE_SOFTWARE, paint);
        }

        setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                int n = motionEvent.getPointerCount();  //number of fingers touching right now
                for (int i = 0; i < n; ++i) {
                    if (motionEvent.getX(i) < getWidth() / 2) {
                        //Finger number i touched left side of PongView.
                        paddle.offsetTo(paddle.left, motionEvent.getY(i) - paddle.height() / 2);
                    } else {
                        //Finger number i touched right side of PongView.
                        paddle2.offsetTo(paddle2.left, motionEvent.getY(i) - paddle2.height() / 2);
                    }
                }

                invalidate();
                return true;
            }

        });

        Runnable runnable = new Runnable() {

            //This method is run by a thread that is not the UI thread.
            @Override
            public void run() {

                for (; ; ) {  //infinite loop
                    //Where the ball would be if its horizontal motion were allowed
                    //to continue for another 1/60th of a second.
                    RectF horizontal = new RectF(ball);
                    horizontal.offset(dx, 0f);

                    //Where the ball would be if its vertical motion were allowed
                    //to continue for another 1/60th of a second.
                    RectF vertical = new RectF(ball);
                    vertical.offset(0f, dy);

                    // added player1 and player2 counter
                    if (!bounds.contains(horizontal)) {
                        if (dx > getWidth() / 2) {
                            player2 = +player2;

                        }

                        if (dx < getWidth() / 2) {
                            player1 = +player1;
                        }
                        if (!bounds.contains(vertical)) {
                            dy = -dy;
                        }
                    }
                    //If the ball is not currently embedded in the paddle,
                    if (!RectF.intersects(ball, paddle)) {

                        //but will be embedded on its next move,
                        if (RectF.intersects(horizontal, paddle)) {
                            dx = -dx;
                        }

                        if (RectF.intersects(vertical, paddle)) {
                            dy = -dy;
                        }
                    }

                    if (!RectF.intersects(ball, paddle2)) {

                        //but will be embedded on its next move,
                        if (RectF.intersects(horizontal, paddle2)) {
                            dx = -dx;
                        }

                        if (RectF.intersects(vertical, paddle2)) {
                            dy = -dy;
                        }
                    }


                    ball.offset(dx, dy);
                    postInvalidate(); //Have UI thread call onDraw.


                    //Sleep for 1/60 of a second.
                    try {
                        Thread.sleep(1000L / 60L);   //milliseconds
                    } catch (InterruptedException interruptedException) {
                    }
                }
            }
        };

        Thread thread = new Thread(runnable);   //a thread that is not the UI thread
        thread.start();   //The thread will execute the run method of the Runnable object.

    }
            @Override
            protected void onLayout(boolean changed, int l, int t, int r, int b) {
                bounds.set(0f, 0f, getWidth(), getHeight());
                int w = getWidth() / 40;
                paddle.set(2 * w, 5 * w, 3 * w, 10 * w);
                paddle2.set(getWidth() - 3 * w, 5 * w, getWidth() - 2 * w, 10 * w);
                ball.set(0, 0, w, w);
            }

            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                canvas.drawRect(paddle, paint);
                canvas.drawRect(paddle2, paint);
                canvas.drawRect(ball, paint);
            }
        }

