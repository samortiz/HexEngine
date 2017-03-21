package com.alwaysrejoice.hexengine.dto;

import android.graphics.Bitmap;

public class Image {
  private String fileName;
  private Bitmap bitmap;


  public Image(String fileName, Bitmap bitmap) {
    this.fileName = fileName;
    this.bitmap = bitmap;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public Bitmap getBitmap() {
    return bitmap;
  }

  public void setBitmap(Bitmap bitmap) {
    this.bitmap = bitmap;
  }
}
