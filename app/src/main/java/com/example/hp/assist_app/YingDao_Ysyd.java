package com.example.hp.assist_app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.hp.assist_app.Copmponent.Guide;
import com.example.hp.assist_app.Copmponent.GuideBuilder;
import com.example.hp.assist_app.Copmponent.SimpleComponent_back;
import com.example.hp.assist_app.Copmponent.SimpleComponent_listen;
import com.example.hp.assist_app.Copmponent.SimpleComponent_yd;
import com.example.hp.assist_app.Copmponent.SimpleComponent_ys;

public class YingDao_Ysyd extends Activity {

    LinearLayout ys, yd;
    ImageView listen, back;
    Guide guide, guide2, guide3, guide4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.speed_tone_activity);
        ys = (LinearLayout) findViewById(R.id.ys);
        yd = (LinearLayout) findViewById(R.id.yd);
        listen = (ImageView) findViewById(R.id.listen);
        back = (ImageView) findViewById(R.id.back);
        ys.post(new Runnable() {
            @Override
            public void run() {
                showGuideView();
            }
        });
    }

    public void showGuideView() {
        GuideBuilder builder = new GuideBuilder();
        builder.setTargetView(ys)
                .setAlpha(150)
                .setOverlayTarget(false)
                .setOutsideTouchable(false);
        builder.setOnVisibilityChangedListener(new GuideBuilder.OnVisibilityChangedListener() {
            @Override
            public void onShown() {
            }

            @Override
            public void onDismiss() {
                showGuide2View();
            }
        });

        builder.addComponent(new SimpleComponent_ys());
        guide = builder.createGuide();
        guide.setShouldCheckLocInWindow(true);
        guide.show(this);
    }

    public void showGuide2View() {
        GuideBuilder builder2 = new GuideBuilder();
        builder2.setTargetView(yd)
                .setAlpha(150)
                .setOverlayTarget(false)
                .setOutsideTouchable(false);
        builder2.setOnVisibilityChangedListener(new GuideBuilder.OnVisibilityChangedListener() {
            @Override
            public void onShown() {
            }

            @Override
            public void onDismiss() {
                showGuide3View();
            }
        });

        builder2.addComponent(new SimpleComponent_yd());
        guide2 = builder2.createGuide();
        guide2.setShouldCheckLocInWindow(true);
        guide2.show(this);
    }

    public void showGuide3View() {
        GuideBuilder builder3 = new GuideBuilder();
        builder3.setTargetView(listen)
                .setAlpha(150)
                .setHighTargetCorner(20)
                .setHighTargetPadding(10)
                .setOverlayTarget(false)
                .setOutsideTouchable(false);
        builder3.setOnVisibilityChangedListener(new GuideBuilder.OnVisibilityChangedListener() {
            @Override
            public void onShown() {
            }

            @Override
            public void onDismiss() {
                showGuide4View();
            }
        });

        builder3.addComponent(new SimpleComponent_listen());
        guide3 = builder3.createGuide();
        guide3.setShouldCheckLocInWindow(true);
        guide3.show(this);
    }

    public void showGuide4View() {
        GuideBuilder builder4 = new GuideBuilder();
        builder4.setTargetView(back)
                .setAlpha(150)
                .setHighTargetCorner(20)
                .setHighTargetPadding(5)
                .setOverlayTarget(false)
                .setOutsideTouchable(false);
        builder4.setOnVisibilityChangedListener(new GuideBuilder.OnVisibilityChangedListener() {
            @Override
            public void onShown() {
            }

            @Override
            public void onDismiss() {
                finish();
            }
        });

        builder4.addComponent(new SimpleComponent_back());
        guide4 = builder4.createGuide();
        guide4.setShouldCheckLocInWindow(true);
        guide4.show(this);
    }

}