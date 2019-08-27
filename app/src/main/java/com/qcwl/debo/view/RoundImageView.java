package com.qcwl.debo.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;

import com.hyphenate.easeui.view.RoundedDrawable;
import com.qcwl.debo.R;

/**
 * xiangpan
 * 2018/3/5
 * 17600660418
 * x1992418@163.com
 */

public class RoundImageView extends AppCompatImageView {
    public static final float DEFAULT_BORDER_WIDTH = 0f;
    private ColorStateList borderColor = ColorStateList.valueOf(RoundedDrawable.DEFAULT_BORDER_COLOR);
    private float borderWidth = DEFAULT_BORDER_WIDTH;
    private Paint mBorderPaint;
    private Bitmap mRawBitmap;

    public RoundImageView(final Context context) {
        this(context, null);
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.setScaleType(ScaleType.FIT_XY);
        TypedArray ta = context.obtainStyledAttributes(attrs,
                R.styleable.RoundImageView, defStyle, 0);
        if (ta != null) {
            mRadius = ta.getDimension(R.styleable.RoundImageView_image_radius, 0);
            mShadowRadius = ta.getDimension(R.styleable.RoundImageView_image_shadow_radius1, 0);
            mIsCircle = ta.getBoolean(R.styleable.RoundImageView_image_circle, false);
            mIsShadow = ta.getBoolean(R.styleable.RoundImageView_image_shadow, false);
            mShadowColor = ta.getInteger(R.styleable.RoundImageView_shadow_color1, 0xffa65eff);


            borderWidth = ta.getDimensionPixelSize(R.styleable.RoundImageView_border_width1, -1);
            if (borderWidth < 0) {
                borderWidth = DEFAULT_BORDER_WIDTH;
            }
            borderColor = ta.getColorStateList(R.styleable.RoundImageView_border_color1);
            if (borderColor == null) {
                borderColor = ColorStateList.valueOf(RoundedDrawable.DEFAULT_BORDER_COLOR);
            }
            ta.recycle();
        } else {
            mRadius = 0;
            mShadowRadius = 0;
            mIsCircle = false;
            mIsShadow = false;
            //mShadowColor = 0xffe4e4e4;
            mShadowColor = 0xffa65eff;
        }

    }


    private float mRadius;
    private float mShadowRadius;
    private int mShadowColor;
    private boolean mIsCircle;
    private boolean mIsShadow;
    private int width;
    private int height;
    private int imageWidth;
    private int imageHeight;
    private Paint mPaint;
    private ColorStateList mBorderColor = ColorStateList.valueOf(DEFAULT_BORDER_COLOR);
    public static final int DEFAULT_BORDER_COLOR = Color.BLACK;
    private Paint mPaintBitmap = new Paint(Paint.ANTI_ALIAS_FLAG);//抗锯齿

    @SuppressLint("ResourceAsColor")
    @Override
    public void onDraw(Canvas canvas) {
        width = canvas.getWidth() - getPaddingLeft() - getPaddingRight();//控件实际大小
        height = canvas.getHeight() - getPaddingTop() - getPaddingBottom();


        if (!mIsShadow)
            mShadowRadius = 0;

       /* imageWidth = width - (int) mShadowRadius * 2 - (int) borderWidth * 2;
        imageHeight = height - (int) mShadowRadius * 2 - (int) borderWidth * 2;*/
        imageWidth = width - (int) mShadowRadius * 2;
        imageHeight = height - (int) mShadowRadius * 2;
        Bitmap image = drawableToBitmap(getDrawable());
        Bitmap reSizeImage = reSizeImage(image, imageWidth, imageHeight);
        initPaint();


       /* mRawBitmap = reSizeImage;
        BitmapShader mShader = new BitmapShader(mRawBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mPaintBitmap.setShader(mShader);
        Paint whitePaint = new Paint();
        whitePaint.setColor(R.color.white);
        // 首先画一个圆，填充的是边框的颜色,大小就是此控件设置的大小
        canvas.drawCircle(getPaddingLeft()+getPaddingTop()/2, getPaddingTop()+getPaddingTop()/2, 192, whitePaint);
        // 在边框的圆的基础上再画一个圆，画的是图片，半径 = 此控件设置的大小 - 边框宽度，就露出了边框
        canvas.drawCircle(getPaddingLeft()+getPaddingTop()/2, getPaddingTop()+getPaddingTop()/2, 184, mPaintBitmap);
*/


        if (mIsCircle) {
            Log.i("......RoundImageView", "...............1111");
            canvas.drawBitmap(createCircleImage(reSizeImage),
                    getPaddingLeft(), getPaddingTop(), null);

        } else {
            Log.i("......RoundImageView", "...............2222");
            canvas.drawBitmap(createRoundImage(reSizeImage),
                    getPaddingLeft(), getPaddingTop(), null);
        }
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }


    private Bitmap createRoundImage(Bitmap bitmap) {
        if (bitmap == null) {
            throw new NullPointerException("Bitmap can't be null");
        }
        BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        Bitmap targetBitmap = Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888);
        Canvas targetCanvas = new Canvas(targetBitmap);

        mPaint.setShader(bitmapShader);

        RectF rect = new RectF(0, 0, imageWidth, imageHeight);
        targetCanvas.drawRoundRect(rect, mRadius, mRadius, mPaint);

        if (mIsShadow) {
            mPaint.setShader(null);
            mPaint.setColor(mShadowColor);
            mPaint.setShadowLayer(mShadowRadius, 1, 1, mShadowColor);
            Bitmap target = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(target);

            RectF rectF = new RectF(mShadowRadius, mShadowRadius, width - mShadowRadius, height - mShadowRadius);
            canvas.drawRoundRect(rectF, mRadius, mRadius, mPaint);
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
            mPaint.setShadowLayer(0, 0, 0, 0xffffff);
            canvas.drawBitmap(targetBitmap, mShadowRadius, mShadowRadius, mPaint);
            return target;
        } else {
            return targetBitmap;
        }

    }


    private Bitmap createCircleImage(Bitmap bitmap) {
        if (bitmap == null) {
            throw new NullPointerException("Bitmap can't be null");
        }
        BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        Bitmap targetBitmap = Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888);
        Canvas targetCanvas = new Canvas(targetBitmap);

        mPaint.setShader(bitmapShader);

        targetCanvas.drawCircle(imageWidth / 2, imageWidth / 2, Math.min(imageWidth, imageHeight) / 2,
                mPaint);

        if (mIsShadow) {
            Log.i("setImageResource",".......mIsShadow");
            mPaint.setShader(null);
            mPaint.setColor(mShadowColor);
            mPaint.setShadowLayer(mShadowRadius, 1, 1, mShadowColor);
            Bitmap target = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(target);

            canvas.drawCircle(width / 2, height / 2, Math.min(imageWidth, imageHeight) / 2,
                    mPaint);
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
            mPaint.setShadowLayer(0, 0, 0, 0xffffff);
            canvas.drawBitmap(targetBitmap, mShadowRadius, mShadowRadius, mPaint);
            return target;
        } else {
            return targetBitmap;
        }

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    /**
     * drawable转bitmap
     *
     * @param drawable
     * @return
     */
    private Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable == null) {
            return null;
        } else if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicHeight(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * 重设Bitmap的宽高
     *
     * @param bitmap
     * @param newWidth
     * @param newHeight
     * @return
     */
    private Bitmap reSizeImage(Bitmap bitmap, int newWidth, int newHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        // 计算出缩放比
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 矩阵缩放bitmap
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
    }


}
