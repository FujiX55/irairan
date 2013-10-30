package org.fujix.irairan;

import android.graphics.*;

public class FpsController extends Task
{
	
	private long  mStartTime = 0;            //測定開始時刻
	private int   mCnt   = 0;                  //カウンタ
	private Paint mPaint = new Paint();     //paint情報
	private float mFps;                     //fps
	private final static int N = 60;        //平均を取るサンプル数
	private final static int FONT_SIZE = 20;//フォントサイズ

	public FpsController()
	{
		mPaint.setColor(Color.BLUE);    //フォントの色を青に設定
		mPaint.setTextSize(FONT_SIZE);  //フォントサイズを設定
	}

	@Override
	public boolean onUpdate()
	{
		if (mCnt == 0)
		{ //1フレーム目なら時刻を記憶
			mStartTime = System.currentTimeMillis();
		}
		if (mCnt == N)
		{ //60フレーム目なら平均を計算する
			long t = System.currentTimeMillis();
			mFps = 1000.f / ((t - mStartTime) / (float)N);
			mCnt = 0;
			mStartTime = t;
		}
		mCnt++;
		return true;
	}

	@Override
	public void onDraw(Canvas c)
	{
		c.drawText(String.format("%.1f", mFps), 0, FONT_SIZE - 2, mPaint);
	}
}
