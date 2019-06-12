package com.uniubi.uface.ether.bean;

import android.graphics.PointF;
import android.graphics.Rect;

import com.uniubi.faceapi.CvFace;


/**
 * 用途：当前检测的画面用于绘制人脸框
 * 作者： wangkangle
 * Email: wkl_2052@qq.com
 * weixin: feimeng16
 * 版本：1.0
 * 创建日期：Administrator on 2017-12-01 17:50
 */

public class CheckFace {

    private Rect rects;//人脸框
    private PointF[] mFacePoints;//人脸点
    private boolean identifyed = false;//标记是否识别过

    private CvFace cvFace;
    private int maxCount;//IR检测最大次数


    public Rect getRects() {
        return rects;
    }

    public void setRects(Rect rects) {
        this.rects = rects;
    }

    public PointF[] getmFacePoints() {
        return mFacePoints;
    }

    public void setmFacePoints(PointF[] facePoints) {
        if (mFacePoints == null){
            mFacePoints = new PointF[facePoints.length];
        }
        System.arraycopy(facePoints,0,mFacePoints, 0,facePoints.length);
    }

    public boolean isIdentifyed() {
        return identifyed;
    }

    public void setIdentifyed(boolean identifyed) {
        this.identifyed = identifyed;
    }

    public CvFace getCvFace() {
        return cvFace;
    }

    public void setCvFace(CvFace cvFace) {
        this.cvFace = cvFace;
    }

    public int getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }

    @Override
    public String toString() {
        return "CheckFace{" +
                "rects=" + rects +
//                ", mFacePoints=" + Arrays.toString(mFacePoints) +
                ", identifyed=" + identifyed +
                '}';
    }
}
