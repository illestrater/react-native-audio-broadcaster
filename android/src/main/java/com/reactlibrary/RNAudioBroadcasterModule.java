
package com.reactlibrary;

import android.media.AudioRecord;
import android.media.AudioFormat;
import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;

import cc.echonet.coolmicdspjava.VUMeterResult;
import cc.echonet.coolmicdspjava.Wrapper;
import cc.echonet.coolmicdspjava.WrapperConstants;

public class RNAudioBroadcasterModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext reactContext;

  public RNAudioBroadcasterModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "RNAudioBroadcaster";
  }

  @ReactMethod
  public void init(Integer playerId, Callback callback) {
    String server = "cue.dj";
    Integer port_num = 1337;
    String username = "source";
    String password = "ethsavedmusic";
    String mountpoint = "holyshit";
    String codec_string = "audio/ogg; codec=vorbis";
    String sampleRate_string = "44100";
    String channel_string = "1";
    int sampleRate = Integer.parseInt(sampleRate_string);
    Log.e("INITIALIZING INITIAILZING?: ", "hello");

    Integer buffersize = AudioRecord.getMinBufferSize(Integer.parseInt(sampleRate_string), Integer.parseInt(channel_string) == 1 ? AudioFormat.CHANNEL_IN_MONO : AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT);

    WritableMap data = new WritableNativeMap();
    Wrapper.init();

    int status = Wrapper.init(this, server, port_num, username, password, mountpoint, codec_string, sampleRate, Integer.parseInt(channel_string), buffersize);
    
    if (status != 0) {
      Log.e("ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ: ", "ERROR WITH INITIALIZATION");
    } else {
      status = Wrapper.start();
      Log.e("ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ: ", "STARTING........");
    }
    
    data.putInt("hi", buffersize);


    if (callback != null) {
        callback.invoke(data);
        return;
    }
  }

  private void callbackHandler(WrapperConstants.WrapperCallbackEvents what, int arg0, int arg1) {
      Log.e("LOOOOOGS?: ", String.valueOf(arg0));
      Log.e("LOOOOOGS?: ", String.valueOf(arg1));
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
      Log.e("Handler VUMeter: ", String.valueOf(result.global_power));
  }
}