package org.fujix.irairan;

import android.graphics.*;
import android.util.*;
import java.util.*;

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

	GameMgr()
	{
		mBarrList.add(new BarricadeSquare(0,  0, 480, 20, new BConf(Barricade.eType.OUT)));// 画面4隅に四角形を配置
		mBarrList.add(new BarricadeSquare(0,  0, 20, 800, new BConf(Barricade.eType.OUT)));// コンフィグを特に設定しない時はnullを渡すとデフォルト設定になる
		mBarrList.add(new BarricadeSquare(460,  0, 20, 800, new BConf(Barricade.eType.OUT)));
		mBarrList.add(new BarricadeSquare(0, 780, 480, 20, new BConf(Barricade.eType.OUT)));

		mBarrList.add(new BarricadeTriangle(0, 0, 200, new BConf(+PI / 150)));// 左上回転する三角形
		mBarrList.add(new BarricadeTriangle(480, 0, 180, new BConf(+PI / 150)));// 右上回転する三角形

		mBarrList.add(new BarricadeStar(240, 240, 50, 200, new BConf(-PI / 360)));// 中央に回転する星
		mBarrList.add(new BarricadeStar(240, 240, 20,  80, new BConf(+PI / 360)));// 中央に回転する星

		mBarrList.add(new BarricadeSquare(300, 440, 200, 20, new BConf(Barricade.eType.OUT)));//右下の固定通路
		mBarrList.add(new BarricadeSquare(250, 520, 130, 20, new BConf(Barricade.eType.OUT)));//
		mBarrList.add(new BarricadeSquare(330, 620, 130, 20, new BConf(Barricade.eType.OUT)));//

		mBarrList.add(new BarricadeSquare(230, 390, 20, 350, new BConf(Barricade.eType.OUT)));//中央区切り線

		mBarrList.add(new BarricadeSquare(0, 480, 240, 20, new BConf(+PI / 360)));// 左下回転するバー

		mBarrList.add(new BarricadeSquare(20, 600, 110, 20, new BConf(+PI / 360)));// 左下回転するバー
		mBarrList.add(new BarricadeSquare(130, 600, 110, 20, new BConf(+PI / 360)));// 左下回転するバー
		mBarrList.add(new BarricadeSquare(185, 600,  55, 20, new BConf(+PI / 360)));// 左下回転するバー

		mBarrList.add(new BarricadeSquare(20, 680,  80, 20, new BConf(Barricade.eType.OUT)));// ゴールに接触したバー

		mBarrList.add(new BarricadeSquare(20, 700,  80, 80, new BConf(Barricade.eType.GOAL)));// ゴール		

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
		c.drawColor(Color.WHITE);       //白で塗りつぶす
		for (Task task : mTaskList)
		{
			task.onDraw(c);// 描画
		}
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
}
