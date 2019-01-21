
package com.reactlibrary;

import android.media.AudioRecord;
import android.media.AudioFormat;
import android.content.Intent;
import android.app.Service;
import android.util.Log;
import android.net.Uri;
import android.provider.Settings;

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
  private AudioBroadcastService broadcaster = new AudioBroadcastService();
  private final ReactApplicationContext reactContext;
  private Integer initialized = 0;
  private String level = "-60";
  private Intent intent;

  public RNAudioBroadcasterModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "RNAudioBroadcaster";
  }

  @ReactMethod
  public void init(ReadableMap settings, Callback callback) {
    Log.e("ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ: ", "INITIALIZING");

    ReactApplicationContext context = getReactApplicationContext();
    Intent intentBattery = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
    intentBattery.setData(Uri.parse("package:" + "com.cuenative"));
    context.startActivity(intentBattery);

    intent = new Intent(context, AudioBroadcastService.class);
    intent.putExtra("url", settings.getString("url"));
    intent.putExtra("port", String.valueOf(settings.getInt("port")));
    intent.putExtra("password", settings.getString("password"));
    intent.putExtra("mount", settings.getString("mount"));
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
    data.putString("level", level);
    if (callback != null) {
        callback.invoke(data);
        return;
    }
  }

  private void callbackHandler(WrapperConstants.WrapperCallbackEvents what, int arg0, int arg1) {
      switch (what) {
          case THREAD_POST_START:
              break;
          case THREAD_PRE_STOP:
              break;
          case THREAD_POST_STOP:
              break;
          case ERROR:
              break;
          case STREAMSTATE:
              break;
          case RECONNECT:
              break;
      }
  }

  private void callbackVUMeterHandler(VUMeterResult result) {
      level = String.valueOf(result.global_power);
  }
}