package org.fujix.irairan;

import android.graphics.*;
import android.util.*;
import java.util.*;
import android.content.res.*;
import android.content.*;

public class GameDirector
{
	private enum eStatus	//状態
	{
		NORMAL,		//普通
		GAMEOVER,	//ゲームオーバー
		GAMECLEAR,	//ゲームクリア
	};

	private static final float PI = (float) Math.PI;
	private ArrayList<Barricade> mBarrList = new ArrayList<Barricade>();//障害物リスト

	private LinkedList<Task> mTaskList = new LinkedList<Task>(); 		//タスクリスト
	private eStatus          mStatus = eStatus.NORMAL;					//状態
	private Player           mPlayer;
	private FpsController 	 mFps;
	private	Vec              mVec = new Vec();		
	private PointF 			 mPtNow, mPtOld;

	private Context 		 mContext;
	
	GameDirector(Context c, int stage)
	{
		mContext = c;
		
		// リソースからステージデータを読込む
		Resources res      = c.getResources();
		String packageName = c.getPackageName();
		
		int bar_count = res.getInteger(res.getIdentifier("st" + stage + "_bar_count","integer",packageName));
		
		for (int bar_id = 0; bar_id < bar_count; bar_id++)
		{
			setBarricade(stage, bar_id+1);			
		}
		
		// タスクリストに登録する
		for (Barricade bar : mBarrList)
		{
			mTaskList.add(bar);     //タスクリストに障害物を追加
		}

		// プレイヤーオブジェクトの登録
		mPlayer = new Player(c);
		mTaskList.add(mPlayer);
		
		// ＦＰＳ表示オブジェクトの登録
		mFps = new FpsController();
//		mTaskList.add(mFps);
		
		// タッチ座標を初期化
		mPtNow = new PointF(0, 0);
		mPtOld = new PointF(0, 0);
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
			mPtNow    = null;
			mPtOld    = null;
			mTaskList = null;
//			for (Barricade bar : mBarrList)
//			{
//				bar = null;
//			}
			mVec      = null;
			mPlayer   = null;
			mBarrList = null;

			Log.d("GameMgr", "GameMgrDestruct");
		}
	}

	/**
	 * 衝突判定
	 */
	private boolean Collision()
	{
		//	Vec vec = new Vec();		
		final Circle cir = mPlayer.getPt();	//プレイヤーの中心円を取得

		for (Barricade barr : mBarrList)		//障害物の数だけループ
		{
			Def.eHitCode code = barr.isHit(cir, mVec);//接触判定

			switch (code)
			{
				case OUT://接触したものが「アウト」なら
					if (0 >= mPlayer.damage())
					{
						mStatus = eStatus.GAMEOVER;//アウト状態に					
						return true;
					}
					break;
				case GOAL:
					mStatus = eStatus.GAMECLEAR;
					return true;
				default:
					break;
			}
		}
		return false;
	}

	/**
	 * 状態の更新
	 */
	public boolean onUpdate()
	{
		if (mStatus != eStatus.NORMAL)
		{	//ゲームの状態が通常でないなら計算しない
			return true;
		}
		// 自機の移動
		movePlayer(mPtOld, mPtNow);
		mPtOld.set(mPtNow);

		if (Collision())
		{	//衝突判定　衝突したならメソッドを抜ける
			return true;
		}
		for (int i=0; i < mTaskList.size(); i++)
		{
			if (mTaskList.get(i).onUpdate() == false)
			{	//更新失敗なら
				mTaskList.remove(i);              //そのタスクを消す
				i--;
			}
		}
		mFps.onUpdate();
		
		return true;
	}

	/**
	 * 状態を表示する
	 */
	private void drawStatus(Canvas c)
	{
		mFps.onDraw(c);
		
		switch (mStatus)
		{
			case GAMEOVER:
				{
					final String msg = "GAME OVER";
					Paint paint = new Paint();
					paint.setTextSize(80);
					paint.setColor(Color.DKGRAY);
					c.drawText(msg, 22, 402, paint);
					paint.setColor(Color.WHITE);
					c.drawText(msg, 20, 400, paint);
				}
				break;
			case GAMECLEAR:
				{
					final String msg = "GOAL!";
					Paint paint = new Paint();
					paint.setTextSize(80);
					paint.setColor(Color.DKGRAY);
					c.drawText(msg, 122, 402, paint);
					paint.setColor(Color.WHITE);
					c.drawText(msg, 120, 400, paint);
				}
				break;
			default:
				break;
		}
	}

	/**
	 * 描画時
	 */
	public void onDraw(Canvas c)
	{
		c.drawColor(Color.BLACK);       //塗りつぶす
		for (Task task : mTaskList)
		{
			task.onDraw(c);// 描画
		}
	}
	
	/**
	 * 状態の描画時
	 */
	public void onDrawStatus(Canvas c)
	{
		drawStatus(c);//状態を表示する		
	}

	/**
	 * ゲームが終了しているかを返却
	 */
	public boolean isFinished()
	{
		if (mStatus != eStatus.NORMAL)
		{
			return true;
		}
		return false;
	}

	/**
	 * 自機の移動
	 */
	public void movePlayer(PointF old, PointF now)
	{
		mPlayer.setMove(old, now);
	}	

	/**
	 * 自機の停止要求
	 */
	public void stopPlayer()
	{
		mPlayer.resetSensorVec();
	}

	/**
	 * 自機オブジェクトの取得
	 */
	public Player getPlayer()
	{
		return mPlayer;
	}

	/**
	 * タッチ座標の初期化
	 */
	public void initTouchPoint(float x, float y)
	{
		setTouchPoint(x, y);
		mPtOld.set(mPtNow);
	}

	/**
	 * タッチした座標を設定する
	 */
	public void setTouchPoint(float x, float y)
	{
		mPtNow.x = x;
		mPtNow.y = y;
	}
	
	/**
	 * リソースから障害物をタイプごとに読分ける
	 */
	private void setBarricade(int stage, int index)
	{
		Resources res      = mContext.getResources();
		String packageName = mContext.getPackageName();
		String shape       = res.getString(res.getIdentifier("st" + stage + "_bar" + index + "_shape", "string", packageName));
		
		if (shape.equals("Square"))
		{
			setBarricadeSquare(stage, index);
		}
		else
		if (shape.equals("Triangle"))
		{
			setBarricadeTriangle(stage, index);
		}
		else
		if (shape.equals("Star"))
		{
			setBarricadeStar(stage, index);
		}
	}
	
	/**
	 * 矩形タイプの障害物の設定
	 */
	private void setBarricadeSquare(int stage, int index)
	{
		Resources res      = mContext.getResources();
		String packageName = mContext.getPackageName();
		
		int x = res.getInteger(res.getIdentifier("st" + stage + "_bar" + index + "_x","integer", packageName));
		int y = res.getInteger(res.getIdentifier("st" + stage + "_bar" + index + "_y","integer", packageName));
		int w = res.getInteger(res.getIdentifier("st" + stage + "_bar" + index + "_w","integer", packageName));
		int h = res.getInteger(res.getIdentifier("st" + stage + "_bar" + index + "_h","integer", packageName));

		String type = res.getString(res.getIdentifier("st" + stage + "_bar" + index + "_type", "string", packageName));

		if (type.equals("OUT"))
		{
			mBarrList.add(new BarricadeSquare(x, y, w, h, new BConf(Barricade.eType.OUT)));
		}
		else
		if (type.equals("GOAL"))
		{
			mBarrList.add(new BarricadeSquare(x, y, w, h, new BConf(Barricade.eType.GOAL)));				
		}		
		else
		if (type.equals("CW"))
		{
			int ratio = res.getInteger(res.getIdentifier("st" + stage + "_bar" + index + "_ratio", "integer", packageName));
			mBarrList.add(new BarricadeSquare(x, y, w, h, new BConf(+PI / ratio)));				
		}		
		else
		if (type.equals("CCW"))
		{
			int ratio = res.getInteger(res.getIdentifier("st" + stage + "_bar" + index + "_ratio", "integer", packageName));
			mBarrList.add(new BarricadeSquare(x, y, w, h, new BConf(-PI / ratio)));				
		}		
	}
	
	/**
	 * 三角形タイプの障害物の設定
	 */
	private void setBarricadeTriangle(int stage, int index)
	{
		Resources res      = mContext.getResources();
		String packageName = mContext.getPackageName();
		
		int x = res.getInteger(res.getIdentifier("st" + stage + "_bar" + index + "_x", "integer", packageName));
		int y = res.getInteger(res.getIdentifier("st" + stage + "_bar" + index + "_y", "integer", packageName));
		int r = res.getInteger(res.getIdentifier("st" + stage + "_bar" + index + "_r", "integer", packageName));

		String type = res.getString(res.getIdentifier("st" + stage + "_bar" + index + "_type", "string", packageName));

		if (type.equals("CW"))
		{
			int ratio = res.getInteger(res.getIdentifier("st" + stage + "_bar" + index + "_ratio", "integer", packageName));
			mBarrList.add(new BarricadeTriangle(x, y, r, new BConf(+PI / ratio)));
		}		
		else
		if (type.equals("CCW"))
		{
			int ratio = res.getInteger(res.getIdentifier("st" + stage + "_bar" + index + "_ratio", "integer", packageName));
			mBarrList.add(new BarricadeTriangle(x, y, r, new BConf(-PI / ratio)));
		}		
	}
	
	/**
	 * 星形の障害物の設定
	 */
	private void setBarricadeStar(int stage, int index)
	{
		Resources res      = mContext.getResources();
		String packageName = mContext.getPackageName();
		
		int x = res.getInteger(res.getIdentifier("st" + stage + "_bar" + index + "_x", "integer", packageName));
		int y = res.getInteger(res.getIdentifier("st" + stage + "_bar" + index + "_y", "integer", packageName));
		int ir = res.getInteger(res.getIdentifier("st" + stage + "_bar" + index + "_ir", "integer", packageName));
		int or = res.getInteger(res.getIdentifier("st" + stage + "_bar" + index + "_or", "integer", packageName));
		
		String type = res.getString(res.getIdentifier("st" + stage + "_bar" + index + "_type", "string", packageName));

		if (type.equals("CW"))
		{
			int ratio = res.getInteger(res.getIdentifier("st" + stage + "_bar" + index + "_ratio", "integer", packageName));
			mBarrList.add(new BarricadeStar(x, y, ir, or, new BConf(+PI / ratio)));
		}		
		else
		if (type.equals("CCW"))
		{
			int ratio = res.getInteger(res.getIdentifier("st" + stage + "_bar" + index + "_ratio", "integer", packageName));
			mBarrList.add(new BarricadeStar(x, y, ir, or, new BConf(-PI / ratio)));
		}		
	}
}
