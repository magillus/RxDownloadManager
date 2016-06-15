package com.matsdevelopsolutions.txdownloadmanagersample;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;

public class DownloadActivity extends AppCompatActivity {

    BroadcastReceiver downloadComplete;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        // start download

        Button downloadBtn = (Button) findViewById(R.id.download_file_button);
        final TextView statusLine = (TextView) findViewById(R.id.status_line);


        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadManager downloadManager = (DownloadManager) getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse("http://perlak.com/projects/MeshNavigator.code"));
                request.setTitle("Test Download #1");
                request.setAllowedOverRoaming(false);
                request.setAllowedOverMetered(false); // API >= 16 (JELLY_BEAN)
                request.setDescription("Download asset description for UI (notification, downloads UI)");
                request.setMimeType("application/octet-stream");
                // optional
                request.setDestinationUri(Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "testdownload1.bin")));
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);// permission
                downloadManager.enqueue(request);
                statusLine.setText("Started download...");
            }
        });

        downloadComplete = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                statusLine.setText("download complete");
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(downloadComplete, intentFilter);

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(downloadComplete);
    }
}
