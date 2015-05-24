package istata.domain;

public class StataDoFile {

    private String path;
    private String content;
    private long timestamp;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long l) {
        this.timestamp = l;
    }

}
