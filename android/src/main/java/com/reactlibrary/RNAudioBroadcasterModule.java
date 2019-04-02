package com.reactlibrary;

import android.media.AudioRecord;
import android.media.AudioFormat;
import android.content.Intent;
import android.app.Service;
import android.util.Log;
import android.net.Uri;
import android.provider.Settings;
import android.os.Messenger;
import android.os.Message;
import android.os.Handler;
import android.os.Bundle;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;

import cc.echonet.coolmicdspjava.VUMeterResult;
import cc.echonet.coolmicdspjava.Wrapper;
import cc.echonet.coolmicdspjava.WrapperConstants;

public class RNAudioBroadcasterModule extends ReactContextBaseJavaModule {
  private final ReactApplicationContext reactContext;
  private Integer initialized = 0;
  private Intent intent;
  private static String level = "-60";

  public RNAudioBroadcasterModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "RNAudioBroadcaster";
  }

  private static class IncomingHandler extends Handler {
    @Override
    public void handleMessage(Message msg) {
      Bundle message = msg.getData();
      level = String.valueOf(msg.arg1);
    }
  }

  @ReactMethod
  public void init(ReadableMap settings, Callback callback) {
    Messenger messenger = new Messenger(new IncomingHandler());
    ReactApplicationContext context = getReactApplicationContext();
    Intent intentBattery = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
    intentBattery.setData(Uri.parse("package:" + "com.cuenative"));
    context.startActivity(intentBattery);

    intent = new Intent(context, AudioBroadcastService.class);
    intent.putExtra("url", settings.getString("url"));
    intent.putExtra("port", String.valueOf(settings.getInt("port")));
    intent.putExtra("password", settings.getString("password"));
    intent.putExtra("mount", settings.getString("mount"));
    intent.putExtra("messenger", messenger);
    context.startService(intent);

    WritableMap data = new WritableNativeMap();
    data.putInt("hi", 1);

    if (callback != null) {
        callback.invoke(data);
        return;
    }
  }

  @ReactMethod
  public void stop() {
    ReactApplicationContext context = getReactApplicationContext();
    context.stopService(intent);
  }

  @ReactMethod
  public void levels(Callback callback) {
    WritableMap data = new WritableNativeMap();
    Log.e("GOT LEVEL ---------------------------- ", level);
    data.putString("level", level);
    if (callback != null) {
        callback.invoke(data);
        return;
    }
  }
}