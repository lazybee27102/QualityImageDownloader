package imagedownloader.project.nhp.qualityimagedownloader.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import imagedownloader.project.nhp.qualityimagedownloader.R;
import imagedownloader.project.nhp.qualityimagedownloader.model.Url;

public class UrlAdapter extends RecyclerView.Adapter<UrlAdapter.UrlHolder> {
    private Context context;
    private List<Url> urls;
    private DownloadListener listener;

    public void setListener(DownloadListener listener) {
        this.listener = listener;
    }

    public interface DownloadListener {
        void onClickDownload(int position);
    }

    public UrlAdapter(Context context, List<Url> urls) {
        this.context = context;
        this.urls = urls;
    }

    @Override
    public UrlHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View urlView = LayoutInflater.from(context).inflate(R.layout.item_url, parent, false);
        return new UrlHolder(urlView);
    }

    @Override
    public void onBindViewHolder(UrlHolder holder, int position) {
        Url url = urls.get(position);
        holder.tvUrl.setText(url.getUrl());
        if (url.getProgress() > 0) {
            holder.btnDownload.setText(url.getProgress() + " %");
        } else {
            holder.btnDownload.setText("Download");
        }

        setupEvent(holder, position);
    }

    private void setupEvent(final UrlHolder holder, final int position) {
        holder.btnDownload.setOnClickListener(null);
        holder.btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClickDownload(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return urls.size();
    }

    static class UrlHolder extends RecyclerView.ViewHolder {
        private TextView tvUrl;
        private Button btnDownload;

        UrlHolder(View itemView) {
            super(itemView);
            tvUrl = itemView.findViewById(R.id.tv_url);
            btnDownload = itemView.findViewById(R.id.btn_download);
        }
    }
}
