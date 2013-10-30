package org.fujix.irairan;

public class BarricadeStar extends Barricade
{

	private static final float PI2 = (float) (Math.PI * 2);

	public BarricadeStar(float x, float y, float inR, float outR, BConf conf)
	{
		super(10, conf);
		for (int i=0; i < 5; i++)
		{
			mPt[i * 2 + 0].x = (float) (x + Math.cos(PI2 / 5 * i) * inR);//内側
			mPt[i * 2 + 0].y = (float) (y + Math.sin(PI2 / 5 * i) * inR);
			mPt[i * 2 + 1].x = (float) (x + Math.cos(PI2 / 5 * i + PI2 / 10) * outR);//外側
			mPt[i * 2 + 1].y = (float) (y + Math.sin(PI2 / 5 * i + PI2 / 10) * outR);
		}
		mCenter.x = x;
		mCenter.y = y;
	}

}
