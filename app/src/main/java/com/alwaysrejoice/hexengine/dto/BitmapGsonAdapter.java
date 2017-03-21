package com.alwaysrejoice.hexengine.dto;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;

/**
 * This will allow my DTO objects to store and load Bitmap
 * as a Base64 encoded native string in JSON
 */
public class BitmapGsonAdapter implements JsonSerializer<Bitmap>, JsonDeserializer<Bitmap> {

  public Bitmap deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    byte[] data = Base64.decode(json.getAsString(), Base64.NO_WRAP);
    return BitmapFactory.decodeByteArray(data, 0, data.length);
  }

  public JsonElement serialize(Bitmap bitmap, Type typeOfSrc, JsonSerializationContext context) {
    byte[] data = null;
    try {
      ByteArrayOutputStream stream = new ByteArrayOutputStream();
      bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
      data = stream.toByteArray();
      stream.close();
    } catch (Exception e) {
      Log.e("BitmapGsonAdapter", "error serializing bitmap", e);
    }
    if (data == null) return null;
    String json = Base64.encodeToString(data, Base64.NO_WRAP);
    Log.d("gson", "serialized bitmap to "+json);
    return new JsonPrimitive(json);
  }

}
