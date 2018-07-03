package com.example.petico.ballancedrunkapp;

/**
 * Created by Petico on 2018. 03. 03..
 */
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class DrunkGuyCharacter extends GameObject {

    private static final int ROW_TOP_TO_BOTTOM = 0;
    private static final int ROW_RIGHT_TO_LEFT = 1;
    private static final int ROW_LEFT_TO_RIGHT = 2;
    private static final int ROW_BOTTOM_TO_TOP = 3;
    private static final int STAYING = 4;


    // Row index of Image are being used.
    private int rowUsing = STAYING;

    private int colUsing;

    /*
    private int[] inertiaX = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
    private int[] inertiaY = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
    private int inertiaCounter = 0;
    */

    private Bitmap[] leftToRights;
    private Bitmap[] rightToLefts;
    private Bitmap[] topToBottoms;
    private Bitmap[] bottomToTops;
    private Bitmap[] staying;

    // Velocity of game character (pixel/millisecond)
    //public static final float VELOCITY = 0.1f;

    private int movingVectorX = 0;
    private int movingVectorY = 0;

    private long lastDrawNanoTime =-1;

    private GameSurface gameSurface;


    public DrunkGuyCharacter(GameSurface gameSurface, Bitmap image, int x, int y) {
        super(image, 4, 3, x, y);

        this.gameSurface = gameSurface;

        this.topToBottoms = new Bitmap[colCount]; // 3
        this.rightToLefts = new Bitmap[colCount]; // 3
        this.leftToRights = new Bitmap[colCount]; // 3
        this.bottomToTops = new Bitmap[colCount]; // 3
        this.staying = new Bitmap[colCount];

        for(int col = 0; col< this.colCount; col++ ) {
            this.topToBottoms[col] = this.createSubImageAt(ROW_TOP_TO_BOTTOM, col);
            this.rightToLefts[col]  = this.createSubImageAt(ROW_RIGHT_TO_LEFT, col);
            this.leftToRights[col] = this.createSubImageAt(ROW_LEFT_TO_RIGHT, col);
            this.bottomToTops[col]  = this.createSubImageAt(ROW_BOTTOM_TO_TOP, col);
            this.staying[col]  = this.createSubImageAt(ROW_TOP_TO_BOTTOM, 1);
        }

    }

    public Bitmap[] getMoveBitmaps()  {
        switch (rowUsing)  {
            case ROW_BOTTOM_TO_TOP:
                return  this.bottomToTops;
            case ROW_LEFT_TO_RIGHT:
                return this.leftToRights;
            case ROW_RIGHT_TO_LEFT:
                return this.rightToLefts;
            case ROW_TOP_TO_BOTTOM:
                return this.topToBottoms;
            case STAYING:
                return this.staying;
            default:
                return null;
        }
    }

    public Bitmap getCurrentMoveBitmap()  {
        Bitmap[] bitmaps = this.getMoveBitmaps();
        return bitmaps[this.colUsing];
    }


    /**
     * A thread minden ciklusában lefut
     *
     * */
    public void update()  {

        this.colUsing++;
        if(colUsing >= this.colCount)  {
            this.colUsing =0;
        }


        // rowUsing
        if(movingVectorX==0 && movingVectorY==0){
            this.rowUsing = STAYING;
        }
        else {
            if (movingVectorX > 0) {
                if (movingVectorY > 0 && Math.abs(movingVectorX) < Math.abs(movingVectorY)) {
                    this.rowUsing = ROW_TOP_TO_BOTTOM;
                } else if (movingVectorY < 0 && Math.abs(movingVectorX) < Math.abs(movingVectorY)) {
                    this.rowUsing = ROW_BOTTOM_TO_TOP;
                } else {
                    this.rowUsing = ROW_LEFT_TO_RIGHT;
                }
            } else {
                if (movingVectorY > 0 && Math.abs(movingVectorX) < Math.abs(movingVectorY)) {
                    this.rowUsing = ROW_TOP_TO_BOTTOM;
                } else if (movingVectorY < 0 && Math.abs(movingVectorX) < Math.abs(movingVectorY)) {
                    this.rowUsing = ROW_BOTTOM_TO_TOP;
                } else {
                    this.rowUsing = ROW_RIGHT_TO_LEFT;
                }
            }
        }
    }

    public void draw(Canvas canvas)  {
        Bitmap bitmap = this.getCurrentMoveBitmap();
        canvas.drawBitmap(bitmap,x, y, null);
        // Last draw time.
        this.lastDrawNanoTime= System.nanoTime();
    }

    public void setMovingVector(int movingVectorX, int movingVectorY)  {
        this.movingVectorX= movingVectorX;
        this.movingVectorY = movingVectorY;
    }




    public int getMovingVectorX() {
        return movingVectorX;
    }

    public int getMovingVectorY() {
        return movingVectorY;
    }
}