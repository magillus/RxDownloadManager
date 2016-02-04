package com.matsdevelopsolutions.txdownloadmanagersample;

import android.app.DownloadManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.matsdevelopsolutions.rxdownloadmanager.DownloadUpdate;
import com.matsdevelopsolutions.rxdownloadmanager.RxDownloadManager;

import rx.Observable;
import rx.functions.Action1;

public class MainActivity extends AppCompatActivity {

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
                if (downloadManager!=null) {
                    downloadObservable = downloadManager.startDownload("http://releases.ubuntu.com/15.10/ubuntu-15.10-desktop-amd64.iso", "Ubuntu-15.10");
                    downloadObservable.subscribe(new Action1<DownloadUpdate>() {
                        @Override
                        public void call(DownloadUpdate downloadUpdate) {
                            displayDownloadUpdate(downloadUpdate, (TextView) findViewById(R.id.download_1_update));
                        }
                    });
                }
            }
        });

    }

    private void displayDownloadUpdate(DownloadUpdate downloadUpdate, TextView textView) {
        if (textView!=null) {
            textView.setText(String.format(getString(R.string.download_status),
                    downloadUpdate.title,
                    downloadUpdate.progressPercentage,
                    downloadUpdate.status));
        }
    }

    private Observable<DownloadUpdate> downloadObservable;
    private RxDownloadManager downloadManager;

    @Override
    protected void onResume() {
        super.onResume();
        downloadManager = new RxDownloadManager.Builder(this).build();
        downloadManager.listDownloads();
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
