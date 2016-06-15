package com.matsdevelopsolutions.txdownloadmanagersample;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by mateusz on 6/15/16.
 */
public class DownloadClickReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        long[] downloadId = intent.getLongArrayExtra(DownloadManager.EXTRA_NOTIFICATION_CLICK_DOWNLOAD_IDS);
        Log.i("DownloadClickReceiver", "Download clicked "+String.valueOf(downloadId));

    }
}
