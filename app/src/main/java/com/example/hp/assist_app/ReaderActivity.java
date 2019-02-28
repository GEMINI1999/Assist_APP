package com.example.hp.assist_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class ReaderActivity extends AppCompatActivity implements View.OnClickListener {
    private LocalBroadcastManager localBroadcastManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reader_activity);

        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
        String currentSpeaker = pref.getString("currentSpeaker", "");
        ImageButton back = (ImageButton) findViewById(R.id.back);
        LinearLayout help = (LinearLayout) findViewById(R.id.help);
        back.setOnClickListener(this);
        help.setOnClickListener(this);
        TextView currentReader = (TextView) findViewById(R.id.current_reader);
        currentReader.setText(currentSpeaker);

        RadioButton bt1 = (RadioButton) findViewById(R.id.jiajia);
        RadioButton bt2 = (RadioButton) findViewById(R.id.xiaokun);
        RadioButton bt3 = (RadioButton) findViewById(R.id.xiaoming);
        RadioButton bt4 = (RadioButton) findViewById(R.id.xiaomei);

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.readers);
        if (bt1.getText().equals(currentSpeaker)) {
            radioGroup.check(R.id.jiajia);
        } else if (bt2.getText().equals(currentSpeaker)) {
            radioGroup.check(R.id.xiaokun);
        } else if (bt3.getText().equals(currentSpeaker)) {
            radioGroup.check(R.id.xiaoming);
        } else if (bt4.getText().equals(currentSpeaker)) {
            radioGroup.check(R.id.xiaomei);
        }
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();

                TextView current_reader = (TextView) findViewById(R.id.current_reader);
                RadioButton radioButton = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());
                switch (radioGroup.getCheckedRadioButtonId()) {
                    case R.id.jiajia:
                        current_reader.setText(radioButton.getText());
                        editor.putString("currentSpeaker", (String) radioButton.getText());
                        editor.apply();
                        Intent reader1 = new Intent();
                        reader1.setAction("com.example.hp.Reader1");
                        reader1.putExtra("ReaderName", "1");
                        localBroadcastManager.sendBroadcast(reader1);
                        break;
                    case R.id.xiaokun:
                        current_reader.setText(radioButton.getText());
                        editor.putString("currentSpeaker", (String) radioButton.getText());
                        editor.apply();
                        Intent reader2 = new Intent();
                        reader2.setAction("com.example.hp.Reader2");
                        reader2.putExtra("ReaderName", "2");
                        localBroadcastManager.sendBroadcast(reader2);
                        break;
                    case R.id.xiaoming:
                        current_reader.setText(radioButton.getText());
                        editor.putString("currentSpeaker", (String) radioButton.getText());
                        editor.apply();
                        Intent reader3 = new Intent();
                        reader3.setAction("com.example.hp.Reader3");
                        reader3.putExtra("ReaderName", "3");
                        localBroadcastManager.sendBroadcast(reader3);
                        break;
                    case R.id.xiaomei:
                        current_reader.setText(radioButton.getText());
                        editor.putString("currentSpeaker", (String) radioButton.getText());
                        editor.apply();
                        Intent reader4 = new Intent();
                        reader4.setAction("com.example.hp.Reader4");
                        reader4.putExtra("ReaderName", "4");
                        localBroadcastManager.sendBroadcast(reader4);
                        break;
                }
            }
        });
    }


    @Override
    public void onClick(View view) {
        Intent it;
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.help:
                it = new Intent(ReaderActivity.this, YingDao_Ldy.class);
                startActivity(it);
                break;
        }
    }
}