package org.fujix.irairan;

public class BarricadeSquare extends Barricade
{

	public BarricadeSquare(float x, float y, float w, float h, BConf conf)
	{
		super(4, conf);
		mPt[0].x  = x;          mPt[0].y = y;
		mPt[1].x  = x + w;      mPt[1].y = y;
		mPt[2].x  = x + w;      mPt[2].y = y + h;
		mPt[3].x  = x;          mPt[3].y = y + h;
		mCenter.x = x + w / 2;
		mCenter.y = y + h / 2;
	}

}

