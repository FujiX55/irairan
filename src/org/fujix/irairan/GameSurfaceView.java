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
	
	private boolean mActive = true;
	
	public static final float STG_WIDTH  = 480.0f;
	public static final float STG_HEIGHT = 800.0f;
	
	private Viewport mViewport;
	
	public GameSurfaceView(Context context)
	{
		super(context);
		getHolder().addCallback(this);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
	{
		//解像度情報変更通知
		float scale_x = width /STG_WIDTH;
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

		mViewport.W = (int)(width /mScale);
		mViewport.H = (int)(height/mScale);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{
		mThread   = new Thread(this);             //別スレッドでメインループを作る
		mGameMgr  = new GameMgr();
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
			if (mActive == false)
			{
				mActive = true;
				mViewport.x = 0;
				mViewport.y = 0;
				// 再始動
				mGameMgr = null;
				System.gc();
				mGameMgr = new GameMgr();
			}
			mGameMgr.onUpdate();
			onDraw(getHolder());			
		}
	}

	private void onDraw(SurfaceHolder holder)
	{
		float x = 0.0f;
		float y = 0.0f;
		
		Canvas c = holder.lockCanvas();
		if (c == null)
		{
			return;
		}
		//ここにゲームの描画処理を書く
		if (mGameMgr != null)
		{
			y = mGameMgr.getPlayer().getPt()._y;			
		}
		c.save(Canvas.CLIP_SAVE_FLAG);
		c.scale(mScale, mScale);

		// 自機の位置にあわせてビューポートを移動する
		mViewport.move(x, y);
		c.translate(0, -mViewport.y);			
		
		mGameMgr.onDraw(c);
		c.restore();

		holder.unlockCanvasAndPost(c);
	}

	@Override
    public boolean onTouchEvent(MotionEvent e)
	{	// タッチセンサーによる操作
		if (mGameMgr == null)
		{
			return true;
		}	
		switch (e.getAction())
		{
			case MotionEvent.ACTION_DOWN:
				if (mGameMgr.isFinished())
				{
					Log.d("GameSurfaceView", "Restart!");
					mActive = false;
				}
				else
				{
					mGameMgr.initTouchPoint(e.getX(), e.getY());
				}
				break;
			case MotionEvent.ACTION_UP:
				break;
			case MotionEvent.ACTION_MOVE:
				if (!mGameMgr.isFinished())
				{
					mGameMgr.setTouchPoint(e.getX(), e.getY());
				}
				break;
			default:
				break;
        }		
        return true;
    }
}
