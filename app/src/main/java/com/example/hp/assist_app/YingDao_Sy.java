package com.example.hp.assist_app;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.CardView;

import com.example.hp.assist_app.Copmponent.Guide;
import com.example.hp.assist_app.Copmponent.GuideBuilder;
import com.example.hp.assist_app.Copmponent.SimpleComponent_back;
import com.example.hp.assist_app.Copmponent.SimpleComponent_ldy;
import com.example.hp.assist_app.Copmponent.SimpleComponent_permission;
import com.example.hp.assist_app.Copmponent.SimpleComponent_ysyd;

/*
  public void showGuideView() {
    GuideBuilder builder = new GuideBuilder();
    builder.setTargetView(reader) 设置高亮区域
        .setAlpha(150) 设置透明度
        .setHighTargetCorner(20) 设置圆角
        .setHighTargetPadding(10) 设置边距
        .setOverlayTarget(false)  这俩不知道，但是试了一下改成true回和原效果不同，会失去蒙版
        .setOutsideTouchable(false);
    builder.setOnVisibilityChangedListener(new GuideBuilder.OnVisibilityChangedListener() {
      @Override
      public void onShown() {
      }

      @Override
      public void onDismiss() { 点击高亮区域后的效果
        Intent it = new Intent(YingDao_Sy.this,YingDao_Ldy.class);
        startActivity(it);
      }
    });

    builder.addComponent(new SimpleComponent_ldy());创建布局
    guide = builder.createGuide();
    guide.setShouldCheckLocInWindow(true);这个不知道
    guide.show(this);
  }
 */
public class YingDao_Sy extends Activity {

    CardView apply_permission, reader, speedTone;
    Guide guide, guide2, guide3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        apply_permission = (CardView) findViewById(R.id.apply_permission);
        reader = (CardView) findViewById(R.id.reader);
        speedTone = (CardView) findViewById(R.id.speedTone);
        apply_permission.post(new Runnable() {
            @Override
            public void run() {
                showGuideView();
            }
        });
    }

    public void showGuideView() {
        GuideBuilder builder = new GuideBuilder();
        builder.setTargetView(apply_permission)
                .setAlpha(150)
                .setHighTargetCorner(20)
                .setHighTargetPadding(10)
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

        builder.addComponent(new SimpleComponent_permission());
        guide = builder.createGuide();
        guide.setShouldCheckLocInWindow(true);
        guide.show(this);
    }

    public void showGuide2View() {
        GuideBuilder builder2 = new GuideBuilder();
        builder2.setTargetView(reader)
                .setAlpha(150)
                .setHighTargetCorner(20)
                .setHighTargetPadding(10)
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

        builder2.addComponent(new SimpleComponent_ldy());
        guide2 = builder2.createGuide();
        guide2.setShouldCheckLocInWindow(true);
        guide2.show(this);
    }

    public void showGuide3View() {
        GuideBuilder builder3 = new GuideBuilder();
        builder3.setTargetView(speedTone)
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
                finish();
            }
        });
        builder3.addComponent(new SimpleComponent_ysyd());
        guide3 = builder3.createGuide();
        guide3.setShouldCheckLocInWindow(true);
        guide3.show(this);
    }
}