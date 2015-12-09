package com.matsdevelopsolutions.rxdownloadmanager;

import android.app.DownloadManager;

public class DownloadUpdate {
    public long id;
    public String name;
    public String url;
    public int progressPercentage;

    public DownloadUpdate(DownloadManager.Request request, String name, long downloadId) {
        this.id = downloadId;
        this.name = name;
    }
}
