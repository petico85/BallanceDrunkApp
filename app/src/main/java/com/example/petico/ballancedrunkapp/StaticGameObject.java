package com.example.petico.ballancedrunkapp;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by Petico on 2018. 07. 03..
 */

public class StaticGameObject extends GameObject {


    public StaticGameObject(Bitmap image, int rowCount, int colCount, int x, int y) {
        super(image, rowCount, colCount, x, y);
    }

    public Bitmap getCurrentMoveBitmap()  {
        return image;
    }


    public void draw(Canvas canvas)  {
        Bitmap bitmap = this.getCurrentMoveBitmap();
        canvas.drawBitmap(bitmap,x, y, null);
    }
}
