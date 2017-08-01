package com.example.snake.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.snake.Backer.ActivityCollector;
import com.example.snake.Backer.BGMService;
import com.example.snake.Backer.ScreenUtils;
import com.example.snake.R;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {
    private boolean Bgm;
    private boolean difficulty;
    private boolean controlMode_CK;
    private String strSound;
    private String strDifficulty;
    private String strControlMode;

    private Button bgmButton;
    private Button difficultyButton;
    private Button controlModeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ScreenUtils.init(getApplicationContext());
        setContentView(R.layout.activity_setting);

        ActivityCollector.addActivity(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        Intent intent = getIntent();
        Bgm = intent.getBooleanExtra("Bgm", true);
        difficulty = intent.getBooleanExtra("difficulty", true);
        controlMode_CK = intent.getBooleanExtra("controlMode_CK", true);
        if (Bgm) {
            strSound = "开启";
        } else {
            strSound = "关闭";
        }
        if (difficulty) {
            strDifficulty = "困难";
        } else {
            strDifficulty = "简单";
        }
        if (controlMode_CK) {
            strControlMode = "触屏控制";
        } else {
            strControlMode = "重力感应";
        }

        bgmButton = (Button) findViewById(R.id.setting_sound);
        bgmButton.setText(strSound);
        bgmButton.setOnClickListener(this);

        difficultyButton = (Button) findViewById(R.id.setting_difficulty);
        difficultyButton.setText(strDifficulty);
        difficultyButton.setOnClickListener(this);

        controlModeButton = (Button) findViewById(R.id.setting_controlMode_CK);
        controlModeButton.setText(strControlMode);
        controlModeButton.setOnClickListener(this);

        Button saveButton = (Button) findViewById(R.id.setting_save);
        saveButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_sound:
                if (Bgm) {
                    stopService(new Intent(SettingActivity.this, BGMService.class));
                    Bgm = false;
                    strSound = "关闭";
                    Toast.makeText(this,"声音已经关了哦~",Toast.LENGTH_SHORT).show();
                } else {
                    startService(new Intent(SettingActivity.this, BGMService.class));
                    Bgm = true;
                    strSound = "开启";
                    Toast.makeText(this,"声音已经开了哦~",Toast.LENGTH_SHORT).show();
                }
                bgmButton.setText(strSound);
                break;
            case R.id.setting_difficulty:
                if (!difficulty) {
                    difficulty = true;
                    strDifficulty = "困难";
                    Toast.makeText(this,"现在是困难模式哦~",Toast.LENGTH_SHORT).show();
                } else {
                    difficulty = false;
                    strDifficulty = "简单";
                    Toast.makeText(this,"现在是简单模式哦~",Toast.LENGTH_SHORT).show();
                }
                difficultyButton.setText(strDifficulty);
                break;
            case R.id.setting_controlMode_CK:
                if (controlMode_CK) {
                    controlMode_CK = false;
                    strControlMode = "重力感应";
                    Toast.makeText(this,"要使用重力感应控制哦~",Toast.LENGTH_SHORT).show();
                } else {
                    controlMode_CK = true;
                    strControlMode = "触屏操控";
                    Toast.makeText(this,"要使用触屏控制哦~",Toast.LENGTH_SHORT).show();
                }
                controlModeButton.setText(strControlMode);
                break;
            case R.id.setting_save:
                Intent intent = new Intent();
                intent.putExtra("Bgm", Bgm);
                intent.putExtra("difficulty", difficulty);
                intent.putExtra("controlMode_CK", controlMode_CK);
                setResult(RESULT_OK, intent);
                ActivityCollector.removeActivity(this);
            default:
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("Bgm", Bgm);
        intent.putExtra("difficulty", difficulty);
        intent.putExtra("controlMode_CK", controlMode_CK);
        setResult(RESULT_OK, intent);
        ActivityCollector.removeActivity(this);
    }
}
