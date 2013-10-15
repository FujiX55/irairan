package org.fujix.geminin;

public class BConf
{
//	public float speed = (float)Math.PI/180;
	public float speed = 0.0f;
	public Barricade.eType type = Barricade.eType.OUT;

	public BConf(Barricade.eType atype)
	{
		type = atype;
	}

	public BConf(float aspeed)
	{
		speed = aspeed;
	}
}
