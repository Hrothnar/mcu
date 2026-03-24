package neo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import neo.enums.Status;

@JsonInclude(Include.NON_NULL)
public class FileReport extends FileState {

    public TagState bitrate = null;
    public TagState cover = null;
    public int breakages = 0;

    public FileReport() {
        
    }

    public FileReport(String name, TagState title, TagState artist, TagState album, TagState year, TagState genre, TagState bitrate, TagState cover) {
        super(name,title, artist, album, year, genre);
        this.bitrate = bitrate;
        this.cover = cover;

        if (isBroken(title)) breakages++;
        if (isBroken(artist)) breakages++;
        if (isBroken(album)) breakages++;
        if (isBroken(year)) breakages++;
        if (isBroken(genre)) breakages++;
        if (isBroken(bitrate)) breakages++;
        if (isBroken(cover)) breakages++;
    }

    private static boolean isBroken(TagState tag) {
        return tag.sts != null && (tag.sts.equals(Status.EMPTY.toString()) || tag.sts.equals(Status.BROKEN.toString()));
    }

    @Override
    public String toString() {
        return "FileReport [bitrate=" + bitrate + ", cover=" + cover + ", breakages=" + breakages + ", name=" + name
                + ", title=" + title + ", artist=" + artist + ", album=" + album + ", year=" + year + ", genre=" + genre
                + "]";
    }
}