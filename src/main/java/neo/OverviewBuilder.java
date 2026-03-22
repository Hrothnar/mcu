package neo;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import neo.dto.FileState;
import neo.dto.TagState;
import neo.utility.Env;
import neo.utility.Utility;
import tools.jackson.databind.ObjectMapper;

public class OverviewBuilder {

    private ObjectMapper om = new ObjectMapper();

    private String workingDirectory = Env.getWorkingDirectory();
    private String collectedDirectory = Utility.createDirectory(workingDirectory + "/collected");
    private String overviewDirectory = Utility.createDirectory(workingDirectory + "/overview");

    public void run() {
        try {
            File music = new File(collectedDirectory);

            List<FileState> overview = overviewAudio(music);

            List<List<FileState>> partitions = Utility.splitList(overview, 32);

            for (int i = 0; i < partitions.size(); i++) {
                String path = String.format("%s%s%d.json", overviewDirectory, "/overview-", i);
                om.writeValue(new File(path), partitions.get(i));

                System.out.printf("Overview record #%d has been written down successfully\n", i);
                Thread.sleep(512);
            }
        } catch (Exception ex) {
            Utility.handleException(ex);
        }
    }

    private List<FileState> overviewAudio(File path) throws Exception {
        List<FileState> overviews = new ArrayList<>();

        File[] files = path.listFiles();

        for (File file : files) {
            if (file.isDirectory()) {
                overviewAudio(file);
            } else {
                if (Utility.isFileBroken(file)) {
                    continue;
                }

                String fileName = file.getName();

                AudioFile audio = AudioFileIO.read(file);
                Tag tag = audio.getTag();

                TagState title = new TagState(tag.getFirst(FieldKey.TITLE), "-");
                TagState artist = new TagState(tag.getFirst(FieldKey.ARTIST), "-");
                TagState album = new TagState(tag.getFirst(FieldKey.ALBUM), "-");
                TagState year = new TagState(tag.getFirst(FieldKey.YEAR), "-");
                TagState genre = new TagState(tag.getFirst(FieldKey.GENRE), "-");

                FileState overview = new FileState(fileName, title, artist, album, year, genre);

                overviews.add(overview);
            }
        }

        overviews.sort((x, y) -> x.artist.old.compareTo(y.artist.old));

        return overviews;
    }
}