package com.uniubi.uface.ether.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.uniubi.uface.etherdemo.R;
import com.uniubi.uface.ether.core.bean.CheckFace;

import java.util.List;

/**
 * Created by uniubi on 2017/3/15.
 */

public class FaceView extends View {
    private Context mContext;
    /**
     * 绘制人脸框位置最主要的参数，由UFaceKit返回
     */
    private List<CheckFace> mCheckFace;
    /**
     * 将选择的人脸框做成Bitmap
     */
    private Bitmap bitmap;
    private Bitmap passFaceBitmap;
    /**
     * 画笔工具
     */
    private Paint mLinePaint;
    /**
     * 人脸框id
     */
    private int id;

    private boolean isIR;

    private int generation = 0;

    public FaceView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        mContext = context;
    }

    /**
     * 调用此方法 用于绘制人脸框
     * rectFList UFaceKtit返回的参数，人脸框图片id；
     */
    public void setFaces(List<CheckFace> rects) {
        this.mCheckFace = rects;

        try {
            invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearRect() {
        mCheckFace = null;
        try {
            invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initPaint(boolean isIR, int generation) {
        this.generation = generation;
        this.isIR = isIR;
        if (isIR) {
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ir_face_img);
        } else {
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.face_frame);
            passFaceBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pass_face);
        }


        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //mLinePaint.setColor(0x8cFFFFFF);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(3f);
        //mLinePaint.setAlpha(180);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        try {
            if (mCheckFace != null) {
                for (CheckFace face : mCheckFace) {
                    if (!face.isCheckPass()){
                        continue;
                    }
                    Rect rect = null;
                    switch (generation) {
                        case 0:
                            rect = rotateDeg(face.getRects(), 640,
                                    480, this.getWidth(), this.getHeight());
                            break;
                        case 1:
                            rect = PoritraitRotateDeg1(face.getRects(), 640,
                               480, this.getWidth(), this.getHeight());
                            break;
                        case 2:
                            rect = poritraitRotateDeg(face.getRects(), 640,
                                480, this.getWidth(), this.getHeight());
                            break;
                        default:
                            break;
                    }

                    if (face.isIdentifyed()) {
                        mLinePaint.setColor(0xFF27FF50);
//                        canvas.drawRect(rect, mLinePaint);
                        canvas.drawBitmap(passFaceBitmap,null,rect,mLinePaint);
                    } else {
                        mLinePaint.setColor(0xccFFFFFF);
                        canvas.drawBitmap(bitmap,null,rect,mLinePaint);
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDraw(canvas);
    }


    public Rect poritraitRotateDeg(Rect rect, int width, int height, int w, int h) {
        int left = rect.left;
        int bottom = rect.bottom;
        int right = rect.right;
        int top = rect.top;

        //二代旋转
        rect.left = (height - bottom) * w / height;
        rect.bottom = right * h / width;
        rect.right = (height - top) * w / height;
        rect.top = left * h / width;


        return rect;
    }

    public Rect rotateDeg(Rect rect, int width, int height, int w, int h) {
        int left = rect.left;
        int bottom = rect.bottom;
        int right = rect.right;
        int top = rect.top;

        //一代处理
        rect.left = (int) ((width - right) * w / width);
        rect.bottom = (int) (bottom * h / height);
        rect.right = (int) ((width - left) * w / width);
        rect.top = (int) (top * h / height);

        return rect;
    }


    /**
     * 竖屏旋转方框
     *
     * @param rect
     * @param width
     * @param height
     * @return
     */
    public static Rect PoritraitRotateDeg1(Rect rect, int width, int height, int w, int h) {
        int left = rect.left;
        int bottom = rect.bottom;
        int right = rect.right;
        int top = rect.top;

        rect.left = top;
        rect.bottom = width - left;
        rect.right = bottom;
        rect.top = width - right;
        return rect;
    }
}
