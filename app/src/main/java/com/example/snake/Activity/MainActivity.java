package com.example.snake.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.snake.R;
import com.example.snake.Backer.ScreenUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final int START_ACTIVITY = 1;
    private final int SETTING_ACTIVITY = 2;

    private SharedPreferences spRank;
    private boolean Bgm = true;
    private boolean difficulty = false;
    private boolean controlMode_CK;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences getSet = getSharedPreferences("setting", MODE_PRIVATE);
        Bgm = getSet.getBoolean("Bgm", Bgm);
        difficulty = getSet.getBoolean("difficulty", difficulty);
        controlMode_CK = getSet.getBoolean("controlMode_CK", true);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ScreenUtils.init(getApplicationContext());
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        ActivityCollector.addActivity(this);

        Button start = (Button) findViewById(R.id.start);
        Button rankList = (Button) findViewById(R.id.rankList);
        Button setting = (Button) findViewById(R.id.setting);
        Button help = (Button) findViewById(R.id.help);
        start.setOnClickListener(this);
        rankList.setOnClickListener(this);
        setting.setOnClickListener(this);
        help.setOnClickListener(this);

        spRank = getSharedPreferences("RankList", MODE_PRIVATE);
        if (Bgm) {
            startService(new Intent(MainActivity.this, BGMService.class));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start:
                Intent intentStart = new Intent(MainActivity.this, SnakeActivity.class);
                intentStart.putExtra("controlMode_CK", controlMode_CK);
                intentStart.putExtra("difficulty", difficulty);
                startActivityForResult(intentStart, START_ACTIVITY);
                break;
            case R.id.rankList:
                AlertDialog.Builder dialogRankList = new AlertDialog.Builder(MainActivity.this);
                dialogRankList.setTitle("排行榜");
                dialogRankList.setIcon(R.drawable.blacksnake);
                int s1 = spRank.getInt("score1", 0);
                int s2 = spRank.getInt("score2", 0);
                int s3 = spRank.getInt("score3", 0);
                String str1 = "NO.1 : " + s1;
                String str2 = "NO.2 : " + s2;
                String str3 = "NO.3 : " + s3;
                dialogRankList.setMessage(str1 + "\n" + str2 + "\n" + str3 + "\n");
                dialogRankList.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                dialogRankList.setPositiveButton("重置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences.Editor editor = getSharedPreferences("RankList", MODE_PRIVATE).edit();
                        editor.clear();
                        editor.apply();
                        Toast.makeText(MainActivity.this, "排行榜已经清空!", Toast.LENGTH_SHORT).show();
                    }
                });
                dialogRankList.show();
                break;
            case R.id.setting:
                Intent intentSetting = new Intent(MainActivity.this, SettingActivity.class);
                intentSetting.putExtra("Bgm", Bgm);
                intentSetting.putExtra("controlMode_CK", controlMode_CK);
                intentSetting.putExtra("difficulty", difficulty);
                startActivityForResult(intentSetting, SETTING_ACTIVITY);
                break;
            case R.id.help:
                helpDialog();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(MainActivity.this, BGMService.class));
        ActivityCollector.removeActivity(this);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setTitle("确认退出吗")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击确认后的操作
                        ActivityCollector.finishAll();
                    }
                })
                .setNegativeButton("返回", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //点击“返回”后的操作，这里不设置任何操作
                    }
                }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case START_ACTIVITY:
                if (resultCode == RESULT_OK) {
                    updateRank(data.getIntExtra("score", 0), data.getBooleanExtra("win", false));
                }
                break;
            case SETTING_ACTIVITY:
                if (resultCode == RESULT_OK) {
                    Bgm = data.getBooleanExtra("Bgm", Bgm);
                    difficulty = data.getBooleanExtra("difficulty", difficulty);
                    controlMode_CK = data.getBooleanExtra("controlMode_CK", controlMode_CK);
                    SharedPreferences.Editor editor = getSharedPreferences("setting", MODE_PRIVATE).edit();
                    editor.putBoolean("difficulty", difficulty);
                    editor.putBoolean("Bgm", Bgm);
                    editor.putBoolean("controlMode_CK", controlMode_CK);
                    editor.apply();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void updateRank(int score, boolean win) {
        int s1 = spRank.getInt("score1", 0);
        int s2 = spRank.getInt("score2", 0);
        int s3 = spRank.getInt("score3", 0);
        SharedPreferences.Editor editor = spRank.edit();
        String str;
        if (score > s1) {
            editor.putInt("score3", s2);
            editor.putInt("score2", s1);
            editor.putInt("score1", score);
            if (win) {
                str = "恭喜您，已经达成通关成就，厉害咯~";
            } else {
                str = "恭喜您，以 " + score + "分 的成绩取得排行榜第一名的成绩，厉害咯~";
            }
        } else if (score > s2) {
            editor.putInt("score3", s2);
            editor.putInt("score2", score);
            str = "恭喜您，以 " + score + "分 的成绩取得排行榜第二名的成绩，厉害咯~";
        } else if (score > s3) {
            editor.putInt("score3", score);
            str = "恭喜您，以 " + score + "分 的成绩取得排行榜第三名的成绩，厉害咯~";
        } else {
            str = "垃圾，你没上榜！";
            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
            dialog.setTitle("成绩");
            dialog.setIcon(R.drawable.blacksnake);
            String str1 = "NO.1 : " + s1;
            String str2 = "NO.2 : " + s2;
            String str3 = "NO.3 : " + s3;
            dialog.setMessage(str1 + "\n" + str2 + "\n" + str3 + "\n\n" + str);
            dialog.setCancelable(false);
            dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            dialog.show();
            return;
        }
        editor.apply();
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle("成绩");
        dialog.setIcon(R.drawable.redsnake);
        s1 = spRank.getInt("score1", 0);
        s2 = spRank.getInt("score2", 0);
        s3 = spRank.getInt("score3", 0);
        String str1 = "NO.1 : " + s1;
        String str2 = "NO.2 : " + s2;
        String str3 = "NO.3 : " + s3;
        dialog.setMessage(str1 + "\n" + str2 + "\n" + str3 + "\n\n" + str);
        dialog.setCancelable(false);
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dialog.show();
    }

    private void helpDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle("< 游戏规则 >");
        dialog.setIcon(R.drawable.blacksnake);
        String s1 = "\n";
        String s2 = "1.玩家可以在游戏设置中选择操控模式，使用触控或重力感应控制；\n";
        String s3 = "2.玩家可以在游戏设置中选择游戏难度，进入困难模式或简单模式；\n";
        String s4 = "3.游戏结束后自动计算游戏得分，更新排行榜；\n";
        String s5 = "4.玩家可以在排行榜中选择重置游戏排行榜。\n";
        dialog.setMessage(s1 + s2 + s3 + s4 + s5);
        dialog.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        dialog.show();
    }
}