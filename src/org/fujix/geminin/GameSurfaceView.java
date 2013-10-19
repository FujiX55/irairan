package org.fujix.geminin;

import android.content.*;
import android.graphics.*;
import android.util.*;
import android.view.*;

public class GameSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable
{
	private GameMgr	_gameMgr;// = new GameMgr();
	private Thread	_thread;

	private PointF _nowXY;// = new PointF();
	private PointF _oldXY;// = new PointF();

	private boolean _active = true;

	public GameSurfaceView(Context context)
	{
		super(context);
		getHolder().addCallback(this);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
	{
		//解像度情報変更通知 
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{
		_thread = new Thread(this);             //別スレッドでメインループを作る
		_gameMgr = new GameMgr();
		_nowXY = new PointF();
		_oldXY = new PointF();

		_thread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{
		_thread = null;

		_gameMgr = null;
		_nowXY = null;
		_oldXY = null;

		Log.d("GameSurfaceView", "GameSurfaceDestroyed");
	}

	@Override
	public void run()
	{
		while (_thread != null)
		{ //メインループ
			if (_active == false)
			{
				_active = true;
				// 再始動
				_gameMgr = null;
				System.gc();
				_gameMgr = new GameMgr();
			}
			_gameMgr.onUpdate();
			onDraw(getHolder());			
		}
	}

	private void onDraw(SurfaceHolder holder)
	{
		Canvas c = holder.lockCanvas();
		if (c == null)
		{
			return;
		}
		//ここにゲームの描画処理を書く
		_gameMgr.onDraw(c);
		holder.unlockCanvasAndPost(c);
	}

	@Override
    public boolean onTouchEvent(MotionEvent e)
	{
        switch (e.getAction())
		{
			case MotionEvent.ACTION_DOWN:
				if (_gameMgr.isFinished())
				{
					Log.d("GameSurfaceView", "Restart!");
					_active = false;
				}
				break;
			case MotionEvent.ACTION_UP:
				break;
			case MotionEvent.ACTION_MOVE:
				if (!_gameMgr.isFinished())
				{
					_nowXY.set(e.getX(), e.getY());
					_gameMgr.movePlayer(_oldXY, _nowXY);
				}
				break;
        }		
		_oldXY.set(e.getX(), e.getY());

        return true;
    }
}
