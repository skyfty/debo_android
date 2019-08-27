package com.qcwl.debo.ui.found.joke.video_new;

import android.app.Application;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import cn.jzvd.JZVideoPlayerStandard;

//创建一个类集成JZVideoPlayerStandard 并在XML设置
public class JZVideoPlayerStandardLoopVideo extends JZVideoPlayerStandard {
    private float b;
    private float x_down;
    private float y_down;
    private float x_cancel;

    public JZVideoPlayerStandardLoopVideo(Context context) {
        super(context);
    }

    public JZVideoPlayerStandardLoopVideo(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onAutoCompletion() {
        super.onAutoCompletion();
        startVideo();
    }

   /* @Override
    public boolean onTouch(View v, MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                Log.i("OnTouch", "ACTION_MOVE移动");
                break;
            case MotionEvent.ACTION_DOWN:
                Log.i("OnTouch", "ACTION_DOWN开始触摸");
                Log.i("OnTouch", "xxxxxx=" + e.getX());
                Log.i("OnTouch", "yyyyyy=" + e.getY());
                //获得开始触摸时的X值
                x_down = e.getX();
                y_down = e.getY();
                break;
            case MotionEvent.ACTION_UP:
                Log.i("OnTouch", "ACTION_UP抬起手指");
                Log.i("OnTouch", "xxxxxx1=" + e.getX());
                Log.i("OnTouch", "yyyyyy1=" + e.getY());
                //获得抬起手指时的X值
                b = e.getX();
                //a<b证明右滑，否则左滑
                if (x_down < b) {
                    Log.i("OnTouch", "-1");
                } else {
                    Log.i("OnTouch", "1");
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                Log.i("OnTouch", "ACTION_CANCEL手势取消");
                Log.i("OnTouch", "xxxxxx2=" + e.getX());
                Log.i("OnTouch", "yyyyyy2=" + e.getY());

                //获得抬起手指时的X值
                x_cancel = e.getX();
                Log.i("OnTouch", "x_cancel-x_down=" + (x_cancel - x_down));
                float x_mobile = x_cancel - x_down;
                //a<b证明右滑，否则左滑
                if (-40 > x_mobile || x_mobile > 40) {
                    if (x_down < x_cancel) {
                        //TODO 这里执行右滑事件
                        Log.i("OnTouch", "右滑");
                        CallBackUtils.doCallBackMethod(-1);
                    } else {
                        //TODO 这里执行左滑事件
                        Log.i("OnTouch", "左滑");
                        CallBackUtils.doCallBackMethod(1);
                    }
                }
                break;
            case MotionEvent.ACTION_OUTSIDE:
                Log.i("OnTouch", "ACTION_OUTSIDE超出UI范围");
                Log.i("OnTouch", e.getX() + "");
                Log.i("OnTouch", e.getY() + "");
                break;
            default:
                break;
        }
        return super.onTouch(v, e);
    }*/


    /*@Override
    public void setOnTouchListener(OnTouchListener l) {
        super.setOnTouchListener(l);
    }*/
}

