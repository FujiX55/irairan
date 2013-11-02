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
		
		this.x = 0;
		this.y = 0;
		this.W = 0;
		this.H = 0;
	}
	
	public void move(float player_x, float player_y)
	{	// ビューポートの移動
		final int ZONE_TOP    = 300;
		final int ZONE_BOTTOM = H - 300;

		// y方向への移動
		if (y > (player_y - ZONE_TOP))
		{
			y = (int)(player_y - ZONE_TOP);
			// 画面の上端は越えない
			if (y < 0)
			{
				y = 0;
			}
		}
		else
		if (y < (player_y - ZONE_BOTTOM))
		{
			y = (int)(player_y - ZONE_BOTTOM);
			// 画面の下端は越えない
			if (y > (STG_HEIGHT - H))
			{
				y = (int)(STG_HEIGHT - H);
			}
		}
	}
} 
