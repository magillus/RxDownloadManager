package com.matsdevelopsolutions.txdownloadmanagersample;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;

public class DownloadActivity extends AppCompatActivity {

    BroadcastReceiver downloadCompleteReceiver;
    BroadcastReceiver downloadClickReceiver;
    DownloadClickReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        // start download

        final Button downloadBtn = (Button) findViewById(R.id.download_file_button);
        final TextView statusLine = (TextView) findViewById(R.id.status_line);


        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadManager downloadManager = (DownloadManager) getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse("http://perlak.com/projects/MeshNavigator.code"));
                request.setTitle("Test Download #1");
                request.setAllowedOverRoaming(false);
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
                //request.setAllowedOverMetered(false); // API >= 16 (JELLY_BEAN)
                request.setDescription("Download asset description for UI (notification, downloads UI)");
                request.setMimeType("application/octet-stream");
                // optional
                //request.setDestinationUri(Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "testdownload1.bin")));
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                long downloadId = downloadManager.enqueue(request);
                statusLine.setText("Started download..."+String.valueOf(downloadId));
            }
        });


        final Button downloadBtn2 = (Button) findViewById(R.id.download_file_button2);
        downloadBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadManager downloadManager = (DownloadManager) getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse("http://perlak.com/projects/movie_clip_1.mp4"));
                request.setTitle("Test Download #2");
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
                request.setAllowedOverRoaming(false);
                //request.setAllowedOverMetered(false); // API >= 16 (JELLY_BEAN)
                request.setDescription("Download asset description for UI (notification, downloads UI)");
                request.setMimeType("application/octet-stream");
                // optional
                //request.setDestinationUri(Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "testdownload1.bin")));
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                long downloadId = downloadManager.enqueue(request);
                statusLine.setText("Started download..."+String.valueOf(downloadId));
            }
        });

        downloadCompleteReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0l);
                statusLine.setText("download complete : "+String.valueOf(downloadId));
            }
        };

        downloadClickReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long[] downloadId = intent.getLongArrayExtra(DownloadManager.EXTRA_NOTIFICATION_CLICK_DOWNLOAD_IDS);
                statusLine.setText("download clicked : "+String.valueOf(downloadId));
            }
        };

        IntentFilter intentFilter= new IntentFilter(DownloadManager.ACTION_NOTIFICATION_CLICKED);
//        intentFilter.addCategory(Intent.CATEGORY_HOME);
        registerReceiver(downloadClickReceiver, intentFilter);

        receiver = new DownloadClickReceiver();
        registerReceiver(receiver, intentFilter);

    }



    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(downloadCompleteReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        unregisterReceiver(downloadClickReceiver);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(downloadCompleteReceiver);
    }
}
