package com.qcwl.debo.ui.found.near.contact;

import android.content.Context;

import com.qcwl.debo.ui.found.near.bean.NearBean;
import com.qcwl.debo.ui.found.near.bean.TrumpetBean;

import java.util.List;

/**
 * Created by Administrator on 2017/8/4.
 */

public interface NearContact {

    interface View {
        void doTrumpetListSuccess(List<TrumpetBean> items);

        void doNearListSuccess(List<NearBean> list);

        void doTrumpetShout();

        void doFailure(int code);
    }

    interface Presenter {
        void getTrumpetList(Context context, String uid);

        void getNearList(Context context, String uid, double lat, double lng, int distance);

        void trumpetShout(Context context, String uid, String lat, String lng, String range, String content);
    }

}
