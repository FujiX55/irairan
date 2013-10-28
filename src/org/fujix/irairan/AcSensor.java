package org.fujix.irairan;

import android.content.*;
import android.hardware.*;
import java.util.*;

public class AcSensor implements SensorEventListener
{

	private SensorManager _sensorManager = null;
	private float _x, _y, _z;

	public void onCreate(Context c)
	{
		_sensorManager = (SensorManager) c.getSystemService(Context.SENSOR_SERVICE);//センサーマネージャを取得
		onResume();
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy)
	{
		//今回は使用しません。
	}

	//アクティビティが動き始めたらリスナーを登録する
	public void onResume()
	{
		List<Sensor> sensorList = _sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);//センサーリストを取得
		if (sensorList != null && !sensorList.isEmpty())
		{
			Sensor sensor = sensorList.get(0);
			_sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);//リスナー登録
		}
	}

	//アクティビティがポーズになったらリスナーを止める
	public void onPause()
	{
		if (_sensorManager == null)
		{
			return;
		}
		_sensorManager.unregisterListener(this);
	}

	//センサーの値に変化があった時呼ばれる
	public void onSensorChanged(SensorEvent event)
	{
		if (event.sensor.getType() == Sensor.TYPE_ORIENTATION)
		{
			// values[0]:
			// Azimuth, angle between the magnetic north direction and the Y axis,
			// around the Z axis (0 to 359). 0=North, 90=East, 180=South, 270=West
			// values[1]:
			// Pitch, rotation around X axis (-180 to 180),
			// with positive values when the z-axis moves toward the y-axis.
			// values[2]:
			// Roll, rotation around Y axis (-90 to 90),
			// with positive values when the x-axis moves away from the z-axis.
			//
			_x = event.values[SensorManager.DATA_X]; // X軸
			_y = event.values[SensorManager.DATA_Y]; // Y軸
			_z = event.values[SensorManager.DATA_Z]; // Z軸
		}
	}

	public float getX()
	{
		return _x;
	}

	public float getY()
	{
		return _y;
	}

	public float getZ()
	{
		return _z;
	}

	//シングルトン
	private static AcSensor _instance = new AcSensor();
	private AcSensor()
	{
		_x = _y = _z = 0;
	}
	public static AcSensor GetInstance()
	{
		return _instance;
	}
}
