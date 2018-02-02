package imagedownloader.project.nhp.qualityimagedownloader.model;

public class Url {
    private String url;
    private int progress;

    public Url() {
    }

    public Url(String url, int progress) {
        this.url = url;
        this.progress = progress;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
