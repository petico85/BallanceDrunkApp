package com.example.petico.ballancedrunkapp;

/**
 * Created by Petico on 2018. 03. 03..
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import static android.content.Context.SENSOR_SERVICE;
import android.hardware.Sensor;


//import static android.content.Context.SENSOR_SERVICE;

public class GameSurface extends SurfaceView implements SurfaceHolder.Callback {


    private GameThread gameThread;

    private SensorManager sensorManager;
    private Sensor gyroscopeSensor;
    private SensorEventListener gyroscopeEventListener;
    private float x=0,y=0,z=0;
    private boolean gyroStart = false;


    private DrunkGuyCharacter drunkGuy01;

    public GameSurface(Context context)  {
        super(context);

        //create character
        Bitmap chibiBitmap1 = BitmapFactory.decodeResource(this.getResources(),R.drawable.chibi1);
        this.drunkGuy01 = new DrunkGuyCharacter(this,chibiBitmap1,this.getWidth()/2,this.getHeight()/2);//center

        // Make Game Surface focusable so it can handle events. .
        this.setFocusable(true);

        // Sét callback.
        this.getHolder().addCallback(this);

        //gyrocontrol
        gyroManager(context);


    }

    /**
     * Gyroscope jelek feldolgozása, továbbítása
     *
     * */
    public void gyroManager(Context context){
        sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if(gyroscopeSensor == null){
            System.out.println("Your device does not have gyroscope sensor.");
        }

        // Create a listener
        gyroscopeEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {

                if(gyroStart) {
                    x += sensorEvent.values[0];
                    y += sensorEvent.values[1];
                    z += sensorEvent.values[2];

                    drunkGuy01.setMovingVector((int) x, (int) -y);
                    System.out.println("SENSORVALUES X:" + x + ", Y:" + y + " Z:" + z);
                }
                else{
                    x=0;
                    y=0;
                    z=0;
                    drunkGuy01.setMovingVector(0, 0);
                    drunkGuy01.setNullForces();
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };

        // Register the listener
        sensorManager.registerListener(gyroscopeEventListener,
                gyroscopeSensor, SensorManager.SENSOR_DELAY_GAME);
    }


    public void update()  {
        this.drunkGuy01.update();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            gyroStart = !gyroStart;
            /*int x=  (int)event.getX();
            int y = (int)event.getY();

            int movingVectorX =x-  this.drunkGuy01.getX() ;
            int movingVectorY =y-  this.drunkGuy01.getY() ;

            this.drunkGuy01.setMovingVector(movingVectorX,movingVectorY);
            return true;
            */
        }
        return false;
    }

    @Override
    public void draw(Canvas canvas)  {
        super.draw(canvas);

        this.drunkGuy01.draw(canvas);
    }

    // Implements method of SurfaceHolder.Callback
    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        this.gameThread = new GameThread(this,holder);
        this.gameThread.setRunning(true);
        this.gameThread.start();
    }

    // Implements method of SurfaceHolder.Callback
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    // Implements method of SurfaceHolder.Callback
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry= true;
        while(retry) {
            try {
                this.gameThread.setRunning(false);

                // Parent thread must wait until the end of GameThread.
                this.gameThread.join();
            }catch(InterruptedException e)  {
                e.printStackTrace();
            }
            retry= true;
        }
    }

}