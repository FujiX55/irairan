package org.fujix.irairan;

import android.graphics.*;
import android.util.*;
import java.util.*;
import android.content.res.*;
import android.content.*;

public class GameMgr
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
	private	Vec              mVec = new Vec();		
	private PointF 			 mPtNow, mPtOld;

	private Context 		 mContext;
	
	GameMgr(Context c)
	{
		mContext = c;
		
		// 画面4隅に四角形を配置
		int stage  = 1;

		Resources res = c.getResources();

		int bar_count = res.getInteger(res.getIdentifier("st" + stage + "_bar_count","integer",c.getPackageName()));
		
		for (int bar_id = 0; bar_id < bar_count; bar_id++)
		{
			setBarricade(stage, bar_id+1);			
		}
//		setBarricade(stage, bar_id++);
//		setBarricade(stage, bar_id++);
//		setBarricade(stage, bar_id++);
//		setBarricade(stage, bar_id++);
//		
//		setBarricade(stage, bar_id++);
//		setBarricade(stage, bar_id++);
////		mBarrList.add(new BarricadeTriangle(0, 0, 200, new BConf(+PI / 150)));// 左上回転する三角形
////		mBarrList.add(new BarricadeTriangle(480, 0, 180, new BConf(+PI / 150)));// 右上回転する三角形
//
//		setBarricade(stage, bar_id++);
//		setBarricade(stage, bar_id++);
////		mBarrList.add(new BarricadeStar(240, 240, 50, 200, new BConf(-PI / 360)));// 中央に回転する星
////		mBarrList.add(new BarricadeStar(240, 240, 20,  80, new BConf(+PI / 360)));// 中央に回転する星
//
//		setBarricade(stage, bar_id++);
//		setBarricade(stage, bar_id++);
//		setBarricade(stage, bar_id++);
////		mBarrList.add(new BarricadeSquare(300, 440, 200, 20, new BConf(Barricade.eType.OUT)));//右下の固定通路
////		mBarrList.add(new BarricadeSquare(250, 520, 130, 20, new BConf(Barricade.eType.OUT)));//
////		mBarrList.add(new BarricadeSquare(330, 620, 130, 20, new BConf(Barricade.eType.OUT)));//
//
//		setBarricade(stage, bar_id++);
////		mBarrList.add(new BarricadeSquare(230, 390, 20, 350, new BConf(Barricade.eType.OUT)));//中央区切り線
//
//		setBarricade(stage, bar_id++);
////		mBarrList.add(new BarricadeSquare(0, 480, 240, 20, new BConf(+PI / 360)));// 左下回転するバー
//
//		setBarricade(stage, bar_id++);
//		setBarricade(stage, bar_id++);
//		setBarricade(stage, bar_id++);
////		mBarrList.add(new BarricadeSquare(20, 600, 110, 20, new BConf(+PI / 360)));// 左下回転するバー
////		mBarrList.add(new BarricadeSquare(130, 600, 110, 20, new BConf(+PI / 360)));// 左下回転するバー
////		mBarrList.add(new BarricadeSquare(185, 600,  55, 20, new BConf(+PI / 360)));// 左下回転するバー
//
//		setBarricade(stage, bar_id++);
////		mBarrList.add(new BarricadeSquare(350, 350, 110, 20, new BConf(+PI / 360)));// 右回転するバー
//		
//		setBarricade(stage, bar_id++);
////		mBarrList.add(new BarricadeSquare(20, 680,  80, 20, new BConf(Barricade.eType.OUT)));// ゴールに接触したバー
//
//		setBarricade(stage, bar_id++);
////		mBarrList.add(new BarricadeSquare(20, 700,  80, 80, new BConf(Barricade.eType.GOAL)));// ゴール		
//
		for (Barricade bar : mBarrList)
		{
			mTaskList.add(bar);     //タスクリストに障害物を追加
		}

		mPlayer = new Player();
		mTaskList.add(mPlayer);
		mTaskList.add(new FpsController());
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

	private boolean Collision()		//衝突判定
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
		return true;
	}

	private void drawStatus(Canvas c)
	{//状態を表示する
		switch (mStatus)
		{
			case GAMEOVER:
				{
					Paint paint = new Paint();
					paint.setTextSize(80);
					paint.setColor(Color.WHITE);
					c.drawText("GameOver", 42, 102, paint);
					paint.setTextSize(80);
					paint.setColor(Color.BLACK);
					c.drawText("GameOver", 40, 100, paint);
				}
				break;
			case GAMECLEAR:
				{
					Paint paint = new Paint();
					paint.setTextSize(80);
					paint.setColor(Color.BLACK);
					c.drawText("GameClear", 40, 100, paint);
				}
				break;
			default:
				break;
		}
	}

	public void onDraw(Canvas c)
	{
		c.drawColor(Color.BLACK);       //塗りつぶす
		for (Task task : mTaskList)
		{
			task.onDraw(c);// 描画
		}
	}
	
	public void onDrawStatus(Canvas c)
	{
		drawStatus(c);//状態を表示する		
	}

	public boolean isFinished()
	{	// ゲーム終了？
		if (mStatus != eStatus.NORMAL)
		{
			return true;
		}
		return false;
	}

	public void movePlayer(PointF old, PointF now)
	{	// 自機の移動
		mPlayer.setMove(old, now);
	}	

	public void stopPlayer()
	{	// 自機の停止要求
		mPlayer.resetSensorVec();
	}

	public Player getPlayer()
	{	// 自機の取得
		return mPlayer;
	}

	public void initTouchPoint(float x, float y)
	{	// タッチした座標の初期化
		setTouchPoint(x, y);
		mPtOld.set(mPtNow);
	}

	public void setTouchPoint(float x, float y)
	{	// タッチした座標を設定する
		mPtNow.x = x;
		mPtNow.y = y;
	}
	
	private void setBarricade(int stage, int index)
	{
		Context c = mContext;
		Resources res = c.getResources();

		String shape = res.getString(res.getIdentifier("st" + stage + "_bar" + index + "_shape","string",c.getPackageName()));
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
	
	private void setBarricadeSquare(int stage, int index)
	{
		Context c = mContext;
		Resources res = c.getResources();
		
		int p1 = res.getInteger(res.getIdentifier("st" + stage + "_bar" + index + "_p1","integer",c.getPackageName()));
		int p2 = res.getInteger(res.getIdentifier("st" + stage + "_bar" + index + "_p2","integer",c.getPackageName()));
		int p3 = res.getInteger(res.getIdentifier("st" + stage + "_bar" + index + "_p3","integer",c.getPackageName()));
		int p4 = res.getInteger(res.getIdentifier("st" + stage + "_bar" + index + "_p4","integer",c.getPackageName()));

		String type = res.getString(res.getIdentifier("st" + stage + "_bar" + index + "_type","string",c.getPackageName()));

		if (type.equals("OUT"))
		{
			mBarrList.add(new BarricadeSquare(p1, p2, p3, p4, new BConf(Barricade.eType.OUT)));
		}
		else
		if (type.equals("GOAL"))
		{
			mBarrList.add(new BarricadeSquare(p1, p2, p3, p4, new BConf(Barricade.eType.GOAL)));				
		}		
		else
		if (type.equals("CW"))
		{
			int ratio = res.getInteger(res.getIdentifier("st" + stage + "_bar" + index + "_ratio","integer",c.getPackageName()));
			mBarrList.add(new BarricadeSquare(p1, p2, p3, p4, new BConf(+PI / ratio)));				
		}		
		else
		if (type.equals("CCW"))
		{
			int ratio = res.getInteger(res.getIdentifier("st" + stage + "_bar" + index + "_ratio","integer",c.getPackageName()));
			mBarrList.add(new BarricadeSquare(p1, p2, p3, p4, new BConf(-PI / ratio)));				
		}		
	}
	
	private void setBarricadeTriangle(int stage, int index)
	{
		Context c = mContext;
		Resources res = c.getResources();

		int x = res.getInteger(res.getIdentifier("st" + stage + "_bar" + index + "_x","integer",c.getPackageName()));
		int y = res.getInteger(res.getIdentifier("st" + stage + "_bar" + index + "_y","integer",c.getPackageName()));
		int r = res.getInteger(res.getIdentifier("st" + stage + "_bar" + index + "_r","integer",c.getPackageName()));

		String type = res.getString(res.getIdentifier("st" + stage + "_bar" + index + "_type","string",c.getPackageName()));

		if (type.equals("CW"))
		{
			int ratio = res.getInteger(res.getIdentifier("st" + stage + "_bar" + index + "_ratio","integer",c.getPackageName()));
			mBarrList.add(new BarricadeTriangle(x, y, r, new BConf(+PI / ratio)));
		}		
		else
		if (type.equals("CCW"))
		{
			int ratio = res.getInteger(res.getIdentifier("st" + stage + "_bar" + index + "_ratio","integer",c.getPackageName()));
			mBarrList.add(new BarricadeTriangle(x, y, r, new BConf(-PI / ratio)));
		}		
	}
	
	private void setBarricadeStar(int stage, int index)
	{
		Context c = mContext;
		Resources res = c.getResources();

		int x = res.getInteger(res.getIdentifier("st" + stage + "_bar" + index + "_x","integer",c.getPackageName()));
		int y = res.getInteger(res.getIdentifier("st" + stage + "_bar" + index + "_y","integer",c.getPackageName()));
		int ir = res.getInteger(res.getIdentifier("st" + stage + "_bar" + index + "_ir","integer",c.getPackageName()));
		int or = res.getInteger(res.getIdentifier("st" + stage + "_bar" + index + "_or","integer",c.getPackageName()));
		
		String type = res.getString(res.getIdentifier("st" + stage + "_bar" + index + "_type","string",c.getPackageName()));

		if (type.equals("CW"))
		{
			int ratio = res.getInteger(res.getIdentifier("st" + stage + "_bar" + index + "_ratio","integer",c.getPackageName()));
			mBarrList.add(new BarricadeStar(x, y, ir, or, new BConf(+PI / ratio)));
		}		
		else
		if (type.equals("CCW"))
		{
			int ratio = res.getInteger(res.getIdentifier("st" + stage + "_bar" + index + "_ratio","integer",c.getPackageName()));
			mBarrList.add(new BarricadeStar(x, y, ir, or, new BConf(-PI / ratio)));
		}		
	}
}
