package nguyen.zylin.hippoplayer.models;

public class Song {
    private long id;
    private String title;
    private String artist;
    private String album;
    private String length;
    private String url;

    public Song(long id, String title, String artist, String album, String length, String url) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.length = length;
        this.url = url;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
