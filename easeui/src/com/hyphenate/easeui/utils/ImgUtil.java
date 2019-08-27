package com.hyphenate.easeui.utils;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.hyphenate.easeui.R;

import java.io.File;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by Administrator on 2017/7/21.
 * 图片加载工具类
 */

public class ImgUtil {

    public static void load(Context context, String imgUrl, final ImageView imageView) {
        if (null == context) {
            if (context instanceof Activity) {
                if (((Activity) context).isFinishing() || ((Activity) context).isDestroyed()) {
                    return;
                }
            }
            return;
        }
        if (!isNullTxt(imgUrl) && null != imageView)
            Glide.with(context)
                    .load(imgUrl)
                    .placeholder(R.mipmap.head)
                    .error(R.mipmap.head)
                    .dontAnimate()
                    .into(imageView);
    }

    public static void loadHead(Context context, String imgUrl, final ImageView imageView) {
        if (null == context) {
            if (context instanceof Activity) {
                if (((Activity) context).isFinishing() || ((Activity) context).isDestroyed()) {
                    return;
                }
            }
            return;
        }
        if (!isNullTxt(imgUrl) && null != imageView)
            Glide.with(context)
                    .load(imgUrl)
                    .placeholder(R.mipmap.head)
                    .error(R.mipmap.head)
                    .dontAnimate()
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            imageView.setImageResource(R.mipmap.head);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            imageView.setImageDrawable(resource);
                            return false;
                        }
                    })
                    .into(imageView);
    }

    public static void load(Context context, int imgId, ImageView imageView) {
        if (null == context) {
            if (context instanceof Activity) {
                if (((Activity) context).isFinishing() || ((Activity) context).isDestroyed()) {
                    return;
                }
            }
            return;
        }
        Glide.with(context).load(imgId)
                .dontAnimate().into(imageView);
    }

    public static void loadGif(Context context, int imgId, ImageView imageView) {
        if (null == context) {
            if (context instanceof Activity) {
                if (((Activity) context).isFinishing() || ((Activity) context).isDestroyed()) {
                    return;
                }
            }
            return;
        }
        Glide.with(context).load(imgId).asGif()
                .dontAnimate().into(imageView);
    }

    public static void setGlideHead(Context context, String avatar, ImageView imageView) {
        if (null == context) {
            if (context instanceof Activity) {
                if (((Activity) context).isFinishing() || ((Activity) context).isDestroyed()) {
                    return;
                }
            }
            return;
        }
        if (!isNullTxt(avatar) && null != imageView)
            try {
            if (((Activity)context).isDestroyed()||((Activity)context).isFinishing()){
                return;
            }
                Glide.with(context)
                        .load(avatar).bitmapTransform(new CropCircleTransformation(context))
                        .placeholder(R.mipmap.head)
                        .error(R.mipmap.head)
                        .into(imageView);
            } catch (Exception e) {
                e.printStackTrace();
            }

    }


    public static void setGlideHead(Context context, String avatar, int p, ImageView imageView) {
        if (null == context) {
            if (context instanceof Activity) {
                if (((Activity) context).isFinishing() || ((Activity) context).isDestroyed()) {
                    return;
                }
            }
            return;
        }
        if (!isNullTxt(avatar) && null != imageView)
            Glide.with(context)
                    .load(avatar)
                    .placeholder(p)
                    .into(imageView);
    }


    public static void setGlideHead(Context context, int mipmapR, ImageView imageView) {
        if (null == context) {
            if (context instanceof Activity) {
                if (((Activity) context).isFinishing() || ((Activity) context).isDestroyed()) {
                    return;
                }
            }
            return;
        }
        if (null != imageView)
            Glide.with(context).load(mipmapR).into(imageView);
    }

    public static void setGlideHead(Context context, File File, ImageView imageView) {
        if (null == context) {
            if (context instanceof Activity) {
                if (((Activity) context).isFinishing() || ((Activity) context).isDestroyed()) {
                    return;
                }
            }
            return;
        }
        if (null != imageView)
            Glide.with(context).load(File).into(imageView);
    }

    public static void setGlideTransformHead(Context context, int logo, int radius, ImageView imageView) {
        if (null == context) {
            if (context instanceof Activity) {
                if (((Activity) context).isFinishing() || ((Activity) context).isDestroyed()) {
                    return;
                }
            }
            return;
        }
        if (null != imageView)
            Glide.with(context)
                    .load(logo).bitmapTransform(new BlurTransformation(context, 15)).into(imageView);
    }

    public static void setGlideTransformHead(Context context, String logo, int radius, ImageView imageView) {
        if (null == context) {
            if (context instanceof Activity) {
                if (((Activity) context).isFinishing() || ((Activity) context).isDestroyed()) {
                    return;
                }
            }
            return;
        }
        if (null != imageView)
            Glide.with(context)
                    .load(logo).bitmapTransform(new BlurTransformation(context, 15)).into(imageView);
    }

    public static void setGlideErrorHead(Context context, String imgUrl, int errorR, ImageView imageView) {
        if (null == context) {
            if (context instanceof Activity) {
                if (((Activity) context).isFinishing() || ((Activity) context).isDestroyed()) {
                    return;
                }
            }
            return;
        }
        if (null != imageView)
            Glide.with(context).load(imgUrl).error(errorR).into(imageView);
    }

    public static void setGlidePlaceholderHead(Context context, String imgUrl, int placeholder, ImageView imageView) {
        if (null == context) {
            if (context instanceof Activity) {
                if (((Activity) context).isFinishing() || ((Activity) context).isDestroyed()) {
                    return;
                }
            }
            return;
        }
        if (null != imageView)
            Glide.with(context).load(imgUrl).placeholder(placeholder).into(imageView);
    }

    public static boolean isNullTxt(String txt) {
        return null == txt || txt.length() == 0;
    }

}
