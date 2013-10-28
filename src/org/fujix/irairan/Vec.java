package org.fujix.irairan;

public class Vec
{

	public float _x, _y;

	Vec()
	{
		_x = _y = 0;
	}

	Vec(float x, float y)
	{
		_x = x;
		_y = y;
	}

	//角度を取得する
	float getAngle()
	{
		return (float)Math.atan2(_y, _x);
	}

	//大きさを取得する
	float getLength()
	{
		return (float)Math.sqrt(_x * _x + _y * _y);
	}

	//引数の値より大きさが大きければ引数の値にする
	void setLengthCap(float maxLength)
	{
		float len = getLength();
		if (maxLength == 0)
		{
			return; //0割防止
		}
		if (len > maxLength)
		{//maxLengthより大きければ大きさをmaxLengthにする
			float rate =len / maxLength;
			_x /= rate;
			_y /= rate;
		}
	}

	// vec方向の向きへrate率ほどブレンドする
	void blend(Vec vec, float rate)
	{
		float w = vec._x - _x;
		float h = vec._y - _y;
		_x += w * rate;
		_y += h * rate;
	}
}
