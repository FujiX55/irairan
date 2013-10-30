package org.fujix.irairan;

public class BarricadeTriangle extends Barricade
{
	public BarricadeTriangle(float x, float y, float r, BConf conf)
	{
		super(3, conf);
		for (int i=0; i < 3; i++)
		{
			mPt[i].x = x + (float) (Math.cos(Math.PI * 2 / 3 * i) * r);
			mPt[i].y = y + (float) (Math.sin(Math.PI * 2 / 3 * i) * r);
		}
		mCenter.x = x;
		mCenter.y = y;
	}
}
