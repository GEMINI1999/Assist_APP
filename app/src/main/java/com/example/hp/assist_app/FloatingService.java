package com.example.hp.assist_app;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.baidu.tts.auth.AuthInfo;
import com.baidu.tts.chainofresponsibility.logger.LoggerProxy;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;
import com.example.hp.assist_app.log.MessageListener;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FloatingService extends Service {
    public static boolean isStarted = false;
    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    private ImageButton imageButton;
    private final int MOBILE_QUERY = 1;
    private final int IS_COPYING = 2;
    private LocalBroadcastManager localBroadcastManager;


    private static final String TAG = "MessageListener";
    private static final String SAMPLE_DIR_NAME = "baiduTTS";

    private String TEXT = "";

    private String currentSpeaker = "0";
    private String speakSpeed = "5";
    private String speakTone = "5";


    // ================== 初始化参数设置开始 ==========================
    /**
     * 发布时请替换成自己申请的appId appKey 和 secretKey。注意如果需要离线合成功能,请在您申请的应用中填写包名。
     * 本demo的包名是com.baidu.tts.sample，定义在build.gradle中。
     */
    protected String appId = "15507224";

    protected String appKey = "5tCKGUylrvVYA6kNFRZibhDB";

    protected String secretKey = "GBHlugqi4C1qIUUCBETj0lEk9OCUYn03";
    // TtsMode.MIX; 离在线融合，在线优先； TtsMode.ONLINE 纯在线； 没有纯离线
    private TtsMode ttsMode = TtsMode.ONLINE;
    // ================选择TtsMode.ONLINE  不需要设置以下参数; 选择TtsMode.MIX 需要设置下面2个离线资源文件的路径
    private static final String TEMP_DIR = Environment.getExternalStorageDirectory().toString() + "/" + SAMPLE_DIR_NAME; // 重要！请手动将assets目录下的3个dat 文件复制到该目录

    // 请确保该PATH下有这个文件
    private static final String TEXT_FILENAME = TEMP_DIR + "/" + "bd_etts_text.dat";

    // 请确保该PATH下有这个文件 ，m15是离线男声
    private String MODEL_FILENAME =
            TEMP_DIR + "/" + "bd_etts_common_speech_as_mand_eng_high_am_v3.0.0_20170516.dat";;
    protected Handler mainHandler;
    protected SpeechSynthesizer mSpeechSynthesizer;


    @Override
    public void onCreate() {
        super.onCreate();
        isStarted = true;

        SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
        currentSpeaker = pref.getString("currentSpeakerNumber","");
        speakSpeed = pref.getInt("speakSpeed",0)/10 + "";
        speakTone = pref.getInt("speakTone",0)/10 + "";

        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.width = 150;
        layoutParams.height = 150;
        layoutParams.x = 1000;
        layoutParams.y = 600;

        imageButton = new ImageButton(getApplicationContext());
        imageButton.setElevation(10);
        imageButton.setImageResource(R.drawable.ic_check_circle_white_24dp);

        Message msg = handler.obtainMessage(MOBILE_QUERY);
        handler.sendMessageDelayed(msg, 10000);
        /**
         * 注册广播接收器
         * by yingyaopeng 2019-2-1
         */
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.hp.ClipBoardReceiver");
        intentFilter.addAction("com.example.hp.SpeedBoardReceiver");
        intentFilter.addAction("com.example.hp.ToneBoardReceiver");
        intentFilter.addAction("com.example.hp.ResetSpeedBoardReceiver");
        intentFilter.addAction("com.example.hp.ResetToneBoardReceiver");
        intentFilter.addAction("com.example.hp.PlayExample");
        intentFilter.addAction("com.example.hp.Reader1");
        intentFilter.addAction("com.example.hp.Reader2");
        intentFilter.addAction("com.example.hp.Reader3");
        intentFilter.addAction("com.example.hp.Reader4");
        MainBroadcastReceiver mainBroadcastReceiver = new MainBroadcastReceiver();
        localBroadcastManager.registerReceiver(mainBroadcastReceiver, intentFilter);

        Intent intent = new Intent(this, MainActivity.class);
        String CHANNEL_ONE_ID = "com.primedu.cn";
        String CHANNEL_ONE_NAME = "Channel One";
        NotificationChannel notificationChannel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(CHANNEL_ONE_ID,
                    CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(notificationChannel);
        }
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("“听字”已开启")
                .setContentText("更多选项")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_check_circle_white_24dp)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_check_circle_white_24dp))
                .setContentIntent(pi)
                .setChannelId(CHANNEL_ONE_ID)
                .build();
        startForeground(1, notification);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        new Thread(){
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }.start();
//        Notification.Builder builder = new Notification.Builder(this.getApplicationContext());
//        Intent nfIntent = new Intent(this,MainActivity.class);
//        builder.setContentIntent(PendingIntent.getActivity(this,0,nfIntent,0))
//                //设置通知栏大图标
//                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),R.drawable.ic_check_circle_white_24dp))
//                //设置服务标题
//                .setContentTitle("服务123")
//                .setSmallIcon(R.drawable.ic_help_black_36dp)
//                //设置服务内容
//                .setContentText("服务正在运行")
//                //设置通知时间
//                .setWhen(System.currentTimeMillis());
//        Notification notification = null;


        if (Settings.canDrawOverlays(this)) {
            imageButton = new ImageButton(getApplicationContext());
            imageButton.setElevation(10);
            imageButton.setImageResource(R.drawable.ic_check_circle_white_24dp);
            imageButton.setColorFilter(Color.RED);
            windowManager.addView(imageButton, layoutParams);
            imageButton.setVisibility(View.INVISIBLE);
            imageButton.setOnTouchListener(new FloatingOnTouchListener());
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
                    TEXT = pref.getString("messages", "");
                    initTTs();
                }
            });
        }
        return super.onStartCommand(intent, flags, startId);
    }


    private class FloatingOnTouchListener implements View.OnTouchListener {
        private int x;
        private int y;

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    break;
                case MotionEvent.ACTION_DOWN:
                    x = (int) event.getRawX();
                    y = (int) event.getRawY();
                    resetTime();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int nowX = (int) event.getRawX();
                    int nowY = (int) event.getRawY();
                    int movedX = nowX - x;
                    int movedY = nowY - y;
                    x = nowX;
                    y = nowY;
                    layoutParams.x = layoutParams.x + movedX;
                    layoutParams.y = layoutParams.y + movedY;
                    windowManager.updateViewLayout(view, layoutParams);
                    break;
                default:
                    break;
            }
            return false;
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MOBILE_QUERY:
                    imageButton.setVisibility(View.INVISIBLE);
                    Log.e("stop", "FloatingService is gone");
                    break;
                case IS_COPYING:
                    imageButton.setVisibility(View.VISIBLE);
                    resetTime();
                    break;
            }
        }
    };

    public void resetTime() {
        Log.e("stop", "reseting time");
        handler.removeMessages(MOBILE_QUERY);
        Message msg = handler.obtainMessage(MOBILE_QUERY);
        handler.sendMessageDelayed(msg, 10000);
    }

    private void initTTs() {
        LoggerProxy.printable(true); // 日志打印在logcat中
        boolean isMix = ttsMode.equals(TtsMode.MIX);

        MessageListener listener = new MessageListener(mainHandler);

        // 1. 获取实例
        mSpeechSynthesizer = SpeechSynthesizer.getInstance();
        mSpeechSynthesizer.setContext(this);

        // 2. 设置listener
        mSpeechSynthesizer.setSpeechSynthesizerListener(listener);

        // 3. 设置appId，appKey.secretKey
        mSpeechSynthesizer.setAppId(appId);

        mSpeechSynthesizer.setApiKey(appKey, secretKey);


        // 4. 支持离线的话，需要设置离线模型
        if (isMix) {
            //检查离线授权文件是否下载成功，离线授权文件联网时SDK自动下载管理，有效期3年，3年后的最后一个月自动更新。
//            isSuccess = checkAuth();
//            if (!isSuccess) {
//                return;
//            }
            // 文本模型文件路径 (离线引擎使用)， 注意TEXT_FILENAME必须存在并且可读
            mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, TEXT_FILENAME);
            // 声学模型文件路径 (离线引擎使用)， 注意TEXT_FILENAME必须存在并且可读
            mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, MODEL_FILENAME);
        }

        // 5. 以下setParam 参数选填。不填写则默认值生效
        // 设置在线发声音人： 0 普通女声（默认） 1 普通男声 2 特别男声 3 情感男声<度逍遥> 4 情感儿童声<度丫丫>
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, currentSpeaker);
        // 设置合成的音量，0-9 ，默认 5
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_VOLUME, "9");
        // 设置合成的语速，0-9 ，默认 5
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEED, speakSpeed);
        // 设置合成的语调，0-9 ，默认 5
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_PITCH, speakTone);
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_HIGH_SPEED_SYNTHESIZE);
        // 该参数设置为TtsMode.MIX生效。即纯在线模式不生效。
        // MIX_MODE_DEFAULT 默认 ，wifi状态下使用在线，非wifi离线。在线状态下，请求超时6s自动转离线
        // MIX_MODE_HIGH_SPEED_SYNTHESIZE_WIFI wifi状态下使用在线，非wifi离线。在线状态下， 请求超时1.2s自动转离线
        // MIX_MODE_HIGH_SPEED_NETWORK ， 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
        // MIX_MODE_HIGH_SPEED_SYNTHESIZE, 2G 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线

        mSpeechSynthesizer.setAudioStreamType(AudioManager.MODE_IN_CALL);

        // x. 额外 ： 自动so文件是否复制正确及上面设置的参数
        Map<String, String> params = new HashMap<>();
        // 复制下上面的 mSpeechSynthesizer.setParam参数
        // 上线时请删除AutoCheck的调用
        if (isMix) {
            params.put(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, TEXT_FILENAME);
            params.put(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, MODEL_FILENAME);
        }
//        InitConfig initConfig =  new InitConfig(appId, appKey, secretKey, ttsMode, params, listener);
//        AutoCheck.getInstance(getApplicationContext()).check(initConfig, new Handler() {
//            @Override
//            /**
//             * 开新线程检查，成功后回调
//             */
//            public void handleMessage(Message msg) {
//                if (msg.what == 100) {
//                    AutoCheck autoCheck = (AutoCheck) msg.obj;
//                    synchronized (autoCheck) {
//                        String message = autoCheck.obtainDebugMessage();
//                        print(message); // 可以用下面一行替代，在logcat中查看代码
//                        // Log.w("AutoCheckMessage", message);
//                    }
//                }·
//            }
//
//        });

        // 6. 初始化
        mSpeechSynthesizer.initTts(ttsMode);

        mSpeechSynthesizer.speak(TEXT);

    }


    private void print(String message) {
        Log.i(TAG, message);
    }

    @Override
    public void onDestroy() {
        if (mSpeechSynthesizer != null) {
            mSpeechSynthesizer.stop();
            mSpeechSynthesizer.release();
            mSpeechSynthesizer = null;
            print("释放资源成功");
        }
        super.onDestroy();
    }

    private class MainBroadcastReceiver extends BroadcastReceiver {
        SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case "com.example.hp.ClipBoardReceiver":
                    Log.e(this.getClass().getSimpleName(), "已经接收");
                    String copyValue = intent.getStringExtra("copyValue");
                    editor.putString("messages", copyValue);
                    editor.apply();
                    Message isCopying = handler.obtainMessage(IS_COPYING);
                    handler.sendMessage(isCopying);
                    break;
                case "com.example.hp.SpeedBoardReceiver":
                    speakSpeed = intent.getStringExtra("speedValue");
                    break;
                case "com.example.hp.ToneBoardReceiver":
                    speakTone = intent.getStringExtra("toneValue");
                    break;
                case "com.example.hp.ResetSpeedBoardReceiver":
                    speakSpeed = intent.getStringExtra("resetSpeedValue");
                    break;
                case "com.example.hp.ResetToneBoardReceiver":
                    Log.e("reset", "tone");
                    speakTone = intent.getStringExtra("resetToneValue");
                    break;
                case "com.example.hp.PlayExample":
                    TEXT = intent.getStringExtra("example");
                    Log.e("example", "exampleing");
                    initTTs();
                    break;
                case "com.example.hp.Reader1":
                    Log.e("reader","1");
                    MODEL_FILENAME =
                            TEMP_DIR + "/" + "bd_etts_common_speech_as_mand_eng_high_am_v3.0.0_20170516.dat";
                    currentSpeaker = "0";
                    TEXT = "您好，我是嘉佳";
                    initTTs();
                    editor.putString("currentSpeakerNumber","0");
                    editor.apply();
                    break;
                case "com.example.hp.Reader2":
                    Log.e("reader","2");
                    MODEL_FILENAME =
                            TEMP_DIR + "/" + "bd_etts_ommon_speech_m15_mand_eng_high_am-mix_v3.0.0_20170505c.dat";
                    currentSpeaker = "1";
                    TEXT = "您好，我是小坤";
                    initTTs();
                    editor.putString("currentSpeakerNumber","1");
                    editor.apply();
                    break;
                case "com.example.hp.Reader3":
                    Log.e("reader","3");
                    MODEL_FILENAME =
                            TEMP_DIR + "/" + "bd_etts_common_speech_f7_mand_eng_high_am-mix_v3.0.0_20170512.dat";
                    currentSpeaker = "3";
                    TEXT = "您好，我是小明";
                    initTTs();
                    editor.putString("currentSpeakerNumber","3");
                    editor.apply();
                    break;
                case "com.example.hp.Reader4":
                    Log.e("reader","4");
                    MODEL_FILENAME =
                            TEMP_DIR + "/" + "bd_etts_common_speech_yyjw_mand_eng_high_am-mix_v3.0.0_20170512.dat";
                    currentSpeaker = "4";
                    TEXT = "您好，我是小美";
                    initTTs();
                    editor.putString("currentSpeakerNumber","4");
                    editor.apply();
                    break;
            }

        }
    }
}