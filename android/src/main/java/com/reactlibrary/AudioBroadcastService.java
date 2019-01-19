package com.reactlibrary;

import android.content.Intent;
import android.content.Context;
import android.app.Service;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.NotificationChannel;
import android.support.v4.app.NotificationCompat;
import android.os.Bundle;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.media.AudioRecord;
import android.media.AudioFormat;
import android.util.Log;

import com.facebook.react.bridge.ReadableMap;

import cc.echonet.coolmicdspjava.VUMeterResult;
import cc.echonet.coolmicdspjava.Wrapper;
import cc.echonet.coolmicdspjava.WrapperConstants;

public class AudioBroadcastService extends Service {
  private String level = "-60";
  private WakeLock wakeLock;
  private WifiLock wifiLock;

  @Override
  public IBinder onBind(Intent intent) {
      return null;
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    wakeLock.acquire();
    wifiLock.acquire();

    Log.e("ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ: ", "STARTED!");
    String url = intent.getStringExtra("url");
    Integer port = Integer.parseInt(intent.getStringExtra("port"));
    String username = "source";
    String password = intent.getStringExtra("password");
    String mount = intent.getStringExtra("mount");
    String codec_string = "audio/ogg; codec=vorbis";
    String sampleRate_string = "44100";
    String channel_string = "2";
    int sampleRate = Integer.parseInt(sampleRate_string);

    Integer buffersize = AudioRecord.getMinBufferSize(Integer.parseInt(sampleRate_string), Integer.parseInt(channel_string) == 1 ? AudioFormat.CHANNEL_IN_MONO : AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT);

    Log.e("ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ: ", url);
    Wrapper.init();
    int status = Wrapper.init(this, url, port, username, password, mount, codec_string, sampleRate, Integer.parseInt(channel_string), buffersize);

    if (status == 0) {
        status = Wrapper.start();
        Log.e("ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ: ", "STREAMING STARTED");
    }

    super.onStartCommand(intent, flags, startId);
    return START_STICKY;
  }

  @Override
  public void onCreate() {
    Log.e("ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ: ", "CREATING");

    PowerManager powerManager = (PowerManager)this.getSystemService(Context.POWER_SERVICE);
    wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "audio-broadcast-wake-lock");
    wakeLock.setReferenceCounted(false);

    // Android 7: Use the application context here to prevent any memory leaks
    WifiManager wifiManager = (WifiManager)this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    wifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, "audio-broadcast-wifi-lock");
    wifiLock.setReferenceCounted(false);

    String packageName = this.getApplicationContext().getPackageName();
    if (powerManager.isIgnoringBatteryOptimizations(packageName)) {
        Log.e("ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ: ", "IGNORING BATTERY OPTIMIZATIONS");
    } else {
        Log.e("ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ: ", "BATTERY OPTIMIZATIONS HEAVY");
    }

    String CHANNEL_ID = "cue_broadcast";
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                "CUE Broadcasting",
                NotificationManager.IMPORTANCE_DEFAULT);

        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);
    }

    Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Broadcast")
            .setContentText("Wooo").build();

    startForeground(1, notification);
    super.onCreate();
  }

  @Override
  public void onDestroy() {
    if(wakeLock.isHeld()) wakeLock.release();
    if(wifiLock.isHeld()) wifiLock.release();

    if (Wrapper.getState() == WrapperConstants.WrapperInitializationStatus.WRAPPER_INTITIALIZED && Wrapper.hasCore()) {
        Wrapper.stop();
        Wrapper.unref();
    }

    super.onDestroy();
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