package org.fujix.irairan;
import android.util.*;

public class Line			// 線分定義構造体
{
	public float _x,_y;		// x,y:始点座標
	public float _sx,_sy;	// sx,sy:サイズ
	
	Line(float x1, float y1, float x2, float y2)
	{
		Set(x1, y1, x2, y2);
	}
	
	@Override
	protected void finalize() throws Throwable {
		try {
			super.finalize();
		} finally {
			Log.d("Line", "LineDestruct");
		}
	}

	public Line()
	{
		_x = _y = _sx = _sy = 0;
	}
	
	public void Set(float x1, float y1, float x2, float y2)
	{
		_x  = x1;
		_y  = y1;
		_sx = x2 - x1;
		_sy = y2 - y1;		
	}
}