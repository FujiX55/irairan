package org.fujix.irairan;

import android.graphics.*;
import java.util.*;
import android.widget.*;
import android.util.*;
import android.content.*;

public class Player extends Task
{
	private Bitmap mPic;
	private int mPic_w, mPic_h;

	private final static float MAX_SPEED = 20;		//移動する最大スピード
	private final static float SIZE = 20;			//自機の大きさ
	private Circle mCir       = null;             	//自機の円
	private Paint  mPaint     = new Paint();     	//描画設定
	private Vec    mVec       = new Vec();          //自機の移動ベクトル
	private Vec    mSensorVec = new Vec();			//センサーのベクトル

	private int    mLife;

	public final static int LIFE_MAX = 100;

	private boolean mDamaged = false;

	Rect mRcSrc = new Rect();
	Rect mRcDst = new Rect();
	Matrix mMat = new Matrix();
	
	private final boolean DEBUG_DRAW = false;
	
	public Player(Context c)
	{
		mPic = BitmapFactory.decodeResource(c.getResources(), R.drawable.ic_launcher);
		mPic_w = mPic.getWidth();
		mPic_h = mPic.getHeight();
		
		mCir = new Circle(240, 50, SIZE);	//(240,0)の位置にSIZEの大きさの円を作る
		mPaint.setColor(Color.BLUE);      	//色を青に設定
		mPaint.setAntiAlias(true);        	//エイリアスをオン
		mLife = LIFE_MAX;
		
		mVec._y = -0.001f;
	}

	@Override
	protected void finalize() throws Throwable
	{
		try
		{
			super.finalize();
		}
		finally
		{
			mCir 		= null;
			mSensorVec 	= null;
			mVec 		= null;
			mPaint 		= null;
			mRcSrc 		= null;
			mRcDst 		= null;
			mMat 		= null;
			
			Log.d("Player", "PlayerDestruct");
		}
	}

	/**
	 * 自機中心円を取得する
	 */
	public final Circle getPt()
	{
		return mCir;
	}

	/**
	 * ベクトルをセットする
	 */
	private void setVec()
	{
		mVec.blend(mSensorVec, 0.05f);        //センサーのベクトル方向に実際の移動ベクトルを5%近づける
	}

	/**
	 * 移動ベクトルの向いている方に動かす
	 */
	private void Move()
	{
		mCir._x += mVec._x;     //移動ベクトルmVecが指す方向に移動させる 
		mCir._y += mVec._y;
	}

	/**
	 * // 指定された場所へ動かす
	 */
	public void MoveTo(float dest_x, float dest_y)
	{
		float distance_x = dest_x - mCir._x;
		float distance_y = dest_y - mCir._y;

		mCir._x = mCir._x  + distance_x / 10;	
		mCir._y = mCir._y  + distance_y / 10;	
	}

	/**
	 * 自機の移動量を設定
	 */
	public void setMove(PointF old, PointF now)
	{
		float x = now.x - old.x;
		float y = now.y - old.y;

		mSensorVec._x = x;
		mSensorVec._y = y;
		mSensorVec.setLengthCap(MAX_SPEED);     	//ベクトルの大きさが最大スピード以上にならないようにする           
	}

	/**
	 * センサーベクトルの初期化
	 */
	public void resetSensorVec()
	{
		mSensorVec._x = mSensorVec._y = 0;
	}

	/**
	 * 自機の更新
	 */
	@Override
	public boolean onUpdate()
	{
		setVec();       //移動ベクトルをセットする
		Move();         //移動ベクトルが向いている方に動かす
		
		return true;
	}

	/**
	 * 自機の描画
	 */
	@Override
	public void onDraw(Canvas c)
	{
		// キャラ画像の回転行列の生成
		final int w  = mPic_w; 			// 描画する幅
		final int h  = mPic_h; 			// 描画する高さ
		final int sx = 0; 				// 画像内の左上座標X
		final int sy = 0; 				// 画像内の左上座標Y
		final int dx = (int)mCir._x; 	// 描画先の左上座標X
		final int dy = (int)mCir._y; 	// 描画先の左上座標Y

		double angle = 0; 				// 回転角度(度)
		
		angle = Math.atan2(mVec._x, -mVec._y) * 180 / Math.PI;
		
		mRcSrc.set(sx,sy,sx+w,sy+h);
		mRcDst.set(dx-w/2,dy-h/2, dx+w/2, dy+h/2);

		mMat.setRotate((float)angle, dx, dy);

		// キャラ画像の表示
		c.save();
		c.concat(mMat);
		c.drawBitmap(mPic, mRcSrc, mRcDst, null);
		c.restore();

		// 当たり判定の描画
		if ( DEBUG_DRAW )
		{
			if (mDamaged)
			{	// ダメージ表現
				mDamaged = false;			
				mPaint.setColor(Color.RED);
			}
			else
			if (mVec.getLength() > 3.0f)
			{   // 速度がある時はシアンで描画する
				mPaint.setColor(Color.CYAN);
			}
			else
			{   // 通常は青で描画
				mPaint.setColor(Color.BLUE);
			}
			c.drawCircle(mCir._x, mCir._y, mCir._r, mPaint);
		}

		// 移動ベクトルの描画
		mPaint.setColor(Color.GREEN);
		c.drawLine( mCir._x, 
				    mCir._y, 
				    mCir._x + mVec._x * 10, 
				    mCir._y + mVec._y * 10, 
				    mPaint );

		// ライフの描画
		c.drawRect(new RectF(mCir._x - SIZE * mLife / LIFE_MAX, 
							 mCir._y - SIZE - 10,
							 mCir._x + SIZE * mLife / LIFE_MAX, 
							 mCir._y - SIZE - 5),
				   mPaint);
	}

	/**
	 * 自機のライフを減らす
	 */
	public int onDamage()
	{
		if (0 < mLife)
		{
			mLife -= 10;
		}
		mDamaged = true;
		
		return mLife;
	}
}
