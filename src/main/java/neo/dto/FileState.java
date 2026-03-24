package neo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(Include.NON_NULL)
@JsonPropertyOrder(value = { "name", "title", "artist", "album", "year", "genre" })
public class FileState {

    public String name = null;
    public TagState title = null;
    public TagState artist = null;
    public TagState album = null;
    public TagState year = null;
    public TagState genre = null;

    public FileState() {
        
    }

    public FileState(String name, TagState title, TagState artist, TagState album, TagState year, TagState genre) {
        this.name = name;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.year = year;
        this.genre = genre;
    }

    @Override
    public String toString() {
        return "FileState [name=" + name + ", title=" + title + ", artist=" + artist + ", album=" + album + ", year="
                + year + ", genre=" + genre + "]";
    }
}