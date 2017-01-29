package com.alwaysrejoice.hexengine.hexengine;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class MapView extends SurfaceView implements Runnable {
    float touchX = 100;
    float touchY = 100;
    Bitmap grass;
    Bitmap tree;
    Bitmap background;

    int viewX = 100;
    int viewY = 200;

    Thread renderThread = null;
    SurfaceHolder holder;
    volatile boolean running = false;

    public MapView(Context context) {
        super(context);
        holder = getHolder();
        InputStream inputStream = null;

        try {
            AssetManager assetManager = context.getAssets();
            inputStream = assetManager.open("grass.png");
            grass = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
            Log.d("BitmapText", "grass.png format: " + grass.getConfig());

            inputStream = assetManager.open("tree.png");
            BitmapFactory.Options options = new BitmapFactory.Options();
            tree = BitmapFactory.decodeStream(inputStream, null, options);
            Log.d("BitmapText", "tree.png format: " + tree.getConfig());

            int maxX = 1500;
            int maxY = 1500;
            int tileWidth = 60;
            int tileHeight = 60;
            Random rand = new Random();

            // Make a map
            background = Bitmap.createBitmap(maxX, maxY, Bitmap.Config.ARGB_8888);
            Canvas bgCanvas = new Canvas(background);
            bgCanvas.drawRGB(100, 200, 200);
            for (int x=0; x<=maxX; x+=tileWidth) {
                for (int y=0; y<=maxY; y+= tileHeight) {
                    bgCanvas.drawBitmap(grass, x, y, null);
                    Paint paint = new Paint();
                    String colorHex = "#"+Integer.toHexString(20+rand.nextInt(200))+
                            Integer.toHexString(20+rand.nextInt(200))+
                            Integer.toHexString(20+rand.nextInt(200));
                    Log.d("MapView", "Color:'"+colorHex+"'");
                    paint.setColor(Color.parseColor(colorHex));
                    bgCanvas.drawCircle(x, y, 10, paint);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {inputStream.close();} catch (IOException e) {}
            }
        }
    }

    public void resume() {
        Log.d("MapView", "resume");
        running = true;
        renderThread = new Thread(this);
        renderThread.start();
    }

    public void run() {
        Log.d("MapView", "running");
        while (running) {
            if (!holder.getSurface().isValid()) {
                continue;
            }
            Canvas canvas = holder.lockCanvas();
            canvas.drawRGB(0, 0, 255);

            int viewSizeX = 1000;
            int viewSizeY = 2000;

            RectF scaleView = new RectF(0, 0,viewSizeX, viewSizeY);
            Rect panView = new Rect(viewX, viewY, viewX+viewSizeX, viewY+viewSizeY);

            canvas.drawBitmap(background, panView, scaleView, null);

            canvas.drawBitmap(tree, touchX - tree.getWidth() / 2, touchY - tree.getHeight() / 2, null);
            holder.unlockCanvasAndPost(canvas);
        }
    }

    public void pause() {
        Log.d("MapView", "pause");
        running = false;
        while(true) {
            try {
                renderThread.join();
                return;
            } catch (InterruptedException e) {
                // retry
            }
        }
    }

    public void touch(float x, float y) {
        this.touchX = x;
        this.touchY = y;
        this.viewX = Math.round(x);
        this.viewY = Math.round(y);
    }
}

