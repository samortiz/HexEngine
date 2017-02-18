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
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class MapView extends SurfaceView implements Runnable {
    Bitmap grass;
    Bitmap tree;
    Bitmap background;
    int VIEW_SIZE_X = 1000;
    int VIEW_SIZE_Y = 1000;

    int BACKGROUND_SIZE_X = 2500;
    int BACKGROUND_SIZE_Y = 2500;
    int TILE_SIZE_X = 60;
    int TILE_SIZE_Y = 60;

    // Current location of view in the background
    int viewX = 100;
    int viewY = 200;

    // Current location touched on the screen
    float touchX = 100;
    float touchY = 100;

    // Scaling
    private ScaleGestureDetector mapScaleDetector;
    private float mapScaleFactor = 1.f;

    // Core screen drawing
    Thread renderThread = null;
    SurfaceHolder holder;
    volatile boolean running = false;

    public MapView(Context context) {
        super(context);
        holder = getHolder();
        mapScaleDetector = new ScaleGestureDetector(context, new MapScaleListener());


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

            Random rand = new Random();

            // Make a background map
            background = Bitmap.createBitmap(BACKGROUND_SIZE_X, BACKGROUND_SIZE_Y, Bitmap.Config.ARGB_8888);
            Canvas bgCanvas = new Canvas(background);
            bgCanvas.drawRGB(100, 200, 200);
            for (int x=0; x<=BACKGROUND_SIZE_X; x+=TILE_SIZE_X) {
                for (int y=0; y<=BACKGROUND_SIZE_X; y+= TILE_SIZE_Y) {
                    bgCanvas.drawBitmap(grass, x, y, null);
                    if (rand.nextInt(10) > 8) {
                        Paint paint = new Paint();
                        String colorHex = "#" + Integer.toHexString(20 + rand.nextInt(200)) +
                                Integer.toHexString(20 + rand.nextInt(200)) +
                                Integer.toHexString(20 + rand.nextInt(200));
                        Log.d("MapView", "Color:'" + colorHex + "'");
                        paint.setColor(Color.parseColor(colorHex));
                        bgCanvas.drawCircle(x, y, 10, paint);
                    }
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
                Log.d("surface", "Surface is not valid");
                continue;
            }
            Canvas canvas = holder.lockCanvas();
            canvas.drawRGB(0, 0, 255);

            Rect subsetView = new Rect(viewX, viewY, viewX+VIEW_SIZE_X, viewY+VIEW_SIZE_Y);
            Rect scaleView = new Rect(0, 0, VIEW_SIZE_X, VIEW_SIZE_Y);

            canvas.drawBitmap(background, subsetView, scaleView, null);

            canvas.drawBitmap(tree, VIEW_SIZE_X/2, VIEW_SIZE_Y/2, null); // center point
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

    public void touchUp(float x, float y) {
        this.touchX = x;
        this.touchY = y;

        int newX = viewX + (Math.round(x) - (VIEW_SIZE_X / 2));
        int newY = viewY + (Math.round(y) - (VIEW_SIZE_Y /2));

        // Bounds checking
        if (newX < 0) newX = 0;
        if (newY < 0) newY = 0;
        if (newX > BACKGROUND_SIZE_X - VIEW_SIZE_X) {
            newX = BACKGROUND_SIZE_X - VIEW_SIZE_X;
        }
        if (newY > BACKGROUND_SIZE_Y - VIEW_SIZE_Y) {
            newY = BACKGROUND_SIZE_Y - VIEW_SIZE_Y;
        }

        this.viewX = newX;
        this.viewY = newY;
        Log.d("touch", "at "+viewX+","+viewY);
    }

    public void pan(float x, float y) {
        Log.d("gesture", "pan "+x+","+y);
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mapScaleDetector.onTouchEvent(event);
        Log.d("Touch", "onTouchEvent");
        return false;
    }

    private class MapScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mapScaleFactor *= detector.getScaleFactor();
            // Don't let the object get too small or too large.
            mapScaleFactor = Math.max(0.1f, Math.min(mapScaleFactor, 5.0f));
            invalidate();
            Log.d("Scale", "Scaling to "+mapScaleFactor);
            return true;
        }
    }
}

