package com.matsdevelopsolutions.txdownloadmanagersample;

import android.app.DownloadManager;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.matsdevelopsolutions.rxdownloadmanager.DownloadUpdate;
import com.matsdevelopsolutions.rxdownloadmanager.RxDownloadManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Downloads adapter.
 */
public class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.ViewHolder> {

    private static final String TAG = "DownloadAdapter";
    private final List<DownloadUpdate> downloadsList = new ArrayList<>();
    private RxDownloadManager downloadManager;

    public void setDownloadsList(List<DownloadUpdate> downloadsList) {
        this.downloadsList.clear();
        this.downloadsList.addAll(downloadsList);
        notifyDataSetChanged();
    }

    public DownloadAdapter(RxDownloadManager downloadManager) {
        this.downloadManager = downloadManager;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(downloadsList.get(position));
    }

    @Override
    public int getItemCount() {
        return downloadsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView title;

        public ViewHolder(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.download_item, parent, false));
            title = (TextView) itemView.findViewById(R.id.title);
        }

        public void bind(final DownloadUpdate downloadUpdate) {
            title.setText(String.format(itemView.getResources().getString(R.string.download_status),
                    downloadUpdate.title,
                    downloadUpdate.progressPercentage,
                    downloadUpdate.getStatus()));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {

                    if (downloadUpdate.status == DownloadManager.STATUS_SUCCESSFUL) {
                        // open file
                        try {
                            downloadManager.loadFile(downloadUpdate.id);
                        } catch (IOException e) {
                            Log.w(TAG, "Error opening downloaded file");
                        }
                    } else {

                        new AlertDialog.Builder(v.getContext())
                                .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        downloadManager.cancelDownload(downloadUpdate.id);
                                    }
                                })
                                .setNegativeButton("Continue Download", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //nothing
                                    }
                                })
                                .setTitle("Cancel Download?")
                                .show();
                    }

                }
            });
        }
    }
}
