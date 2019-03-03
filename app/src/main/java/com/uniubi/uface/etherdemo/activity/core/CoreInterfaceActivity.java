package com.uniubi.uface.etherdemo.activity.core;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.uniubi.faceapi.CvFace;
import com.uniubi.uface.etherdemo.R;
import com.uniubi.uface.ether.core.EtherFaceManager;
import com.uniubi.uface.ether.core.cvhandle.FaceHandler;
import com.uniubi.uface.ether.core.exception.CvFaceException;
import com.uniubi.uface.etherdemo.utils.PhotoUtils;
import com.uniubi.uface.etherdemo.view.dialog.SelectPhotoDialog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author qiaopeng
 * @date 2018/8/17
 */
public class CoreInterfaceActivity extends AppCompatActivity {
    @BindView(R.id.btn_track)
    Button btnTrack;
    @BindView(R.id.btn_detect)
    Button btnDetect;
    @BindView(R.id.btn_getfeatrue)
    Button btnGetfeatrue;
    @BindView(R.id.btn_compare)
    Button btnCompare;
    @BindView(R.id.text_result)
    TextView textResult;
    @BindView(R.id.btn_select)
    Button btnSelect;
    @BindView(R.id.image_photo)
    AppCompatImageView imagePhoto;
    @BindView(R.id.btn_saveone)
    Button btnSaveone;
    @BindView(R.id.btn_savetwo)
    Button btnSavetwo;
    @BindView(R.id.btn_nsaveone)
    Button btnNsaveone;
    @BindView(R.id.btn_nsavetwo)
    Button btnNsavetwo;
    @BindView(R.id.btn_ncompare)
    Button btnNcompare;
    private Bitmap bitmap;
    SelectPhotoDialog dialog;
    // 拍照
    private static final int PHOTO_REQUEST_CAMERA = 1;
    // 从相册中选择
    private static final int PHOTO_REQUEST_GALLERY = 2;
    // 文件名称
    private static final String PHOTO_FILE_NAME = "image.png";

    private String filePath = Environment.getExternalStorageDirectory() + "/tmp/";

    private FaceHandler faceHandler;
    private CvFace[] cvFaces;
    private int count = 0;

    private byte[] feature;
    private byte[] feature1;
    private byte[] feature2;
    private List<float[]> increListOne = new ArrayList<>();
    private List<float[]> increListTwo = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coreinterface);
        ButterKnife.bind(this);
        mkdirs("tmp");
        init();


    }

    private void init() {
        faceHandler = new FaceHandler();
        faceHandler.init();

        //初始化增量比对
        faceHandler.compareInit(256, 100);
    }

    @OnClick({R.id.btn_track, R.id.btn_detect, R.id.btn_getfeatrue, R.id.btn_compare, R.id.btn_select, R.id.btn_saveone,
            R.id.btn_savetwo, R.id.btn_nsaveone, R.id.btn_nsavetwo, R.id.btn_ncompare})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_track:
                count++;
                if (bitmap != null) {
                    try {
                        cvFaces = faceHandler.trackBGR(bitmap);
                        if (cvFaces != null) {
                            StringBuilder result = new StringBuilder();
                            for (CvFace cvFace : cvFaces) {
                                result.append(cvFace.toString());
                                result.append("\n");
                            }
                            textResult.setText(result);
                        } else {
                            //track是针对连续帧的数据的，因此只track一次的话是不会有数据的。
                            if (count == 1) {
                                Toast.makeText(CoreInterfaceActivity.this, "请再点击一次", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(CoreInterfaceActivity.this, "请检查人脸朝向是否正确", Toast.LENGTH_SHORT).show();
                            }
                        }

                    } catch (CvFaceException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.btn_detect:
                if (bitmap != null) {
                    try {
                        cvFaces = faceHandler.detectBGR(bitmap);
                        if (cvFaces != null) {
                            StringBuilder result = new StringBuilder();
                            for (CvFace cvFace : cvFaces) {
                                result.append(cvFace.toString());
                                result.append("\n");
                            }
                            textResult.setText(result);
                        } else {
                            Toast.makeText(CoreInterfaceActivity.this, "请检查人脸朝向是否正确", Toast.LENGTH_SHORT).show();
                        }

                    } catch (CvFaceException e) {
                        e.printStackTrace();
                    }
                }

                break;
            case R.id.btn_getfeatrue:
                if (bitmap != null && cvFaces != null && cvFaces.length > 0) {
                    try {
                        //默认提取第一个人脸
                        feature = faceHandler.getFeatureBGR(bitmap, cvFaces[0]);
                        if (feature != null) {
                            textResult.setText(Arrays.toString(feature));
                        }
                    } catch (CvFaceException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.btn_saveone:
                feature1 = feature;
                break;
            case R.id.btn_savetwo:
                feature2 = feature;
                break;
            case R.id.btn_compare:

                float score = 0;
                if (feature1 != null && feature2 != null) {
                    try {
                        score = faceHandler.comapreFeature(feature1, feature2);
                    } catch (CvFaceException e) {
                        e.printStackTrace();
                    }
                }
                textResult.setText("比对得分：  " + score);
                break;
            case R.id.btn_nsaveone:
                if (feature != null) {
                    increListOne.add(faceHandler.featureBytes2Floats(feature));
                    btnNsaveone.setText("LEFT增量保存一个特征值" + increListOne.size());
                }
                break;
            case R.id.btn_nsavetwo:
                if (feature != null) {
                    increListTwo.add(faceHandler.featureBytes2Floats(feature));
                    btnNsavetwo.setText("RIGHT增量保存一个特征值" + increListTwo.size());
                }
                break;
            case R.id.btn_ncompare:
                if (!TextUtils.isEmpty(compareTopN())) {
                    textResult.setText(compareTopN());
                }
                break;
            case R.id.btn_select:
                dialog = new SelectPhotoDialog(CoreInterfaceActivity.this,
                        new SelectPhotoDialog.OnButtonClickListener() {
                            @Override
                            public void Onclick(int v) {
                                switch (v) {
                                    case 0:
                                        camera();
                                        break;
                                    case 1:
                                        gallery();
                                    default:
                                        break;
                                }
                                dialog.dismiss();
                            }
                        });
                dialog.show();

                break;

            default:
                break;
        }
    }

    private String compareTopN() {

        if (increListOne.size() <= 0 || increListTwo.size() <= 0) {
            Toast.makeText(this, "请添加人脸特征值", Toast.LENGTH_SHORT).show();
            return null;
        }

        for (int i = 0; i < increListOne.size(); i++) {
            faceHandler.addOneCompareRecord(increListOne.get(i), "" + i);
        }

        float[][] featuresToCompare = new float[increListTwo.size()][];
        for (int i = 0; i < increListTwo.size(); i++) {
            featuresToCompare[i] = increListTwo.get(i);
        }

        return faceHandler.compareFeatureTopK(featuresToCompare);
    }

    private static void mkdirs(String dirPath) {
        File sdRoot = Environment.getExternalStorageDirectory();
        String dir = dirPath;
        File mkDir = new File(sdRoot, dir);
        if (!mkDir.exists()) {
            mkDir.mkdirs();
        }
    }

    /**
     * 从相册获取
     */
    public void gallery() {
        // 激活系统图库，选择一张图片
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        try {
            startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 从相机获取
     */
    public void camera() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        // 判断存储卡是否可以用，可用进行存储
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(new File(filePath,
                            PHOTO_FILE_NAME)));
        }
        try {
            startActivityForResult(intent, PHOTO_REQUEST_CAMERA);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PHOTO_REQUEST_GALLERY) {
            if (data != null) {
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imagePhoto.setImageBitmap(bitmap);
            }

        } else if (requestCode == PHOTO_REQUEST_CAMERA) {
            bitmap = PhotoUtils.getSmallBitmap(filePath + PHOTO_FILE_NAME);
            imagePhoto.setImageBitmap(bitmap);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (bitmap != null) {
            bitmap.recycle();
        }
    }
}
