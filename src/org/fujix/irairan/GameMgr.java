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
	private ArrayList<Barricade> _barrList = new ArrayList<Barricade>();//障害物リスト

	private LinkedList<Task> _taskList = new LinkedList<Task>(); //タスクリスト
	private eStatus _status = eStatus.NORMAL;//状態
	private Player _player;
	private	Vec _vec = new Vec();		

	GameMgr()
	{
		_barrList.add(new BarricadeSquare(0,  0, 480, 20, new BConf(Barricade.eType.OUT)));// 画面4隅に四角形を配置
		_barrList.add(new BarricadeSquare(0,  0, 20, 800, new BConf(Barricade.eType.OUT)));// コンフィグを特に設定しない時はnullを渡すとデフォルト設定になる
		_barrList.add(new BarricadeSquare(460,  0, 20, 800, new BConf(Barricade.eType.OUT)));
		_barrList.add(new BarricadeSquare(0, 780, 480, 20, new BConf(Barricade.eType.OUT)));

		_barrList.add(new BarricadeTriangle(0, 0, 200, new BConf(+PI / 150)));// 左上回転する三角形
		_barrList.add(new BarricadeTriangle(480, 0, 180, new BConf(+PI / 150)));// 右上回転する三角形

		_barrList.add(new BarricadeStar(240, 240, 50, 200, new BConf(-PI / 360)));// 中央に回転する星
		_barrList.add(new BarricadeStar(240, 240, 20,  80, new BConf(+PI / 360)));// 中央に回転する星

		_barrList.add(new BarricadeSquare(300, 440, 200, 20, new BConf(Barricade.eType.OUT)));//右下の固定通路
		_barrList.add(new BarricadeSquare(250, 520, 130, 20, new BConf(Barricade.eType.OUT)));//
		_barrList.add(new BarricadeSquare(330, 620, 130, 20, new BConf(Barricade.eType.OUT)));//

		_barrList.add(new BarricadeSquare(230, 390, 20, 350, new BConf(Barricade.eType.OUT)));//中央区切り線

		_barrList.add(new BarricadeSquare(0, 480, 240, 20, new BConf(+PI / 360)));// 左下回転するバー

		_barrList.add(new BarricadeSquare(20, 600, 110, 20, new BConf(+PI / 360)));// 左下回転するバー
		_barrList.add(new BarricadeSquare(130, 600, 110, 20, new BConf(+PI / 360)));// 左下回転するバー
		_barrList.add(new BarricadeSquare(185, 600,  55, 20, new BConf(+PI / 360)));// 左下回転するバー

		_barrList.add(new BarricadeSquare(20, 680,  80, 20, new BConf(Barricade.eType.OUT)));// ゴールに接触したバー

		_barrList.add(new BarricadeSquare(20, 700,  80, 80, new BConf(Barricade.eType.GOAL)));// ゴール		

		for (Barricade bar : _barrList)
		{
			_taskList.add(bar);     //タスクリストに障害物を追加
		}

		_player = new Player();
		_taskList.add(_player);
		_taskList.add(new FpsController());
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
			_taskList = null;
//			for (Barricade bar : _barrList)
//			{
//				bar = null;
//			}
			_vec = null;
			_player = null;
			_barrList = null;

			Log.d("GameMgr", "GameMgrDestruct");
		}
	}

	private boolean Collision()		//衝突判定
	{
		//	Vec vec = new Vec();		
		final Circle cir = _player.getPt();	//プレイヤーの中心円を取得

		for (Barricade barr : _barrList)		//障害物の数だけループ
		{
			Def.eHitCode code = barr.isHit(cir, _vec);//接触判定

			switch (code)
			{
				case OUT://接触したものが「アウト」なら
					_status = eStatus.GAMEOVER;//アウト状態に
					return true;
				case GOAL:
					_status = eStatus.GAMECLEAR;
					return true;
				default:
					break;
			}
		}
		return false;
	}

	public boolean onUpdate()
	{
		if (_status != eStatus.NORMAL)
		{//ゲームの状態が通常でないなら計算しない
			return true;
		}
		if (Collision())
		{//衝突判定　衝突したならメソッドを抜ける
			return true;
		}
		for (int i=0; i < _taskList.size(); i++)
		{
			if (_taskList.get(i).onUpdate() == false)
			{ //更新失敗なら
				_taskList.remove(i);              //そのタスクを消す
				i--;
			}
		}
		return true;
	}

	private void drawStatus(Canvas c)
	{//状態を表示する
		switch (_status)
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
		for (Task task : _taskList)
		{
			task.onDraw(c);// 描画
		}
		drawStatus(c);//状態を表示する
	}

	public boolean isFinished()
	{	// ゲーム終了？
		if (_status != eStatus.NORMAL)
		{
			return true;
		}
		return false;
	}

	public void movePlayer(PointF old, PointF now)
	{	// 自機の移動
		_player.setMove(old, now);
	}	
	
	public Player getPlayer()
	{	// 自機の取得
		return _player;
	}
}
