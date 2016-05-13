package com.matsdevelopsolutions.txdownloadmanagersample;

import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.matsdevelopsolutions.rxdownloadmanager.DownloadUpdate;
import com.matsdevelopsolutions.rxdownloadmanager.RxDownloadManager;

import java.util.List;

import rx.Subscriber;
import rx.Subscription;

public class MainActivity extends AppCompatActivity {

    private DownloadAdapter downloadAdapter;
    private Subscription updateSubscription;
    private RxDownloadManager downloadManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Button download1 = (Button) findViewById(R.id.btn_download_1);
        download1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //http://releases.ubuntu.com/15.10/ubuntu-15.10-desktop-amd64.iso
                if (downloadManager != null) {
                    downloadManager.startDownload("http://releases.ubuntu.com/15.10/ubuntu-15.10-desktop-amd64.iso", "Ubuntu-15.10");
                }
            }
        });
        findViewById(R.id.btn_download_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (downloadManager != null) {
                    downloadManager.startDownload("http://ipv4.download.thinkbroadband.com/100MB.zip", "100MB file.");
                }
            }
        });

        RecyclerView downloadList = (RecyclerView) findViewById(R.id.download_list);
        downloadList.setLayoutManager(new LinearLayoutManager(this));

        downloadManager = new RxDownloadManager.Builder(this).setRefreshTimeout(10).build();
        downloadAdapter = new DownloadAdapter(downloadManager);
        downloadList.setAdapter(downloadAdapter);

    }


    @Override
    protected void onResume() {
        super.onResume();
        downloadManager.onResume();
        updateSubscription = downloadManager.getAllDownloads().subscribe(new Subscriber<List<DownloadUpdate>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(List<DownloadUpdate> downloadUpdates) {
                downloadAdapter.setDownloadsList(downloadUpdates);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        downloadManager.onPause();
        if (updateSubscription != null && !updateSubscription.isUnsubscribed()) {
            updateSubscription.unsubscribe();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_view_all) {
            Intent intent = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
