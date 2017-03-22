package com.alwaysrejoice.hexengine.util;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.alwaysrejoice.hexengine.dto.BitmapGsonAdapter;
import com.alwaysrejoice.hexengine.dto.Game;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {

  // Custom GSON serialize/deserializer
  // This will allow my DTO to store/load Bitmaps as Base64
  public static final Gson gson = new GsonBuilder().registerTypeHierarchyAdapter(Bitmap.class, new BitmapGsonAdapter()).create();

  public static final String GAME_DIR = "HexEngine/games";
  public static final String IMAGE_DIR = "HexEngine/images";

  /**
   * @return  Path to the game storage location in external storage
   */
  public static File getGamePath() {
    return new File(Environment.getExternalStorageDirectory(), GAME_DIR);
  }

  /**
   * @return the path to the file containing the specified game
   */
  public static File getGameFile(String gameName) {
    File file =  null;
    if (!isExternalStorageWritable()) {
      Log.e("ERROR", "External Storage is not writable");
      return null;
    }
    try {
      File gameDir = getGamePath();
      file = new File(gameDir, gameName+".json");
    } catch (Exception e) {
      Log.e("GameUtils", "Error creating file for "+gameName, e);
    }
    return file;
  }

  /* Checks if external storage is available for read and write */
  public static boolean isExternalStorageWritable() {
    String state = Environment.getExternalStorageState();
    if (Environment.MEDIA_MOUNTED.equals(state)) {
      return true;
    }
    return false;
  }

  /**
   * Loads a bitmap from assets something like "/images/template/grass.png"
   * NOT external storage where the games are saved like "HexEngine/images/..."
   */
  public static Bitmap loadBitmap(AssetManager assetManager, String filename) {
    InputStream inputStream = null;
    Bitmap bitmap = null;
    try {
      //Log.d("util", "Opening bitmap");
      inputStream = assetManager.open(filename);
      bitmap = BitmapFactory.decodeStream(inputStream);
    } catch (IOException e) {
      Log.e("utils", "Error reading bitmap from assets ", e);
    } finally {
      try {
        inputStream.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return bitmap;
  }

  /**
   * Load a bitmap from external storage, not from assets
   */
  public static Bitmap loadBitmap(String fileName) {
    File imgFile = getImageFile(fileName);
    InputStream inputStream = null;
    Bitmap bitmap = null;
    try {
      Log.d("fileUtil", "loadBitmap fileName="+fileName);
      inputStream = new FileInputStream(imgFile);
      bitmap = BitmapFactory.decodeStream(inputStream);
    } catch (IOException e) {
      Log.e("loadBitmap", "Error loading bitmap for fileName="+fileName, e);
    } finally {
      try {
        inputStream.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return bitmap;
  }

  /**
   * @return the path to the file containing the specified game
   */
  public static File getImageFile(String fileName) {
    File file =  null;
    if (!isExternalStorageWritable()) {
      Log.e("ERROR", "External Storage is not writable");
      return null;
    }
    try {
      File dir = new File(Environment.getExternalStorageDirectory(), IMAGE_DIR);
      file = new File(dir, fileName+".png");
    } catch (Exception e) {
      Log.e("GameUtils", "Error creating file for "+fileName, e);
    }
    return file;
  }


  /**
   * Creates and sets up the external storage as necessary.
   * This is only run the first time, but we need to check it just in case.
   */
  public static void initDisk(AssetManager assetManager) {
    File imgDir = new File(Environment.getExternalStorageDirectory(), IMAGE_DIR);
    if (!imgDir.exists()) {
      imgDir.mkdirs();
      copyFilesFromAssetsToStorage(assetManager, "images/template");
    }

    File gameDir = getGamePath();
    if (!gameDir.exists()) {
      gameDir.mkdirs();
      // TODO : Copy template games
    }
  }

  /**
   * This will only run the copy if
   * Copies the template image files from assets to the external storage folder
   */
  public static void copyFilesFromAssetsToStorage(AssetManager assetManager, String filePath) {
    try {
      String[] fileNames = assetManager.list(filePath);
      for (String fileName: fileNames) {
        InputStream in = assetManager.open(filePath+"/" + fileName);
        org.apache.commons.io.FileUtils.copyInputStreamToFile(in, getImageFile(fileName));
        in.close();
        Log.d("fileUtils", "copied file=" + fileName+ " from assets to external storage");
      }
    } catch (IOException e) {
      Log.e("fileUtils", "Error copying files from assets to storage", e);
    }
  }




}
