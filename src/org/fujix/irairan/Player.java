package org.fujix.irairan;

import android.graphics.*;
import java.util.*;
import android.widget.*;
import android.util.*;

public class Player extends Task
{
	private final static float MAX_SPEED = 20;	//移動する最大スピード
	private final static float SIZE = 20;		//自機の大きさ
	private Circle mCir       = null;             	//自機の円
	private Paint  mPaint     = new Paint();     	//描画設定
	private Vec    mVec       = new Vec();           	//自機の移動ベクトル
	private Vec    mSensorVec = new Vec();			//センサーのベクトル
	
	private int    mLife;

	public Player()
	{
		mCir = new Circle(240, 50, SIZE);//(240,0)の位置にSIZEの大きさの円を作る
		mPaint.setColor(Color.BLUE);      //色を青に設定
		mPaint.setAntiAlias(true);        //エイリアスをオン
//		mVec._y = 2;                      //移動ベクトルを下に向ける
		mLife = 100;
	}

	@Override
	protected void finalize() throws Throwable {
		try {
			super.finalize();
		} finally {
			mCir 		= null;
			mSensorVec 	= null;
			mVec 		= null;
			mPaint 		= null;
			
			Log.d("Player", "PlayerDestruct");
		}
	}

	//自機中心円を取得する
	public final Circle getPt()
	{
		return mCir;
	}

	// ベクトルをセットする
	private void setVec()
	{
//		float x = -AcSensor.GetInstance().getX() * 2;    //加速度センサーの情報を取得
//		float y =  AcSensor.GetInstance().getY() * 2;
//		mSensorVec._x = x < 0 ? -x * x : x * x;     //2乗して変化を大袈裟にする
//		mSensorVec._y = y < 0 ? -y * y : y * y;     //2乗すると+になるので、負ならマイナスを付ける
//		mSensorVec.setLengthCap(MAX_SPEED);     //ベクトルの大きさが最大スピード以上にならないようにする           
//		mSensorVec._x = 0;
//		mSensorVec._y = 0;
		mVec.blend(mSensorVec, 0.05f);        //センサーのベクトル方向に実際の移動ベクトルを5%近づける
	}

	// 移動ベクトルの向いている方に動かす
	private void Move()
	{
		mCir._x += mVec._x;     //移動ベクトルmVecが指す方向に移動させる 
		mCir._y += mVec._y;
	}
	
	// 指定された場所へ動かす
	public void MoveTo(float dest_x, float dest_y)
	{
		float distance_x = dest_x - mCir._x;
		float distance_y = dest_y - mCir._y;
		mCir._x = mCir._x  + distance_x / 10;	
		mCir._y = mCir._y  + distance_y / 10;	
	}

	public void setMove(PointF old, PointF now)
	{
		float x = now.x - old.x;
		float y = now.y - old.y;
//		x *= 0.5f;
//		y *= 0.5f;
//		mSensorVec._x = x < 0 ? -x * x : x * x;     //2乗して変化を大袈裟にする
//		mSensorVec._y = y < 0 ? -y * y : y * y;     //2乗すると+になるので、負ならマイナスを付ける
		mSensorVec._x = x;
		mSensorVec._y = y;
		mSensorVec.setLengthCap(MAX_SPEED);     	//ベクトルの大きさが最大スピード以上にならないようにする           
//		mVec.blend(mSensorVec, 0.05f);        		//センサーのベクトル方向に実際の移動ベクトルを5%近づける
	}
	
	public void resetSensorVec()
	{
		mSensorVec._x = mSensorVec._y = 0;
	}
	
	@Override
	public boolean onUpdate()
	{
		setVec();       //移動ベクトルをセットする
		Move();         //移動ベクトルが向いている方に動かす
		return true;
	}

	@Override
	public void onDraw(Canvas c)
	{
		if (mVec.getLength() > 4.0f)
		{
			mPaint.setColor(Color.RED);
		}
		else
		{
			mPaint.setColor(Color.BLUE);
		}
		c.drawCircle(mCir._x, mCir._y, mCir._r, mPaint);
	}
	
	public void damage()
	{	// 自機のライフ
		if (0 < mLife)
		{
			mLife--;
		}
	}
}
