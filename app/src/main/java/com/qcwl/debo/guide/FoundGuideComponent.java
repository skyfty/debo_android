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

public class FoundGuideComponent implements Component {

    private int flag;
    private RelativeLayout ll;

    public FoundGuideComponent(int flag) {
        this.flag = flag;
    }

    @Override
    public View getView(LayoutInflater inflater) {
        if (flag < 5) {
            ll = (RelativeLayout) inflater.inflate(R.layout.found_guide, null);
        }
//        else {
//            ll = (RelativeLayout) inflater.inflate(R.layout.sanfangke_guide, null);
//        }
        TextView tv = (TextView) ll.findViewById(R.id.tv);
        tv.setTypeface(FontCustom.setFont(App.getApp()));
        if (flag == 1) {
            tv.setText(R.string.chouyichou_guide);
        } else if (flag == 2) {
            tv.setText(R.string.pengyipeng_guide);
        } else if (flag == 3) {
            tv.setText(R.string.zhuangyizhuang_guide);
        } else if (flag == 4) {
            tv.setText(R.string.star_guide);
        }/* else if (flag == 5) {
            tv.setText(R.string.sanfangke_guide);
        }*/
        return ll;
    }

    @Override
    public int getAnchor() {
        if (flag < 5)
            return Component.ANCHOR_BOTTOM;
        return Component.ANCHOR_TOP;
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
