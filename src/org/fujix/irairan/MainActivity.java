package org.fujix.irairan;

import android.app.*;
import android.os.*;
import android.util.*;
import android.view.*;

public class MainActivity extends Activity
{
	GameSurfaceView mView;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
		Log.d("MainActivity", "MainActivityStart!");
		
        super.onCreate(savedInstanceState);

		//フルスクリーンに設定
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		//画面のタイムアウト防止 
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		mView = new GameSurfaceView(this);
		setContentView(mView);
		
		// センサー初期化
//		AcSensor.GetInstance().onCreate(this);
    }

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
//		System.exit(0);
	}
	
	@Override
	protected void onResume()
	{	// アクティビティが動き始める時呼ばれる
		super.onResume();
//		AcSensor.GetInstance().onResume();// 開始時にセンサーを動かし始める

		System.gc();
	}

	@Override
	protected void onPause()
	{	// アクティビティの動きが止まる時呼ばれる
		super.onPause();
//		AcSensor.GetInstance().onPause();// 中断時にセンサーを止める
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{	//ボタンが押された時に呼ばれる
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{	//戻るボタンなら
			Log.d("MainActivity", "KEY_BACK!");
			mView = null;
			System.gc();
		}
		//それ以外のボタンなら標準の動きをさせる
		return super.onKeyDown(keyCode, event);
	}
}
