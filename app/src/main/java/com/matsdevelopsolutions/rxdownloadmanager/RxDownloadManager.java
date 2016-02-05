package com.matsdevelopsolutions.rxdownloadmanager;

import android.app.DownloadManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.BehaviorSubject;

public class RxDownloadManager {

    private static final String SHARED_PREFERENCES_KEY = "DOWNLOAD_MANAGER_SHARED_PREF";
    private static final String DOWNLOAD_IDS_KEY = "DOWNLOAD_IDS_KEY";

    private PendingIntent downloadClickedIntent;
    private Context context;

    private final DownloadManager downloadManager;
    private final BroadcastReceiver downloadClickReceiver;
    private final SharedPreferences preferences;
    private final BroadcastReceiver downloadCompleteReceiver;
    private final BehaviorSubject<List<DownloadUpdate>> allDownloadsSubject = BehaviorSubject.create();

    /**
     * Map that stores active subjects of download that were "requested to track".
     */
    private Map<Long, DownloadUpdateContainer> downloadSubjectsMap = new HashMap<>();
    private Subscription updateSubscription;

    public class DownloadUpdateContainer {
        public DownloadUpdateContainer(Long id, BehaviorSubject<DownloadUpdate> subject) {
            this.subject = subject;
            this.id = id;
        }

        public BehaviorSubject<DownloadUpdate> subject;
        public Long id;
    }

    private DownloadUpdate createDownloadCompletedUpdate(long downloadId) {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadId);
        Cursor cursor = downloadManager.query(query);
        return DownloadUpdate.fromCursor(cursor);
    }

    private RxDownloadManager(@NonNull Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);
        downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        downloadCompleteReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (downloadId >= 0) {
                    DownloadUpdateContainer download = downloadSubjectsMap.get(downloadId);
                    if (download == null) {
                        download = new DownloadUpdateContainer(downloadId, BehaviorSubject.create(createDownloadCompletedUpdate(downloadId)));
                        // todo create completed file status
                    }
                    // todo refresh state of downloadable
                    updateDownloadDetails(download);
                    download.subject.onCompleted();// it completed
                }
            }
        };
        downloadClickReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                try {
                    downloadClickedIntent.send();
                } catch (PendingIntent.CanceledException e) {
                    e.printStackTrace();
                }
                // execute action

                // check if action is for download manager update
                // update map of downloadupate with values
                // call onError when error happens on download
                // call onComplete when download is completed
            }
        };

        // refresh receiver
        startUpdate();
    }

    public void onResume() {
        context.registerReceiver(downloadCompleteReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        context.registerReceiver(downloadClickReceiver, new IntentFilter(DownloadManager.ACTION_NOTIFICATION_CLICKED));
    }

    public void onPause() {
        try {
            context.unregisterReceiver(downloadCompleteReceiver);
            context.unregisterReceiver(downloadClickReceiver);
        } catch (IllegalArgumentException iae) {

        }
    }

    public void stop() {
        stopUpdate();
    }

    Observable<Long> updateIntervalObservable;

    private void stopUpdate() {
        if (updateSubscription != null && !updateSubscription.isUnsubscribed()) {
            updateSubscription.unsubscribe();
        }
    }

    private void startUpdate() {
        if (updateIntervalObservable == null) {
            updateIntervalObservable = Observable.interval(60, TimeUnit.SECONDS);
            updateSubscription = updateIntervalObservable.observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<Long>() {
                        @Override
                        public void onCompleted() {
                            updateSubscription.unsubscribe();
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(Long aLong) {
                            updateQuery();
                        }
                    });
        }
        // first start
        updateQuery();
    }

    private void updateDownloadDetails(DownloadUpdateContainer download) {
        //todo update only one value
        List<Long> idList = new ArrayList<>();
        idList.add(download.id);
        updateObservablesByIdList(idList);
    }

    private void updateQuery() {
        final List<Long> currentDownloadIds = new ArrayList<>(downloadSubjectsMap.keySet());
        // add downloads no active downloads get from preferences
        Set<String> storedDownloadIds = preferences.getStringSet(DOWNLOAD_IDS_KEY, Collections.<String>emptySet());

        for (String stringDownloadId : storedDownloadIds) {
            Long id = Long.valueOf(stringDownloadId);
            if (!currentDownloadIds.contains(id)) {
                currentDownloadIds.add(id);
            }
        }
        if (currentDownloadIds.size() > 0) {
            updateObservablesByIdList(currentDownloadIds);
        }
    }

    private void updateObservablesByIdList(final List<Long> currentDownloadIds) {
        DownloadManager.Query query = new DownloadManager.Query();
        Iterator<Long> downloadIdIterator = currentDownloadIds.iterator();
        long[] idsArray = new long[currentDownloadIds.size()];
        int i = 0;
        while (downloadIdIterator.hasNext()) {
            idsArray[i++] = downloadIdIterator.next();
        }
        query.setFilterById(idsArray);

        Cursor cursor = downloadManager.query(query);
        if (cursor != null) {
            allDownloadsSubject.onNext(updateDownloadDetails(cursor));
        }
    }

    private List<DownloadUpdate> updateDownloadDetails(Cursor cursor) {
        List<DownloadUpdate> updatesList = new ArrayList<>();
        DownloadUpdate downloadUpdate;
        while (cursor.moveToNext()) {
            downloadUpdate = DownloadUpdate.fromCursor(cursor);
            if (downloadUpdate != null) {
                updatesList.add(downloadUpdate);
                // update
                if (downloadSubjectsMap.get(downloadUpdate.id) != null) {
                    downloadSubjectsMap.get(downloadUpdate.id).subject.onNext(downloadUpdate);
                } else {
                    downloadSubjectsMap.put(downloadUpdate.id, new DownloadUpdateContainer(downloadUpdate.id, BehaviorSubject.create(downloadUpdate)));
                }
            }
        }
        return updatesList;
    }

    public Observable<DownloadUpdate> pauseDownload(long downloadId) {
        return null;
    }

    public Observable<DownloadUpdate> resumeDownload(long downloadId) {
        return null;
    }

    public Observable<DownloadUpdate> cancelDownload(long downloadId) {
        return null;
    }

    public Observable<DownloadUpdate> fetchDownloadObservalbe(long downloadId) {
        return downloadSubjectsMap.get(downloadId).subject.asObservable();
    }

    /**
     * Starts download of file and returns observable for updates
     *
     * @param url
     * @param name
     * @return
     */
    public Observable<DownloadUpdate> startDownload(String url, String name) {

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setTitle(name);

        long downloadId = downloadManager.enqueue(request);
        addDownloadToPreferences(downloadId);
        DownloadUpdate downloadUpdate = new DownloadUpdate(name, downloadId);
        downloadUpdate.remoteUri = url;

        downloadSubjectsMap.put(Long.valueOf(downloadId), new DownloadUpdateContainer(downloadId, BehaviorSubject.create(downloadUpdate)));
        updateQuery();
        return downloadSubjectsMap.get(downloadId).subject.asObservable();
    }

    private void addDownloadToPreferences(long downloadId) {
        Set<String> currentIds = preferences.getStringSet(DOWNLOAD_IDS_KEY, new HashSet<String>());
        currentIds.add(String.valueOf(downloadId));
        preferences.edit().putStringSet(DOWNLOAD_IDS_KEY, currentIds).apply();
    }

    public Observable<List<DownloadUpdate>> getAllDownloads() {
        return allDownloadsSubject.asObservable();
    }

    public static class Builder {
        Context context;
        PendingIntent downloadClickedIntent;

        public Builder(Context context) {
            this.context = context;
        }

        public static Builder withContext(Context context) {
            return new Builder(context);
        }

        public Builder setOnDownloadClickIntent(PendingIntent onDownloadClickIntent) {
            downloadClickedIntent = onDownloadClickIntent;
            return this;
        }

        public RxDownloadManager build() {
            return new RxDownloadManager(context);
        }
    }
}
