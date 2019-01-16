package com.reactlibrary;

import android.content.Intent;
import cc.echonet.coolmicdspjava.VUMeterResult;
import cc.echonet.coolmicdspjava.Wrapper;
import cc.echonet.coolmicdspjava.WrapperConstants;

public class AudioBroadcastService extends IntentService {
  public AudioBroadcastService() {
      super("AudioBroadcastService");
  }

  @Override
  public void onCreate() {
    Wrapper.init();
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    int status = Wrapper.init(this, server, port_num, username, password, mountpoint, codec_string, sampleRate, Integer.parseInt(channel_string), buffersize);
    
    if (status == 0) {
      status = Wrapper.start();
    }

    return START_NOT_STICKY;
  }

  @Override
  public void onDestroy() {
    if (Wrapper.getState() == WrapperConstants.WrapperInitializationStatus.WRAPPER_INTITIALIZED && Wrapper.hasCore()) {
        Wrapper.stop();
        Wrapper.unref();
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