package org.fujix.geminin;

import android.content.*;
import android.graphics.*;
import android.view.*;

public class GameSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable
{
	private GameMgr	_gameMgr = new GameMgr();
	private Thread	_thread;

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
		_thread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{
		_thread = null;
	}

	@Override
	public void run()
	{
		while (_thread != null)
		{ //メインループ
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
				break;
			case MotionEvent.ACTION_UP:
				if (_gameMgr.isFinished())
				{
					_gameMgr = new GameMgr();
				}
				break;
			case MotionEvent.ACTION_MOVE:
				break;
        }
        return true;
    }
}
