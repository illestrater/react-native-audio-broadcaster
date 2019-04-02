package com.reactlibrary;

import android.content.Intent;
import android.content.Context;
import android.app.PendingIntent;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.NotificationChannel;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.os.Bundle;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Process;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.media.AudioRecord;
import android.media.AudioFormat;
import android.util.Log;
import android.R;

import com.facebook.react.bridge.ReadableMap;

import cc.echonet.coolmicdspjava.VUMeterResult;
import cc.echonet.coolmicdspjava.Wrapper;
import cc.echonet.coolmicdspjava.WrapperConstants;

public class AudioBroadcastService extends IntentService {
  private String level = "-60";
  private WakeLock wakeLock;
  private WifiLock wifiLock;
  private Boolean continueRunning = false;

  public AudioBroadcastService() {
      super("AudioBroadcastService");
  }

  @Override
  public IBinder onBind(Intent intent) {
      return null;
  }

  @Override
  protected void onHandleIntent(Intent intent) {
      Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
      try {
        while (continueRunning) {
            Thread.sleep(5000);
        }
      } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
      }
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    continueRunning = true;
    wakeLock.acquire();
    wifiLock.acquire();

    String url = intent.getStringExtra("url");
    Integer port = Integer.parseInt(intent.getStringExtra("port"));
    String username = "source";
    String password = intent.getStringExtra("password");
    String mount = intent.getStringExtra("mount");
    String codec_string = "audio/ogg; codec=vorbis";
    String sampleRate_string = "48000";
    String channel_string = "2";
    String quality_string= "1";
    int sampleRate = Integer.parseInt(sampleRate_string);

    Integer buffersize = AudioRecord.getMinBufferSize(Integer.parseInt(sampleRate_string), Integer.parseInt(channel_string) == 1 ? AudioFormat.CHANNEL_IN_MONO : AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT);

    Wrapper.init();
    int status = Wrapper.init(this, url, port, username, password, mount, codec_string, sampleRate, Integer.parseInt(channel_string), buffersize*20);
    int testStatus = Wrapper.performMetaDataQualityUpdate("", "", Double.parseDouble(quality_string), 0);

    if (status == 0) {
        status = Wrapper.start();
    }

    startForegroundService();

    super.onStartCommand(intent, flags, startId);
    return START_STICKY;
  }

  @Override
  public void onCreate() {
    PowerManager powerManager = (PowerManager)this.getSystemService(Context.POWER_SERVICE);
    wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "audio-broadcast-wake-lock");
    wakeLock.setReferenceCounted(false);

    // Android 7: Use the application context here to prevent any memory leaks
    WifiManager wifiManager = (WifiManager)this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    wifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, "audio-broadcast-wifi-lock");
    wifiLock.setReferenceCounted(false);

    super.onCreate();
  }

  @Override
  public void onDestroy() {
    continueRunning = false;
    if(wakeLock.isHeld()) wakeLock.release();
    if(wifiLock.isHeld()) wifiLock.release();

    if (Wrapper.getState() == WrapperConstants.WrapperInitializationStatus.WRAPPER_INTITIALIZED && Wrapper.hasCore()) {
        Wrapper.stop();
        Wrapper.unref();
    }

    super.onDestroy();
  }

  private void startForegroundService() {
    String packageName = this.getApplicationContext().getPackageName();

    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

    String CHANNEL_ID = "cue_broadcast";
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                "CUE Broadcasting",
                NotificationManager.IMPORTANCE_DEFAULT);

        notificationManager.createNotificationChannel(channel);
    }

    Intent intent = this.getApplicationContext().getPackageManager().getLaunchIntentForPackage(packageName);
    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
    intent.setAction(Intent.ACTION_VIEW);
    intent.setData(Uri.parse("audiobroadcaster://notification.click"));
    PendingIntent pendingIntent = PendingIntent.getActivity(this.getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

    NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(this.getApplicationContext().getResources().getIdentifier("ic_notification", "mipmap", packageName))
            .setContentTitle("CUE Music Broadcast")
            .setContentText("You are broadcasting to CUE Music")
            .setStyle(new NotificationCompat.BigTextStyle()
                .setBigContentTitle("CUE Music Broadcast")
                .bigText("You are broadcasting to CUE Music"))
            .setPriority(Notification.PRIORITY_MAX)
            .setFullScreenIntent(pendingIntent, true);

    startForeground(1, notificationBuilder.build());
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