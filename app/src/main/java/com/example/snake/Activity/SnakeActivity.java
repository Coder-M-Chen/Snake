package com.example.snake.Activity;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.PowerManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.snake.Backer.ActivityCollector;
import com.example.snake.R;
import com.example.snake.Backer.ScreenUtils;
import com.example.snake.Backer.SnakeView;

public class SnakeActivity extends AppCompatActivity {

    private SnakeView snakeView;
//    private PowerManager pm=null;
//    private PowerManager.WakeLock wakeLock = null;

    //重力感应
    SensorEventListener lsn = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event == null) {
                return;
            }

            int DIRECTION_L = 0x1001;
            int DIRECTION_T = 0x0011;
            int DIRECTION_R = 0x0110;
            int DIRECTION_B = 0x1100;

            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                float x = event.values[0];
                float y = event.values[1];
                if (Math.abs(x) > 2 || Math.abs(y) > 2) {
                    if (Math.abs(x) > Math.abs(y)) {
                        if (x > 0) {
                            snakeView.currDirection = DIRECTION_B;
                        } else {
                            snakeView.currDirection = DIRECTION_T;
                        }
                    } else {
                        if (y > 0) {
                            snakeView.currDirection = DIRECTION_R;
                        } else {
                            snakeView.currDirection = DIRECTION_L;
                        }
                    }
                    if ((snakeView.currDirection & snakeView.lastDirection) == 0) {
                        snakeView.currDirection = snakeView.lastDirection;
                    }
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO Auto-generated method stub
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ScreenUtils.init(getApplicationContext());
        setContentView(R.layout.activity_snake);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        ActivityCollector.addActivity(this);

        snakeView = (SnakeView) findViewById(R.id.snakeView);
        snakeView.setSnakeActivity(SnakeActivity.this);

        Intent intent = getIntent();
        boolean controlMode_CK = intent.getBooleanExtra("controlMode_CK", true);
        snakeView.setControlMode_CK(controlMode_CK);
        boolean difficulty = intent.getBooleanExtra("difficulty",false);
        snakeView.setDifficulty(difficulty);

        if (!controlMode_CK) {
            //获取重力感应传感器服务
            SensorManager mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            Sensor mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            mSensorManager.registerListener(lsn, mSensor, SensorManager.SENSOR_DELAY_GAME);
        }

        //锁定屏幕，禁止熄屏
//        pm=(PowerManager)getSystemService(POWER_SERVICE);
//        wakeLock = pm.newWakeLock(PowerManager.ON_AFTER_RELEASE,"TAG");
//        wakeLock.acquire();
//
//        if(wakeLock!=null){
//            wakeLock.release();
//        }
    }

    public void gameOver(int score,boolean win) {
        Intent intent = new Intent();
        intent.putExtra("score", score);
        intent.putExtra("win",win);
        setResult(RESULT_OK, intent);
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        ActivityCollector.removeActivity(this);
    }
}
