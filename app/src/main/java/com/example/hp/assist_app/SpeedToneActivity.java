package com.example.hp.assist_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

public class SpeedToneActivity extends AppCompatActivity implements View.OnClickListener {

    private LocalBroadcastManager localBroadcastManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.speed_tone_activity);

        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
        int speed = pref.getInt("speakSpeed", 0);
        int tone = pref.getInt("speakTone", 0);
        String currentSpeaker = pref.getString("currentSpeaker", "");

        ImageButton back = (ImageButton) findViewById(R.id.back);
        back.setOnClickListener(this);
        LinearLayout help = (LinearLayout) findViewById(R.id.help);
        help.setOnClickListener(this);
        TextView current_speaker = (TextView) findViewById(R.id.speaker);
        TextView current_language = (TextView) findViewById(R.id.current_language);
        SeekBar speedBar = (SeekBar) findViewById(R.id.speed_bar);
        SeekBar toneBar = (SeekBar) findViewById(R.id.tone_bar);
        current_speaker.setText(currentSpeaker);
        speedBar.setProgress(speed);
        toneBar.setProgress(tone);
        final SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
        speedBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                editor.putInt("speakSpeed", i);
                editor.apply();
                Intent mIntent = new Intent();
                mIntent.setAction("com.example.hp.SpeedBoardReceiver");
                mIntent.putExtra("speedValue", i / 10 + "");
                localBroadcastManager.sendBroadcast(mIntent);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        toneBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                editor.putInt("speakTone", i);
                editor.apply();
                Intent mIntent = new Intent();
                mIntent.setAction("com.example.hp.ToneBoardReceiver");
                mIntent.putExtra("toneValue", i / 10 + "");
                localBroadcastManager.sendBroadcast(mIntent);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    @Override
    public void onClick(View view) {
        SeekBar speed_bar = (SeekBar) findViewById(R.id.speed_bar);
        SeekBar tone_bar = (SeekBar) findViewById(R.id.tone_bar);
        switch (view.getId()) {
            case R.id.reset_speech_rate:
                speed_bar.setProgress(50);
                Intent resetSpeedIntent = new Intent();
                resetSpeedIntent.setAction("com.example.hp.ResetSpeedBoardReceiver");
                resetSpeedIntent.putExtra("resetSpeedValue", 5 + "");
                localBroadcastManager.sendBroadcast(resetSpeedIntent);
                break;
            case R.id.reset_tone:
                tone_bar.setProgress(50);
                Intent resetToneIntent = new Intent();
                resetToneIntent.setAction("com.example.hp.ResetToneBoardReceiver");
                resetToneIntent.putExtra("resetToneValue", 5 + "");
                localBroadcastManager.sendBroadcast(resetToneIntent);
                break;
            case R.id.example:
                Intent playExample = new Intent();
                playExample.setAction("com.example.hp.PlayExample");
                playExample.putExtra("example", "这是当前的朗读效果，快去使用听字吧");
                localBroadcastManager.sendBroadcast(playExample);
                break;
            case R.id.back:
                finish();
                break;
            case R.id.help:
                Log.e("speedTone", "clicked");
                Intent it = new Intent(SpeedToneActivity.this, YingDao_Ysyd.class);
                startActivity(it);
                break;
        }
    }
}
