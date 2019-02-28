package com.example.hp.assist_app.Copmponent;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.hp.assist_app.R;

public class SimpleComponent_ysyd implements Component {

    @Override
    public View getView(LayoutInflater inflater) {

        LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.layer_ysyd, null);
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
        return 40;
    }

    @Override
    public int getYOffset() {
        return -270;
    }
}
