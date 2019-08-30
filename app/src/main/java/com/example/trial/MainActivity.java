package com.example.trial;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewDebug;
import android.widget.Button;
import android.widget.TextView;

import com.example.trial.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;


public class MainActivity extends AppCompatActivity {
    Button buttonStart;
    Button buttonStop;
    File writer;
    FileOutputStream fOut;
    OutputStreamWriter myOutWriter;
    int cnt=1;

    SensorManager mySensorManager;
    Sensor mySensor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonStart = (Button)findViewById(R.id.buttonStart);
        buttonStop = (Button)findViewById(R.id.buttonStop);


        mySensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mySensor = mySensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        buttonStart.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                buttonStart.setEnabled(false);
                buttonStop.setEnabled(true);

                //String txt = getStorageDir() + File.separator + "sensor_"+ String.valueOf(cnt) +".csv";
                String txt = "/sdcard/DriveSyncFiles/"+ "sensor_"+ String.valueOf(cnt) +".csv";
                writer = new File(txt);
                try {
                    boolean result = writer.createNewFile();
                    if (!result) {
                        Log.e("createfile", "file create failed!");
                    }
                    fOut = new FileOutputStream(writer);
                    myOutWriter = new OutputStreamWriter(fOut);
                    myOutWriter.append(String.format("time,Bx,By,Bz\n"));
                }catch(Exception e){
                    e.printStackTrace();
                }

                mySensorManager.registerListener(proximitySensorEventListener,mySensor,SensorManager.SENSOR_DELAY_FASTEST);

                return true;
            }
        }) ;
        buttonStop.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                buttonStart.setEnabled(true);
                buttonStop.setEnabled(false);

                mySensorManager.unregisterListener(proximitySensorEventListener);

                try {
                    myOutWriter.flush();
                    myOutWriter.close();
                    fOut.close();
                }catch (Exception e){
                    e.printStackTrace();
                }

                cnt=cnt+1;

                return true;
            }
        });


    }

    private String getStorageDir() {
        return this.getExternalFilesDir(null).getAbsolutePath();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    SensorEventListener proximitySensorEventListener
            = new SensorEventListener() {
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO Auto-generated method stub
        }

        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                // get values for each axes X,Y,Z
                try {
                    //Log.e("onSensorChanged", "new sensor data arrived!");
                    myOutWriter.append(String.format("%d,%f,%f,%f\n", System.currentTimeMillis()%10000, event.values[0], event.values[1], event.values[2]));
                }catch(Exception e){
                    e.printStackTrace();
                }

            }
        }
    };
}

