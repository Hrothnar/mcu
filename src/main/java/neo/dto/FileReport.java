package neo.dto;

import java.util.regex.Pattern;

public class FileReport {

    public String name = null;
    public String location = null;
    public String title = null;
    public String artist = null;
    public String album = null;
    public String year = null;
    public String bitrate = null;
    public String albumCover = null;

    public FileReport(
            String name,
            String location,
            String title,
            String artist,
            String album,
            String year,
            String bitrate,
            String albumCover) {
        this.name = name;
        this.location = location;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.year = year;
        this.bitrate = bitrate;
        this.albumCover = albumCover;
    }


    @Override
    public String toString() {
        return "FileReport [name=" + name + ", location=" + location + ", title=" + title + ", artist=" + artist
                + ", album=" + album + ", year=" + year + ", bitrate=" + bitrate + ", albumCover=" + albumCover + "]";
    }

}