package com.qcwl.debo.widget;

import com.hyphenate.easeui.domain.EaseUser;

/**
 * Created by Administrator on 2016/9/23.
 */

public class RobotUser extends EaseUser {
    public RobotUser(String username) {
        super(username.toLowerCase());
    }
}