package org.fujix.irairan;

import android.app.*;
import android.os.*;
import android.util.*;
import android.view.*;

public class MainActivity extends Activity
{
	GameView mView;

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

		mView = new GameView(this);
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
	
	/**
	 * アクティビティが動き始める時呼ばれる
	 */
	@Override
	protected void onResume()
	{
		super.onResume();
//		AcSensor.GetInstance().onResume();// 開始時にセンサーを動かし始める

		System.gc();
	}

	/**
	 * アクティビティの動きが止まる時呼ばれる
	 */
	@Override
	protected void onPause()
	{
		super.onPause();
//		AcSensor.GetInstance().onPause();// 中断時にセンサーを止める
	}

	/**
	 * ボタンが押された時に呼ばれる
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
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
