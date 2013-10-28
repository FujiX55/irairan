package org.fujix.irairan;

import android.content.*;
import android.graphics.*;
import android.util.*;
import android.view.*;

public class GameSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable
{
	private GameMgr	mGameMgr;
	private Thread	_thread;

	private float _scale = 1.0f;
	
	private PointF _nowXY;
	private PointF _oldXY;

	private boolean _active = true;
	
	private static final float BG_WIDTH  = 480.0f;
	private static final float BG_HEIGHT = 800.0f;
	
	private int mViewWidth, mViewHeight;
	private int mTransX, mTransY;

	public GameSurfaceView(Context context)
	{
		super(context);
		getHolder().addCallback(this);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
	{
		//解像度情報変更通知
		float scale_x = width /BG_WIDTH;
		float scale_y = height/BG_HEIGHT;
		
//		if (scale_x > scale_y)
//		{
//			_scale = scale_y;
//		}
//		else
//		{
//			_scale = scale_x;
//		}
		_scale = scale_x;

		mViewWidth  = (int)(width /_scale);
		mViewHeight = (int)(height/_scale);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{
		_thread = new Thread(this);             //別スレッドでメインループを作る
		mGameMgr = new GameMgr();
		_nowXY = new PointF();
		_oldXY = new PointF();

		_thread.start();
		
		mTransX = 0;
		mTransY = 0;
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{
		_thread = null;

		mGameMgr = null;
		_nowXY = null;
		_oldXY = null;

		Log.d("GameSurfaceView", "GameSurfaceDestroyed");
	}

	@Override
	public void run()
	{
		while (_thread != null)
		{ 	//メインループ
			if (_active == false)
			{
				_active = true;
				mTransX = 0;
				mTransY = 0;
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
		float y = 0;
		Canvas c = holder.lockCanvas();

		//ここにゲームの描画処理を書く
		if (c == null)
		{
			return;
		}
		if (mGameMgr != null)
		{
			y = mGameMgr.getPlayer().getPt()._y;			
		}
		c.save(Canvas.CLIP_SAVE_FLAG);
		c.scale(_scale, _scale);

		final int zone_top    = 300;
		final int zone_bottom = mViewHeight - 300;
		
		// 自機の位置にあわせてビューポートを移動する
		if (mTransY < -(y - zone_top))
		{
			mTransY = -(int)(y - zone_top);
			// 画面の端は越えない
			if (mTransY > 0)
			{
				mTransY = 0;
			}
		}
		if (mTransY > -(y - zone_bottom))
		{
			mTransY = -(int)(y - zone_bottom);
			// 画面の端は越えない
			if (mTransY < -(BG_HEIGHT - mViewHeight))
			{
				mTransY = -(int)(BG_HEIGHT - mViewHeight);
			}
		}
		c.translate(0, mTransY);			
		
		mGameMgr.onDraw(c);
		c.restore();

		holder.unlockCanvasAndPost(c);
	}

	@Override
    public boolean onTouchEvent(MotionEvent e)
	{
        switch (e.getAction())
		{
			case MotionEvent.ACTION_DOWN:
				if (mGameMgr != null && mGameMgr.isFinished())
				{
					Log.d("GameSurfaceView", "Restart!");
					_active = false;
				}
				break;
			case MotionEvent.ACTION_UP:
				break;
			case MotionEvent.ACTION_MOVE:
				if (mGameMgr != null && !mGameMgr.isFinished())
				{
					_nowXY.set(e.getX(), e.getY());
					mGameMgr.movePlayer(_oldXY, _nowXY);
				}
				break;
        }		
		_oldXY.set(e.getX(), e.getY());

        return true;
    }
}
