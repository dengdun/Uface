package com.whzxw.uface.ether.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.uniubi.uface.ether.R;


public class SelectPhotoDialog extends Dialog implements View.OnClickListener {
	private Context mContext;
	public SelectPhotoDialog(Context context, OnButtonClickListener onButtonClickListener) {
		super(context);
		mContext = context;
		this.listener = onButtonClickListener;
	}
	public interface OnButtonClickListener{
		public void Onclick(int v);
	}

	private Button btnOne;
	private Button btnTwo;
	private Button btnThree;


	private OnButtonClickListener listener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        Window window = getWindow();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
       // window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//背景全部变暗
        //设置dialog在屏幕底部
        window.setGravity(Gravity.BOTTOM);
        //设置dialog弹出时的动画效果，从屏幕底部向上弹出
        window.getDecorView().setPadding(0, 0, 0, 0);
        //获得window窗口的属性
        WindowManager.LayoutParams lp = window.getAttributes();
        //设置窗口宽度为充满全屏
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        //设置窗口高度为包裹内容
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.dimAmount = 0.44f;
        //将设置好的属性set回去
        window.setAttributes(lp);
		
		View v = LayoutInflater.from(mContext).inflate(R.layout.dialog_bottom_select, null);
		btnOne = (Button) v.findViewById(R.id.dialog_camera);
		btnTwo = (Button) v.findViewById(R.id.dialog_pictrue);
		btnThree = (Button) v.findViewById(R.id.dialog_avatar_cancle);
		btnOne.setOnClickListener(this);
		btnTwo.setOnClickListener(this);
		btnThree.setOnClickListener(this);
		this.setContentView(v);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dialog_camera:
			listener.Onclick(0);
			break;
		case R.id.dialog_pictrue:
			listener.Onclick(1);
			break;
		case R.id.dialog_avatar_cancle:
			listener.Onclick(2);
			break;
		default:
			break;
		}
	}
}
