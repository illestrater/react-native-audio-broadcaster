
package com.reactlibrary;

import android.media.AudioRecord;
import android.media.AudioFormat;
import android.util.Log;

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
  private String level = "-60";

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
    String server = settings.getString("url");
    Integer port_num = settings.getInt("port");
    String username = "source";
    String password = settings.getString("password");
    String mountpoint = settings.getString("mount");
    String codec_string = "audio/ogg; codec=vorbis";
    String sampleRate_string = "44100";
    String channel_string = "2";
    int sampleRate = Integer.parseInt(sampleRate_string);

    Integer buffersize = AudioRecord.getMinBufferSize(Integer.parseInt(sampleRate_string), Integer.parseInt(channel_string) == 1 ? AudioFormat.CHANNEL_IN_MONO : AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT);

    WritableMap data = new WritableNativeMap();
    Wrapper.init();

    int status = Wrapper.init(this, server, port_num, username, password, mountpoint, codec_string, sampleRate, Integer.parseInt(channel_string), buffersize);
    
    if (status == 0) {
      status = Wrapper.start();
    }

    data.putInt("hi", buffersize);

    if (callback != null) {
        callback.invoke(data);
        return;
    }
  }

  @ReactMethod
  public void stop() {
    if (Wrapper.getState() == WrapperConstants.WrapperInitializationStatus.WRAPPER_INTITIALIZED && Wrapper.hasCore()) {
        Wrapper.stop();
        Wrapper.unref();
    }
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