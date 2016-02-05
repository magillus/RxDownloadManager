package com.matsdevelopsolutions.txdownloadmanagersample;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.matsdevelopsolutions.rxdownloadmanager.DownloadUpdate;

import java.util.ArrayList;
import java.util.List;

/**
 * Downloads adapter.
 */
public class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.ViewHolder> {

    private final List<DownloadUpdate> downloadsList = new ArrayList<>();

    public void setDownloadsList(List<DownloadUpdate> downloadsList) {
        this.downloadsList.clear();
        this.downloadsList.addAll(downloadsList);
        notifyDataSetChanged();
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

        public void bind(DownloadUpdate downloadUpdate) {
            title.setText(String.format(itemView.getResources().getString(R.string.download_status),
                    downloadUpdate.title,
                    downloadUpdate.progressPercentage,
                    downloadUpdate.getStatus()));
        }
    }
}
