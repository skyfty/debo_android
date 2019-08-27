package com.qcwl.debo.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by Administrator on 2018/9/28.
 */

public class SearchEditText extends android.support.v7.widget.AppCompatEditText {

    private boolean isIconLeft = false;//图标是否默认在左边
    private Drawable[] drawables; // 控件的图片资源
    private Drawable drawableLeft; // 搜索图标

    public SearchEditText(Context context) {
        super(context);
        initView(context);
    }

    public SearchEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public SearchEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        if (context instanceof Activity)            //键盘监听(下面会贴出代码)
            SoftKeyBoardListener.setListener((Activity) context, new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
                @Override
                public void keyBoardShow(int height) {//键盘处于打开状态
                    SearchEditText.this.setCursorVisible(true);// 显示光标
                    isIconLeft = true;//重新绘制(图片居左或者不显示)
                }

                @Override
                public void keyBoardHide(int height) {//键盘处于关闭装啊提
                    SearchEditText.this.setCursorVisible(false);// 隐藏光标
                    if (!TextUtils.isEmpty(getText().toString().trim())) {
                        return;
                    }
                    isIconLeft = false;//重新绘制(图片居中)
                }
            });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isIconLeft) {
            // 图片居左显示(我这里是不显示，需要显示就去除注释就可以了)
            //            if (drawables == null) drawables = getCompoundDrawables();//获取四个图片位置(左上右下)
            //            if (drawableLeft == null) drawableLeft = drawables[0];//左边图片//
            //   this.setCompoundDrawablesWithIntrinsicBounds(drawableLeft , null, null, null);
            this.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            super.onDraw(canvas);
        }
        else { // 将图标绘制在中间
            if (drawables == null)
                drawables = getCompoundDrawables();//获取四个图片位置(左上右下)
            if (drawableLeft == null)
                drawableLeft = drawables[0];//左边图片
            this.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, null, null);//绘制图片
            float textWidth = getPaint().measureText(getHint().toString());//得到文字宽度
            int drawablePadding = getCompoundDrawablePadding();//得到drawablePadding宽度
            int drawableWidth = drawableLeft.getIntrinsicWidth();//得到图片宽度
            float bodyWidth = textWidth + drawableWidth + drawablePadding;//计算距离
            canvas.translate((getWidth() - bodyWidth - getPaddingLeft() - getPaddingRight()) / 2, 0);//最终绘制位置
            super.onDraw(canvas);
        }
    }

    /**
     * 动态设置图片位置
     */
    public void setIconSeat(boolean iconSeat) {
        isIconLeft = iconSeat;
        invalidate();//重新绘制
    }

}
