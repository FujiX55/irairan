package org.fujix.irairan;

import android.content.*;
import android.graphics.*;
import android.util.*;
import android.view.*;

public class GameView extends SurfaceView implements SurfaceHolder.Callback, Runnable
{
	private GameDirector	mDirector;
	private Thread	mThread;

	private float   mScale = 1.0f;

	private boolean mRestart = false;

	public static final float STG_WIDTH  = 480.0f;
	public static final float STG_HEIGHT = 800.0f;

	private Viewport mViewport;

	private Context mContext;

	public GameView(Context context)
	{
		super(context);
		getHolder().addCallback(this);

		mContext = context;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
	{
		//解像度情報変更通知
		float scale_x = width / STG_WIDTH;
//		float scale_y = height/STG_HEIGHT;

//		if (scale_x > scale_y)
//		{
//			mScale = scale_y;
//		}
//		else
//		{
//			mScale = scale_x;
//		}
		mScale = scale_x;
//		mScale = scale_x * 2;

		mViewport.W = (int)(width  / mScale);
		mViewport.H = (int)(height / mScale);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{
		mThread   = new Thread(this);             //別スレッドでメインループを作る
		mDirector = new GameDirector(mContext, 1);
		mViewport = new Viewport(STG_WIDTH, STG_HEIGHT);

		mThread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{
		mThread   = null;
		mDirector = null;
		mViewport = null;

		Log.d("GameView", "SurfaceDestroyed");
	}

	@Override
	public void run()
	{
		while (mThread != null)
		{ 	//メインループ
			if (mRestart)
			{
				mRestart = false;
				// プレイ再開
				onRestart();
			}
			mDirector.onUpdate();
			onDraw(getHolder());			
		}
	}

	private void onRestart()
	{
		// ビューポートの初期化
		mViewport.x = 0;
		mViewport.y = 0;
		// 再始動
		mDirector = null;
		System.gc();
		mDirector = new GameDirector(mContext, 2);		
	}
	
	private void onDraw(SurfaceHolder holder)
	{
		Canvas c = holder.lockCanvas();
		
		if (c != null)
		{
			//ここにゲームの描画処理を書く
			if (mDirector != null)
			{
				float x = mDirector.getPlayer().getPt()._x;			
				float y = mDirector.getPlayer().getPt()._y;			
				
				c.save(Canvas.CLIP_SAVE_FLAG);
//				c.save(Canvas.ALL_SAVE_FLAG);
				c.scale(mScale, mScale);

				// 自機の位置にあわせてビューポートを移動する
				mViewport.update(x, y);
				c.translate(-mViewport.x, -mViewport.y);			
//				c.rotate(45.0f);
				
				mDirector.onDraw(c);
				c.restore();
				
				// ※元の座標に描画したいがうまく行ってない
				c.translate(+mViewport.x, +mViewport.y);			
				mDirector.onDrawStatus(c);
			}
			holder.unlockCanvasAndPost(c);
		}
	}

	@Override
    public boolean onTouchEvent(MotionEvent e)
	{	// タッチセンサーによる操作
		if (mDirector == null)
		{
			return true;
		}
		// 状態に応じてタッチ操作の反応を切替える
		if (mDirector.isFinished())
		{	// プレイ終了している場合
			switch (e.getAction())
			{
				case MotionEvent.ACTION_DOWN:
					// ゲーム再開
					Log.d("GameView", "Restart!");
					mRestart = true;
					break;
				default:
					break;
			}		

		}
		else
		{	// プレイ中
			switch (e.getAction())
			{
				case MotionEvent.ACTION_DOWN:
					// タッチ開始位置をセットする
					mDirector.initTouchPoint(e.getX(), e.getY());
					break;
				case MotionEvent.ACTION_MOVE:
					// 移動中のタッチ位置を更新する
					mDirector.setTouchPoint(e.getX(), e.getY());
					break;
				default:
					break;
			}		
		}
        return true;
    }
}
