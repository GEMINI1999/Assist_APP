package com.example.hp.assist_app;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private String mSampleDirPath;

    private static int a = 0;
    private static final String SAMPLE_DIR_NAME = "baiduTTS";
    private static final String SPEECH_FEMALE_MODEL_NAME = "bd_etts_common_speech_f7_mand_eng_high_am-mix_v3.0.0_20170512.dat";
    private static final String SPEECH_MALE_MODEL_NAME = "bd_etts_common_speech_m15_mand_eng_high_am-mix_v3.0.0_20170505.dat";
    private static final String TEXT_MODEL_NAME = "bd_etts_text.dat";
    private LocalBroadcastManager localBroadcastManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialEnv();
        initPermission();

        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
        boolean isWrite = pref.getBoolean("isWrite", false);
        boolean isApplyPermission = pref.getBoolean("isApplyPermission", false);

        if (!isWrite) {
            SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
            editor.putString("currentSpeaker", "嘉佳");
            editor.putString("currentSpeakerNumber", "0");
            editor.putString("messages", "");//存从剪切板中获取到的字符串
            editor.putInt("speakSpeed", 50);
            editor.putInt("speakTone", 50);
            editor.putBoolean("isApplyPermission", false);
            editor.putBoolean("isWrite", true);
            editor.apply();
            isWrite = true;
        }

        if (isApplyPermission) {
            startService(new Intent(MainActivity.this, FloatingService.class));
        }


        CardView reader = (CardView) findViewById(R.id.reader);
        CardView speedTone = (CardView) findViewById(R.id.speedTone);
        LinearLayout help = (LinearLayout) findViewById(R.id.help);
        reader.setOnClickListener(this);
        speedTone.setOnClickListener(this);
        help.setOnClickListener(this);

        final ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        cm.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
                ClipData data = cm.getPrimaryClip();
                ClipData.Item item = data.getItemAt(0);
                Intent mIntent = new Intent();
                mIntent.setAction("com.example.hp.ClipBoardReceiver");
                mIntent.putExtra("copyValue", item.getText());
                localBroadcastManager.sendBroadcast(mIntent);
            }
        });
        a++;
        Log.e("创建主活动累计次数", a + "");
    }

    @Override
    public void onClick(View view) {
        Intent it;
        switch (view.getId()) {
            case R.id.reader:
                it = new Intent(MainActivity.this, ReaderActivity.class);
                startActivity(it);
                break;
            case R.id.speedTone:
                it = new Intent(MainActivity.this, SpeedToneActivity.class);
                startActivity(it);
                break;
            case R.id.help:
                it = new Intent(MainActivity.this, YingDao_Sy.class);
                startActivity(it);
                break;
        }
    }

    public void startFloatingService(View view) {
        if (FloatingService.isStarted) {
            Log.e("permissions2", "点击了");
            Toast.makeText(this, "听字正在工作", Toast.LENGTH_LONG).show();
            return;
        }
        if (!Settings.canDrawOverlays(this)) {
            startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), 0);
            SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
            editor.putBoolean("isApplyPermission", true);
            editor.apply();
            Toast.makeText(this, "请再次点击“开启听字“按钮”", Toast.LENGTH_LONG).show();
            Toast.makeText(this, "请再次点击“开启听字“按钮”", Toast.LENGTH_LONG).show();
        } else {
            Log.e("permissions", "点击了");
            startService(new Intent(MainActivity.this, FloatingService.class));
        }
    }

    private void initPermission() {
        String[] permissions = {
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_SETTINGS,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE
        };

        ArrayList<String> toApplyList = new ArrayList<String>();

        for (String perm : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
                toApplyList.add(perm);
                // 进入到这里代表没有权限.
            }
        }
        String[] tmpList = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()) {
            ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 123);
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // 此处为android 6.0以上动态授权的回调，用户自行实现。
    }

    private void initialEnv() {
        if (mSampleDirPath == null) {
            String sdcardPath = Environment.getExternalStorageDirectory().toString();
            mSampleDirPath = sdcardPath + "/" + SAMPLE_DIR_NAME;
        }
        File file = new File(mSampleDirPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        copyFromAssetsToSdcard(false, SPEECH_FEMALE_MODEL_NAME, mSampleDirPath + "/" + SPEECH_FEMALE_MODEL_NAME);
        copyFromAssetsToSdcard(false, SPEECH_MALE_MODEL_NAME, mSampleDirPath + "/" + SPEECH_MALE_MODEL_NAME);
        copyFromAssetsToSdcard(false, TEXT_MODEL_NAME, mSampleDirPath + "/" + TEXT_MODEL_NAME);
    }

    public void copyFromAssetsToSdcard(boolean isCover, String source, String dest) {
        File file = new File(dest);
        if (isCover || (!isCover && !file.exists())) {
            InputStream is = null;
            FileOutputStream fos = null;
            try {
                is = getResources().getAssets().open(source);
                String path = dest;
                fos = new FileOutputStream(path);
                byte[] buffer = new byte[1024];
                int size = 0;
                while ((size = is.read(buffer, 0, 1024)) >= 0) {
                    fos.write(buffer, 0, size);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onStart() {

        Log.i("MainActivity", "onStart");

        if (!NetworkUtil.isNetworkAvailable(this)) {
            showSetNetworkUI(this);
        } else {
            Toast.makeText(this, "当前网络可用", Toast.LENGTH_SHORT).show();
        }

        super.onStart();
    }

    @Override
    protected void onResume() {

        Log.i("MainActivity", "onStart");

        super.onResume();
    }

    /*
      * 打开设置网络界面
      */
    public void showSetNetworkUI(final Context context) {
// 提示对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("首次使用时需联网")
                .setMessage("当前网络连接不可用,请开启网络连接")
                .setPositiveButton("设置", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
// TODO Auto-generated method stub
                        Intent intent = null;
// 判断手机系统的版本 即API大于10 就是3.0或以上版本
                        if (android.os.Build.VERSION.SDK_INT > 10) {
                            intent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
                        } else {
                            intent = new Intent();
                            ComponentName component = new ComponentName(
                                    "com.android.settings",
                                    "com.android.settings.WirelessSettings");
                            intent.setComponent(component);
                            intent.setAction("android.intent.action.VIEW");
                        }
                        context.startActivity(intent);
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }
}
