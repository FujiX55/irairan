package org.fujix.irairan;

import android.app.*;
import android.os.*;
import android.view.View.*;
import android.widget.*;
import android.view.*;
import android.content.*;

public class StartActivity extends Activity implements OnClickListener
{	// スタート画面のアクティビティ
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		//フルスクリーンに設定
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.start);
		
		// スタートボタンを登録
		Button startButton = (Button)findViewById(R.id.startButton);
		startButton.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v)
	{	// スタートボタン押下でゲーム画面に遷移する
		Intent intent = new Intent();
		
		intent.setClassName("org.fujix.irairan", "org.fujix.irairan.MainActivity");
		
		startActivity(intent);
	}
}
