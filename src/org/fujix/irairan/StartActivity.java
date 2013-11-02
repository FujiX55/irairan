package org.fujix.irairan;

import android.app.*;
import android.os.*;
import android.view.View.*;
import android.widget.*;
import android.view.*;
import android.content.*;

public class StartActivity extends Activity implements OnClickListener
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.start);
		
		Button startButton = (Button)findViewById(R.id.startButton);
		
		startButton.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v)
	{
		Intent intent = new Intent();
		
		intent.setClassName("org.fujix.irairan", "org.fujix.irairan.MainActivity");
		
		startActivity(intent);
	}
}
