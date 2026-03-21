package neo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@JsonPropertyOrder(value = {"name", "location", "breakages"})
public class FileState {

    public String name = null;
    public String location = null;
    public FieldState title = null;
    public FieldState artist = null;
    public FieldState album = null;
    public FieldState year = null;
    public FieldState bitrate = null;
    public FieldState albumCover = null;
    public FieldState genre = null;
    public int breakages = 0;

    public FileState() {

    }

    public FileState(String name, String location, FieldState title, FieldState artist, FieldState album,
            FieldState year, FieldState bitrate, FieldState albumCover, FieldState genre) {
        this.name = name;
        this.location = location;
        this.title = title;
        if (title.status != null) breakages++;
        this.artist = artist;
        if (artist.status != null) breakages++;
        this.album = album;
        if (album.status != null) breakages++;
        this.year = year;
        if (year.status != null) breakages++;
        this.bitrate = bitrate;
        if (bitrate.status != null) breakages++;
        this.albumCover = albumCover;
        if (albumCover.status != null) breakages++;
        this.genre = genre;
        if (genre.status != null) breakages++;
    }

    @Override
    public String toString() {
        return "FileState [name=" + name + ", location=" + location + ", title=" + title + ", artist=" + artist
                + ", album=" + album + ", year=" + year + ", bitrate=" + bitrate + ", albumCover=" + albumCover + "]";
    }
}