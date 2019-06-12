package com.uniubi.uface.ether.bean;

import com.uniubi.faceapi.CvFace;

/**
 * @author qiaopeng
 * @date 2018/8/3
 */
public class VerifyObj {

    private byte[] cameraData;

    private CvFace[] faces;

    public VerifyObj() {
    }

    public VerifyObj(byte[] cameraData, CvFace[] faces) {
        this.cameraData = cameraData;
        this.faces = faces;
    }

    public byte[] getCameraData() {
        return cameraData;
    }

    public void setCameraData(byte[] cameraData) {
        this.cameraData = cameraData;
    }

    public CvFace[] getFaces() {
        return faces;
    }

    public void setFaces(CvFace[] faces) {
        this.faces = faces;
    }
}
