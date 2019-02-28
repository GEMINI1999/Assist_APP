package com.example.hp.assist_app.Copmponent;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.hp.assist_app.R;

/*
  @Override
  public View getView(LayoutInflater inflater) {

    LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.layer_ldy, null); 设置引导层布局
    ll.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Toast.makeText(view.getContext(), "未点中正确位置", Toast.LENGTH_SHORT).show();
      }
    });
    return ll;
  }

  @Override
  public int getAnchor() {获取焦点
    return ANCHOR_BOTTOM;
  }

  @Override
  public int getFitPosition() {
    return FIT_END;
  }

  @Override
  public int getXOffset() {往左移动为正数，往右移动为负数
    return 10;
  }

  @Override
  public int getYOffset() {往上移动为负数，往上移动为正数
    return 0;
  }
}
40 -270 3.
30 0 2.
10 0 1.
 */
public class SimpleComponent_ldy implements Component {

    @Override
    public View getView(LayoutInflater inflater) {

        LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.layer_ldy, null);
        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "未点中正确位置", Toast.LENGTH_SHORT).show();
            }
        });
        return ll;
    }

    @Override
    public int getAnchor() {
        return ANCHOR_BOTTOM;
    }

    @Override
    public int getFitPosition() {
        return FIT_END;
    }

    @Override
    public int getXOffset() {
        return 25;
    }

    @Override
    public int getYOffset() {
        return 0;
    }
}
