package org.fujix.irairan;

import android.graphics.*;
import android.util.*;

public class Barricade extends Task
{
	Path mPath = new Path();

	public enum eType
	{ // 障害物のタイプ
		OUT, // あたるとアウトなタイプ
		GOAL // あたるとゴールなタイプ
		}

	protected PointF mCenter = new PointF(0, 0); 	// 図形の中心点
	protected PointF mPt[]; 						// 図形の頂点
	protected Paint  mPaint = new Paint(); 			// ペイント
	protected eType  mType; 						// タイプ(当たるとアウトな壁、ゴールの壁、等)
	protected float  mRotSpeed = 0; 				// 回転スピード

	// コンストラクタ。 type=タイプ、 n=頂点の数、 conf=設定情報
	public Barricade(int n, BConf conf)
	{
		if (conf != null)
		{
			mRotSpeed = conf.speed; 	// 回転スピード
			mType = conf.type; 			// 物体のタイプ
		}
//		mPaint.setStyle(Paint.Style.STROKE);
		
		switch (mType)
		{
			case OUT: // 接触してアウトな物
				mPaint.setColor(Color.RED); 	// 赤に
				break;
			case GOAL: // 接触してゴールな物
				mPaint.setColor(Color.GREEN); 	// 緑に
				break;
		}
		mPt = new PointF[n]; // 頂点配列を作る
		for (int i = 0; i < n; i++)
		{
			mPt[i] = new PointF(); 				// 頂点を作る
		}
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
//			for (int i = 0; i < _pt.length; i++)
//			{
//				_pt[i] = null;
//			}
			mCenter = null;
			mPaint = null;
			mPath = null;
			
			Log.d("Barricade", "BarricadeDestruct");
		}
	}

	// 更新する
	public boolean onUpdate()
	{
		if (mRotSpeed != 0)
		{  //回転するなら
			DiagramCalcr.RotateDiagram(mPt, mCenter, mRotSpeed); //頂点リスト(_pt)を_centerを中心に回転する
		}
		return true;
	}

	// 接触しているかを問う。円cirが接触していれば接触した線分をvecに格納し、物体のタイプを返す。接触していなければNOを返す
	public Def.eHitCode isHit(final Circle cir, Vec vec)
	{
		if (DiagramCalcr.Collision(mPt, cir, vec) == true)
		{	//頂点群_ptが示す各辺と円cirが接触していればベクトルをvecに入れてtrueを返す
			switch (mType)
			{
				case OUT://アウトな線なら
					return Def.eHitCode.OUT;
				case GOAL://ゴールな線なら
					return Def.eHitCode.GOAL;
			}
		}
		return Def.eHitCode.NO; //何も接触していない
	}

	// 描画する
	public void onDraw(Canvas c)
	{
		if (mPt.length < 1)
		{ // 頂点が1未満の図形はありえない
			return;
		}
		// パスを設定
		mPath.reset();
		mPath.moveTo(mPt[0].x, mPt[0].y); // パスの初期位置をセット
		for (int i = 0; i < mPt.length; i++)
		{
			mPath.lineTo(mPt[i].x, mPt[i].y); // 頂点の位置へラインを引いていく
		}
		c.drawPath(mPath, mPaint); // 引いたラインを描画する
	}
}
