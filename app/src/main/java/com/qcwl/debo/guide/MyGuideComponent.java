package com.qcwl.debo.guide;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qcwl.debo.R;
import com.qcwl.debo.application.App;
import com.qcwl.debo.utils.FontCustom;


/**
 * Created by qcwl02 on 2017/9/28.
 */

public class MyGuideComponent implements Component {

    @Override
    public View getView(LayoutInflater inflater) {
        RelativeLayout ll = (RelativeLayout) inflater.inflate(R.layout.found_guide, null);
        TextView tv = (TextView) ll.findViewById(R.id.tv);
        tv.setTypeface(FontCustom.setFont(App.getApp()));
        tv.setText(R.string.advertisement_guide);
        return ll;
    }

    @Override
    public int getAnchor() {
        return Component.ANCHOR_BOTTOM;
    }

    @Override
    public int getFitPosition() {
        return Component.FIT_START;
    }

    @Override
    public int getXOffset() {
        return 0;
    }

    @Override
    public int getYOffset() {
        return 10;
    }
}
