package com.matsdevelopsolutions.rxdownloadmanager;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.subjects.BehaviorSubject;
import rx.subjects.Subject;

public class RxDownloadManager {

    final DownloadManager downloadManager;
    final BroadcastReceiver downloadUpdateRecevier;

    Map<Long, BehaviorSubject<DownloadUpdate>> subjectMap = new HashMap<>();
    private RxDownloadManager(@NonNull Context context) {
        downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        downloadUpdateRecevier = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
// check if action is for download manager update
                // update map of downloadupate with values
                // call onError when error happens on download
                // call onComplete when download is completed

            }
        };
    }

    public Observable<DownloadUpdate> fetchDownloadObservalbe(long downloadId) {
        return subjectMap.get(downloadId);
    }

    /**
     * Starts download of file and returns observable for updates
     * @param url
     * @param name
     * @return
     */
    public Observable<DownloadUpdate> startDownload(String url, String name) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setTitle(name);

        long downloadId = downloadManager.enqueue(request);

        DownloadUpdate downloadUpdate = new DownloadUpdate(request, name, downloadId);

        subjectMap.put(Long.valueOf(downloadId), BehaviorSubject.<DownloadUpdate>create(downloadUpdate));

        return subjectMap.get(downloadId);
    }

    public static class Builder {
        Context context;

        private Builder(Context context) {
            this.context = context;
        }

        public static Builder withContext(Context context) {
            return new Builder(context);
        }

        public RxDownloadManager build() {
            return new RxDownloadManager(context);
        }
    }
}
