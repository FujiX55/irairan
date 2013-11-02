package org.fujix.irairan;

import android.content.*;
import android.graphics.*;
import android.util.*;
import android.view.*;

public class GameSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable
{
	private GameMgr	mGameMgr;
	private Thread	mThread;

	private float   mScale = 1.0f;

	private boolean mRestart = false;

	public static final float STG_WIDTH  = 480.0f;
	public static final float STG_HEIGHT = 800.0f;

	private Viewport mViewport;

	private Context mContext;

	public GameSurfaceView(Context context)
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
		mScale = scale_x * 2;

		mViewport.W = (int)(width  / mScale);
		mViewport.H = (int)(height / mScale);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{
		mThread   = new Thread(this);             //別スレッドでメインループを作る
		mGameMgr  = new GameMgr(mContext);
		mViewport = new Viewport(STG_WIDTH, STG_HEIGHT);

		mThread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{
		mThread   = null;
		mGameMgr  = null;
		mViewport = null;

		Log.d("GameSurfaceView", "GameSurfaceDestroyed");
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
			mGameMgr.onUpdate();
			onDraw(getHolder());			
		}
	}

	private void onRestart()
	{
		// ビューポートの初期化
		mViewport.x = 0;
		mViewport.y = 0;
		// 再始動
		mGameMgr = null;
		System.gc();
		mGameMgr = new GameMgr(mContext);		
	}
	
	private void onDraw(SurfaceHolder holder)
	{
		Canvas c = holder.lockCanvas();
		
		if (c != null)
		{
			//ここにゲームの描画処理を書く
			if (mGameMgr != null)
			{
				float x = 0.0f;
				float y = 0.0f;

				x = mGameMgr.getPlayer().getPt()._x;			
				y = mGameMgr.getPlayer().getPt()._y;			
				
				c.save(Canvas.CLIP_SAVE_FLAG);
				c.scale(mScale, mScale);

				// 自機の位置にあわせてビューポートを移動する
				mViewport.update(x, y);
				c.translate(-mViewport.x, -mViewport.y);			

				mGameMgr.onDraw(c);
				c.restore();
			}
			holder.unlockCanvasAndPost(c);
		}
	}

	@Override
    public boolean onTouchEvent(MotionEvent e)
	{	// タッチセンサーによる操作
		if (mGameMgr == null)
		{
			return true;
		}
		// 状態に応じてタッチ操作の反応を切替える
		if (mGameMgr.isFinished())
		{	// プレイ終了している場合
			switch (e.getAction())
			{
				case MotionEvent.ACTION_DOWN:
					// ゲーム再開
					Log.d("GameSurfaceView", "Restart!");
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
					mGameMgr.initTouchPoint(e.getX(), e.getY());
					break;
				case MotionEvent.ACTION_MOVE:
					// 移動中のタッチ位置を更新する
					mGameMgr.setTouchPoint(e.getX(), e.getY());
					break;
				default:
					break;
			}		
		}
        return true;
    }
}
