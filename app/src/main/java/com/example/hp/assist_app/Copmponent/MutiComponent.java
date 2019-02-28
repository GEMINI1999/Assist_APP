package com.example.hp.assist_app.Copmponent;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hp.assist_app.R;

/**
 * Created by binIoter on 16/6/17.
 */
public class MutiComponent implements Component {

    @Override
    public View getView(LayoutInflater inflater) {
        LinearLayout ll = new LinearLayout(inflater.getContext());
        LinearLayout.LayoutParams param =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setLayoutParams(param);
        ImageView imageView = new ImageView(inflater.getContext());
        imageView.setImageResource(R.mipmap.arrow_top);
        TextView textView = new TextView(inflater.getContext());
        textView.setText("点击这里选择朗读员");
        textView.setTextColor(inflater.getContext().getResources().getColor(R.color.white));
        textView.setTextSize(30);
        ll.removeAllViews();
        ll.addView(textView);
        ll.addView(imageView);
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
        return FIT_CENTER;
    }

    @Override
    public int getXOffset() {
        return -170;
    }

    @Override
    public int getYOffset() {
        return 0;
    }
}
