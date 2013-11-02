package org.fujix.irairan;
import android.graphics.*;

public class Viewport
{
	private float STG_WIDTH;
	private float STG_HEIGHT;
	
	public int x, y;	// ビューポートの左上座標
	public int W, H;	// ビューポートの幅と高さ
	
	Viewport(float stage_w, float stage_h)
	{
		STG_WIDTH  = stage_w;
		STG_HEIGHT = stage_h;
		
		x = y = 0;
		W = H = 0;
	}
	
	public void update(float player_x, float player_y)
	{	// ビューポートの更新
		final int ZONE_TOP    = 0 + H / 3;
		final int ZONE_BOTTOM = H - H / 3;
		final int ZONE_LEFT   = 0 + W / 3;
		final int ZONE_RIGHT  = W - W / 3;
		
		x = zoning(x, player_x, ZONE_LEFT, ZONE_RIGHT, STG_WIDTH,  W);
		y = zoning(y, player_y, ZONE_TOP, ZONE_BOTTOM, STG_HEIGHT, H);		
	}
	
	private int zoning(int pos, float player_pos, int min, int max, float stage_size, float viewport_size)
	{	// 自機の位置にあわせてビューポートを移動する
		if (pos > (player_pos - min))
		{
			pos = (int)(player_pos - min);
			// 画面の最少端は越えない
			if (pos < 0)
			{
				pos = 0;
			}
		}
		else
		if (pos < (player_pos - max))
		{
			pos = (int)(player_pos - max);
			// 画面の最大端は越えない
			if (pos > (stage_size - viewport_size))
			{
				pos = (int)(stage_size - viewport_size);
			}
		}		
		return pos;
	}
} 
