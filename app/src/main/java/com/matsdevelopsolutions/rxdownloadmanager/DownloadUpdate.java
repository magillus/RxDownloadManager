package com.matsdevelopsolutions.rxdownloadmanager;

import android.app.DownloadManager;
import android.database.Cursor;

public class DownloadUpdate {
    public long id;
    public String title;
    public String localUri;
    public String remoteUri;
    public String description;
    public int status = 0;
    public long size;
    public long downloadedSoFar;
    public int progressPercentage;


    public DownloadUpdate(String title, long downloadId) {
        this.id = downloadId;
        this.title = title;
        this.size = 0;
        this.downloadedSoFar = 0;
        this.progressPercentage = 0;
    }

    public static DownloadUpdate fromCursor(Cursor cursor) {
        long downloadId = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_ID));
        String description = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_DESCRIPTION));
        String localUri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
        String title = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE));
        long totalSize = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
        int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
        long downloadedSoFar = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
        String remoteUri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_URI));
        DownloadUpdate downloadUpdate = new DownloadUpdate(title, downloadId);
        downloadUpdate.localUri = localUri;
        downloadUpdate.remoteUri = remoteUri;
        downloadUpdate.status = status;
        downloadUpdate.description = description;
        downloadUpdate.size = totalSize;
        downloadUpdate.downloadedSoFar = downloadedSoFar;
        if (totalSize > 0) {
            downloadUpdate.progressPercentage = (int) (((float) downloadedSoFar) / ((float) totalSize));
        } else {
            downloadUpdate.progressPercentage = 0;
        }
        return downloadUpdate;
    }
}
