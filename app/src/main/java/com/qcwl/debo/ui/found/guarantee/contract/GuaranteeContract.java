package com.qcwl.debo.ui.found.guarantee.contract;

import android.content.Context;

/**
 * Created by Administrator on 2017/8/19.
 */

public interface GuaranteeContract {

    interface View {
        void doSuccess(int type, Object object);

        void doFailure(int code);
    }

    interface Presenter {
        void getGuaranteeDetailList(Context context, String uid, int page);

        void confirmGuaranteeDetail(Context context, String uid, int state,String tri_id, int request_type);
    }

}
