package com.qcwl.debo.chat;

/**
 * Created by Administrator on 2016/9/22.
 */

/**
 * Enum class to handle the different states
 * of permissions since the PackageManager only
 * has a granted and denied state.
 */
enum Permissions {
    GRANTED,
    DENIED,
    NOT_FOUND
}
