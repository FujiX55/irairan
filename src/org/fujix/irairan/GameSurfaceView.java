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
	
	private PointF  mPtNow;
	private PointF  mPtOld;

	private boolean mActive = true;
	
	private static final float STG_WIDTH  = 480.0f;
	private static final float STG_HEIGHT = 800.0f;
	
	private ViewPort mViewPort;
	
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

		mViewPort.W = (int)(width /mScale);
		mViewPort.H = (int)(height/mScale);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{
		mThread   = new Thread(this);             //別スレッドでメインループを作る
		mGameMgr  = new GameMgr();
		mPtNow    = new PointF();
		mPtOld    = new PointF();
		mViewPort = new ViewPort(0, 0, 0, 0);
		
		mThread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{
		mThread   = null;
		mGameMgr  = null;
		mPtNow    = null;
		mPtOld    = null;
		mViewPort = null;

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
				mViewPort.x = 0;
				mViewPort.y = 0;
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

		final int ZONE_TOP    = 300;
		final int ZONE_BOTTOM = mViewPort.H - 300;
		
		// 自機の位置にあわせてビューポートを移動する
		if (mViewPort.y < -(y - ZONE_TOP))
		{
			mViewPort.y = -(int)(y - ZONE_TOP);
			// 画面の上端は越えない
			if (mViewPort.y > 0)
			{
				mViewPort.y = 0;
			}
		}
		if (mViewPort.y > -(y - ZONE_BOTTOM))
		{
			mViewPort.y = -(int)(y - ZONE_BOTTOM);
			// 画面の下端は越えない
			if (mViewPort.y < -(STG_HEIGHT - mViewPort.H))
			{
				mViewPort.y = -(int)(STG_HEIGHT - mViewPort.H);
			}
		}
		c.translate(0, mViewPort.y);			
		
		mGameMgr.onDraw(c);
		c.restore();

		holder.unlockCanvasAndPost(c);
	}

	@Override
    public boolean onTouchEvent(MotionEvent e)
	{	// タッチセンサーによる操作
        switch (e.getAction())
		{
			case MotionEvent.ACTION_DOWN:
				if (mGameMgr != null && !mGameMgr.isFinished())
				{
					mGameMgr.initTouchPoint(e.getX(), e.getY());
				}
				if (mGameMgr != null && mGameMgr.isFinished())
				{
					Log.d("GameSurfaceView", "Restart!");
					mActive = false;
				}
				break;
			case MotionEvent.ACTION_UP:
				if (mGameMgr != null && !mGameMgr.isFinished())
				{
//					mGameMgr.stopPlayer();
				}
				break;
			case MotionEvent.ACTION_MOVE:
				if (mGameMgr != null && !mGameMgr.isFinished())
				{
//					mPtNow.set(e.getX(), e.getY());
//					mGameMgr.movePlayer(mPtOld, mPtNow);
					mGameMgr.setTouchPoint(e.getX(), e.getY());
				}
				break;
			default:
				if (mGameMgr != null && !mGameMgr.isFinished())
				{
//					mGameMgr.stopPlayer();
				}
				break;
        }
		mPtOld.set(e.getX(), e.getY());
		
        return true;
    }
}
