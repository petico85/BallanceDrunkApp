package com.example.petico.ballancedrunkapp;

/**
 * Created by Petico on 2018. 03. 03..
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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


    private static final int speedLimit = 20;

    private SensorManager sensorManager;
    private Sensor gyroscopeSensor;
    private SensorEventListener gyroscopeEventListener;
    private float x=0,y=0,z=0;
    private boolean gyroStart = false;
    private Bitmap chibiBitmap1;
    private Bitmap background;
    private Bitmap barTableBitmap;
    private DrunkPhysics drunkPhysics = null;


    private DrunkGuyCharacter drunkGuy01 = null;
    private StaticGameObject barTable = null;

    public GameSurface(Context context)  {
        super(context);

        //beállítjuk a bitmapokat
        setBitmaps(R.drawable.chibi1,R.drawable.floor01,R.drawable.bar002);

        // Make Game Surface focusable so it can handle events. .
        this.setFocusable(true);

        // Sét callback.
        this.getHolder().addCallback(this);

        //gyrocontrol
        gyroManager(context);

        /*//create character
        Bitmap chibiBitmap1 = BitmapFactory.decodeResource(this.getResources(),R.drawable.chibi1);
        this.drunkGuy01 = new DrunkGuyCharacter(this,chibiBitmap1,this.getWidth()/2,this.getHeight()/2);//center*/

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

                //ha már létrejött az objektum, mivel a gyrosensor előbb kapcsol be mint alindulna a szál
                //nem kéne a surface-ben lennie ennek
                if(drunkGuy01 != null) {

                    if (gyroStart) {
                        x += sensorEvent.values[0];
                        y += sensorEvent.values[1];
                        z += sensorEvent.values[2];

                        drunkGuy01.setMovingVector((int) x, (int) -y);
                        System.out.println("SENSORVALUES X:" + x + ", Y:" + y + " Z:" + z);
                    } else {
                        x = 0;
                        y = 0;
                        z = 0;
                        drunkGuy01.setMovingVector(0, 0);
                        setNullForces();
                    }
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

        //főszereplőfizikája és ütközés vizsgálata
        collisionDetection();

        this.drunkGuy01.update();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            //gyyroscope le fel kapcsolaása
            gyroStart = !gyroStart;
        }
        return false;
    }

    @Override
    public void draw(Canvas canvas)  {
        //alap kirajzolása
        super.draw(canvas);

        //padló
        drawFloor(canvas);

        //statikus elemek
        this.barTable.draw(canvas);
        //npc


        //főszereplő
        this.drunkGuy01.draw(canvas);

        //debug
        drawDeveloperInfos(canvas);
    }

    // Implements method of SurfaceHolder.Callback
    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        this.gameThread = new GameThread(this,holder);
        this.gameThread.setRunning(true);
        this.gameThread.start();

        //csak a futás elkezdése után lesznek értékei a getwidth és gethight függvényeknek

        //statikus elemek
        this.barTable = new StaticGameObject(barTableBitmap, 1, 1, this.getWidth()-barTableBitmap.getWidth(), 1);
        //create character
        this.drunkGuy01 = new DrunkGuyCharacter(this,chibiBitmap1,this.getWidth()/2,this.getHeight()/2);//center

        //fizika
        drunkPhysics = new DrunkPhysics(75,drunkGuy01.getHeight());//karakter súly kilógramban/karakter magassága pixelben

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

    private void drawFloor(Canvas canvas) {

        for (int i = 0; i < this.getWidth(); i = i + this.background.getWidth()) {
            for(int i2=0; i2 < this.getHeight(); i2 = i2 + this.background.getHeight())
                canvas.drawBitmap(this.background, i, i2, null);
        }
    }

    private void setBitmaps(int chibiBitmap1ID, int backgroundID, int barTbaleID) {
        this.chibiBitmap1 = BitmapFactory.decodeResource(this.getResources(),chibiBitmap1ID);
        this.background = BitmapFactory.decodeResource(getResources(), backgroundID/*R.drawable.floor01*/);
        this.barTableBitmap = BitmapFactory.decodeResource(getResources(), barTbaleID);
    }


    private void collisionDetection() {
        int moveX = drunkGuy01.getMovingVectorX();
        int moveY = drunkGuy01.getMovingVectorY();

        //speedlimit
        if(moveX > speedLimit) moveX = speedLimit;
        else if(moveX < -speedLimit) moveX = -speedLimit;
        if(moveY > speedLimit) moveY = speedLimit;
        else if(moveY < -speedLimit) moveY = -speedLimit;


        //tehetetlenség
        //ide jön egy fizikai osztály hívás, ami kiszámolja, innentől kezdve a moveX moveY majd csak mozgatási erő lesz nem a konkrét elmozdulás mutatója

        this.drunkPhysics.processMove(moveX,moveY);
        System.out.println("processedway: " + drunkPhysics.getMoveX() +","+drunkPhysics.getMoveY());
        drunkGuy01.setX((int) (drunkGuy01.getX() +  drunkPhysics.getMoveX()));
        drunkGuy01.setY((int) (drunkGuy01.getY() +  drunkPhysics.getMoveY()));


        // When the game's character touches the edge of the screen
        if(drunkGuy01.getX() < 0 )  {
            drunkGuy01.setX(0);
            drunkPhysics.setVx(0);
            drunkPhysics.setFx(0);
        } else if(drunkGuy01.getX() > this.getWidth() - drunkGuy01.getWidth())  {
            drunkGuy01.setX(this.getWidth()- drunkGuy01.getWidth());
            drunkPhysics.setVx(0);
            drunkPhysics.setFx(0);
        }

        if(drunkGuy01.getY() < 0 )  {
            drunkGuy01.setY(0);
            drunkPhysics.setVy(0);
            drunkPhysics.setFy(0);
        } else if(drunkGuy01.getY() > this.getHeight()- drunkGuy01.getHeight())  {
            drunkGuy01.setY(this.getHeight()- drunkGuy01.getHeight());
            drunkPhysics.setVy(0);
            drunkPhysics.setFy(0);
        }
    }

    /**
     * A karakterre ható erők nullázása
     * */
    private void setNullForces()
    {
        drunkPhysics.setVx(0);
        drunkPhysics.setVy(0);
        drunkPhysics.setFx(0);
        drunkPhysics.setFy(0);
    }


    /**
     * csak fejlesztéshez kell kirajzolni
     * */
    private void drawDeveloperInfos(Canvas canvas)
    {
        Paint developerPaint = new Paint();
        developerPaint.setColor(Color.RED);
        //32*32-es megháromszorozva tehát 96/96 a sprite és a közepéről indulunk, a vonal méretét megtíszrezezzük hogy jobban látszon
        //vector
        canvas.drawLine((drunkGuy01.getX()+(drunkGuy01.getWidth()/2)),(drunkGuy01.getY()+(drunkGuy01.getHeight()/2)), (drunkGuy01.getX()+(drunkGuy01.getWidth()/2)) + (drunkGuy01.getMovingVectorX()*10),  (drunkGuy01.getY()+(drunkGuy01.getHeight()/2)) + (drunkGuy01.getMovingVectorY()*10), developerPaint);
        developerPaint.setColor(Color.WHITE);
        developerPaint.setTextSize(40);
        canvas.drawText("MovingvectorX: " + drunkGuy01.getMovingVectorX(), 10, 50, developerPaint);
        canvas.drawText("MovingvectorY: " + drunkGuy01.getMovingVectorY(), 10, 90, developerPaint);
        canvas.drawText("Speed X: " + (int) drunkPhysics.getVx() + " m/s", 10, 130, developerPaint);
        canvas.drawText("Speed Y: " + (int) drunkPhysics.getVy() + " m/s", 10, 170, developerPaint);
    }

}