package com.example.hp.assist_app;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.RadioButton;

import com.example.hp.assist_app.Copmponent.Guide;
import com.example.hp.assist_app.Copmponent.GuideBuilder;
import com.example.hp.assist_app.Copmponent.SimpleComponent_back;
import com.example.hp.assist_app.Copmponent.SimpleComponent_ldy1;
import com.example.hp.assist_app.Copmponent.SimpleComponent_listen1;

public class YingDao_Ldy extends Activity {

    RadioButton jiajia;
    ImageButton back;
    Guide guide, guide2, guide3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reader_activity);

        back = (ImageButton) findViewById(R.id.back);
        jiajia = (RadioButton) findViewById(R.id.jiajia);
        jiajia.post(new Runnable() {
            @Override
            public void run() {
                showGuideView();
            }
        });
    }

    public void showGuideView() {
        GuideBuilder builder = new GuideBuilder();
        builder.setTargetView(jiajia)
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

        builder.addComponent(new SimpleComponent_ldy1());
        guide = builder.createGuide();
        guide.setShouldCheckLocInWindow(true);
        guide.show(this);
    }

    public void showGuide2View() {
        GuideBuilder builder2 = new GuideBuilder();
        builder2.setTargetView(jiajia)
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

        builder2.addComponent(new SimpleComponent_listen1());
        guide2 = builder2.createGuide();
        guide2.setShouldCheckLocInWindow(true);
        guide2.show(this);
    }

    public void showGuide3View() {
        GuideBuilder builder3 = new GuideBuilder();
        builder3.setTargetView(back)
                .setAlpha(150)
                .setHighTargetCorner(20)
                .setHighTargetPadding(5)
                .setOverlayTarget(false)
                .setOutsideTouchable(false);
        builder3.setOnVisibilityChangedListener(new GuideBuilder.OnVisibilityChangedListener() {
            @Override
            public void onShown() {
            }

            @Override
            public void onDismiss() {
                finish();
            }
        });

        builder3.addComponent(new SimpleComponent_back());
        guide3 = builder3.createGuide();
        guide3.setShouldCheckLocInWindow(true);
        guide3.show(this);
    }
}