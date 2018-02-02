package imagedownloader.project.nhp.qualityimagedownloader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import imagedownloader.project.nhp.qualityimagedownloader.adapter.UrlAdapter;
import imagedownloader.project.nhp.qualityimagedownloader.model.Url;

public class MainActivity extends AppCompatActivity implements UrlAdapter.DownloadListener {
    private static final String UPDATE_PROGRESS = "update-progress";
    private String TAG = MainActivity.class.getCanonicalName();

    private RecyclerView rvImageUrl;
    private final List<Url> urls = new ArrayList<>();
    private UrlAdapter urlAdapter;

    private HandlerThread downloadHandlerThread;
    private Handler downloadHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupView();
        setData();
        setupAdapter();
        setupEvent();
    }

    private void setupAdapter() {
        urlAdapter = new UrlAdapter(this, urls);
        urlAdapter.setListener(this);
        rvImageUrl.setAdapter(urlAdapter);
    }

    private void setData() {
        urls.add(new Url("https://pixabay.com/get/ea35b8082cf5083ecd1f420ce6454296e46ae3d018b6144792f0c27d/tree-3097419.jpg", 0));
        urls.add(new Url("https://pixabay.com/get/ea35b8082cf5083ecd1f420ce6454296e46ae3d018b6144792f0c27d/tree-3097419.jpg", 0));
        urls.add(new Url("https://pixabay.com/get/ea35b8082cf5083ecd1f420ce6454296e46ae3d018b6144792f0c27d/tree-3097419.jpg", 0));
        urls.add(new Url("https://pixabay.com/get/ea35b8082cf5083ecd1f420ce6454296e46ae3d018b6144792f0c27d/tree-3097419.jpg", 0));
        urls.add(new Url("https://pixabay.com/get/ea35b8082cf5083ecd1f420ce6454296e46ae3d018b6144792f0c27d/tree-3097419.jpg", 0));
    }

    private void setupEvent() {
        BroadcastReceiver progressUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int position = intent.getIntExtra("position", 0);
                int progress = intent.getIntExtra("progress", 0);
                Url url = urls.get(position);
                url.setProgress(progress);
                urlAdapter.notifyItemChanged(position);
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(progressUpdateReceiver, new IntentFilter(UPDATE_PROGRESS));
    }

    private void setupView() {
        rvImageUrl = findViewById(R.id.rv_image_url);
        rvImageUrl.setLayoutManager(new LinearLayoutManager(this));
        rvImageUrl.setHasFixedSize(true);
    }

    @Override
    public void onClickDownload(final int position) {
        ensureHandlerThreadReady();
        ensureHandlerReady();
        sendMessage(position);
    }

    private void sendMessage(int position) {
        Message message = downloadHandler.obtainMessage();
        message.arg1 = position;
        downloadHandler.sendMessage(message);
    }

    private void ensureHandlerReady() {
        if (downloadHandler == null) {
            downloadHandler = new Handler(downloadHandlerThread.getLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    downloadImage(msg.arg1);
                }
            };
        }
    }

    private void ensureHandlerThreadReady() {
        if (downloadHandlerThread == null) {
            downloadHandlerThread = new HandlerThread("download-handler-thread");
            downloadHandlerThread.start();
        }
    }

    private Bitmap downloadImage(int position) {
        String url = urls.get(position).getUrl();
        int count;
        try {
            URL urlFactory = new URL(url);
            URLConnection connection = urlFactory.openConnection();
            connection.connect();
            InputStream inputStream = connection.getInputStream();

            int lenghtOfFile = connection.getContentLength();
            long total = 0;
            byte data[] = new byte[1024];
            while ((count = inputStream.read(data)) != -1) {
                total += count;
                // publishing the progress....
                // After this onProgressUpdate will be called
                int progress = (int) ((total * 100) / lenghtOfFile);
                Log.d(TAG, "downloadImage: " + progress);
                sendProgress(position, progress);
            }

            Bitmap result = BitmapFactory.decodeStream(inputStream);
            Log.d(TAG, "downloadImage: " + result.toString());
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void sendProgress(int position, int progress) {
        Intent intent = new Intent(UPDATE_PROGRESS);
        intent.putExtra("position", position);
        intent.putExtra("progress", progress);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
