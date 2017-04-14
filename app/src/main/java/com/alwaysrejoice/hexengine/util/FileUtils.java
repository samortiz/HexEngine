package com.alwaysrejoice.hexengine.util;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {

  public static final String ROOT_DIR = "HexEngine";
  public static final String GAME_DIR = ROOT_DIR+"/games";
  public static final String IMAGE_BACKGROUND_DIR = ROOT_DIR+"/images/background";
  public static final String IMAGE_UNITS_DIR = ROOT_DIR+"/images/units";
  public static final String IMAGE_EFFECTS_DIR = ROOT_DIR+"/images/effects";
  public static final String IMAGE_ABILITIES_DIR = ROOT_DIR+"/images/abilities";
  public static final String MOD_DIR = ROOT_DIR+"/mods";
  public static final String WORLDS_DIR = "/worlds";

  /**
   * @return  Path to the game storage location in external storage
   */
  public static File getExtPath(String path) {
    if (!isExternalStorageWritable()) {
      Log.e("ERROR", "External Storage is not writable");
      return null;
    }
    return new File(Environment.getExternalStorageDirectory(), path);
  }

  /**
   * @return the path to the file containing the specified game
   */
  public static File getGameFile(String gameName) {
    return getExtJsonFile(gameName, GAME_DIR);
  }

  /**
   * @return the file object for the specified world
   */
  public static File getWorldFile(String worldName) {
    return getExtJsonFile(worldName, WORLDS_DIR);
  }

  /**
   *  Checks if a file exists on external storage
   * @param dir use one of the *_DIR constants in this file
   * @param fileName name of the file (including the .json)
   * @return true if the file exists
   */
  public static boolean fileExists(String dir, String fileName) {
    try {
      File fullDir = getExtPath(dir);
      File file = new File(fullDir, fileName);
      return file.exists();
    } catch (Exception e) {
      Log.e("GameUtils", "Error checking for file "+dir+"/"+fileName, e);
    }
    return false;
  }

  /**
   * Gets a file from the external file system (testing for writability)
   * @param fileName name of the file without the .json extension
   * @param dir to external path (use constants in this file *_DIR)
   * @return the File object
   */
  public static File getExtJsonFile(String fileName, String dir) {
    File file =  null;
    if (!isExternalStorageWritable()) {
      Log.e("ERROR", "External Storage is not writable");
      return null;
    }
    try {
      File fullDir = getExtPath(dir);
      file = new File(fullDir, fileName+".json");
    } catch (Exception e) {
      Log.e("GameUtils", "Error getting file "+fileName, e);
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
   * fileName is the full file name with .png on the end
   * @param extDir : Path in external files (use FileUtils.*_DIR constants)
   */
  public static Bitmap loadBitmap(String extDir, String fileName) {
    File imgFile = new File(getExtPath(extDir), fileName);
    InputStream inputStream = null;
    Bitmap bitmap = null;
    try {
      //Log.d("fileUtil", "loadBitmap fileName="+fileName);
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
   * Creates and sets up the external storage as necessary.
   * This is only run the first time, but we need to check it just in case.
   */
  public static void initDisk(AssetManager assetManager) {
    //deleteExtFiles(); // DEBUG
    initDir(assetManager, "images/background", IMAGE_BACKGROUND_DIR);
    initDir(assetManager, "images/units", IMAGE_UNITS_DIR);
    initDir(assetManager, "images/effects", IMAGE_EFFECTS_DIR);
    initDir(assetManager, "images/abilities", IMAGE_ABILITIES_DIR);
    initDir(assetManager, "games", GAME_DIR);
    initDir(assetManager, "worlds", WORLDS_DIR);
  }

  // Copies files from the specified assets folder to the external storage directory
  public static void initDir(AssetManager assetManager, String assetDir, String extPath) {
    File dir = getExtPath(extPath);
    if (!dir.exists()) {
      dir.mkdirs();
      copyFilesFromAssetsToStorage(assetManager, assetDir, extPath);
    }
  }

  /**
   * This will only run the copy if
   * Copies the template image files from assets to the external storage folder
   */
  public static void copyFilesFromAssetsToStorage(AssetManager assetManager, String assetPath, String extPath) {
    try {
      String[] fileNames = assetManager.list(assetPath);
      for (String fileName: fileNames) {
        InputStream in = assetManager.open(assetPath+"/" + fileName);
        org.apache.commons.io.FileUtils.copyInputStreamToFile(in, new File(getExtPath(extPath), fileName));
        in.close();
        Log.d("fileUtils", "copied file=" + fileName+ " from assets/"+assetPath+" to external storage "+extPath);
      }
    } catch (IOException e) {
      Log.e("fileUtils", "Error copying files from assets to storage", e);
    }
  }

  /**
   * Cleanup all the storage (probably useful just for debug/testing purposes)
   */
  public static void deleteExtFiles() {
    deleteDir(IMAGE_BACKGROUND_DIR);
    deleteDir(IMAGE_UNITS_DIR);
    deleteDir(IMAGE_EFFECTS_DIR);
    deleteDir(IMAGE_ABILITIES_DIR);
    deleteDir(GAME_DIR);
    deleteDir(WORLDS_DIR);
    deleteDir(ROOT_DIR);
  }

  /**
   * Deletes the contents of an external folder (non-recursively.. only one level!)
   */
  public static void deleteDir(String path) {
    File dir = FileUtils.getExtPath(path);
    if (dir.exists()) {
      for (String fileName : dir.list()) {
        File file = new File(dir, fileName);
        file.delete();
        Log.d("Deleting", "deleting " + path + "/" + fileName);
      } // for
      dir.delete();
    }
  }

}
