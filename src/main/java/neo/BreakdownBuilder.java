package neo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import neo.utility.Env;
import neo.utility.Utility;
import tools.jackson.databind.ObjectMapper;

public class BreakdownBuilder {

    private ObjectMapper om = new ObjectMapper();

    private String musicDirectory = Env.getMusicDirectory();
    private String workingDirectory = Env.getWorkingDirectory();
    private String breakdownDirectory = Utility.createDirectory(workingDirectory + "/breakdown");

    public void run() {
        try {
            File music = new File(musicDirectory);

            Map<String, List<String>> breakdownMap = buildMap(music);
            Map<String, Integer> count = new HashMap<>();

            for (String genre : breakdownMap.keySet()) {
                List<String> compositions = breakdownMap.get(genre);
                count.put(genre, compositions.size());
            }

            om.writeValue(new File(breakdownDirectory + "/breakdown.json"), count);

            System.out.printf("Genre breakdown record has been written down successfully\n");
        } catch (Exception ex) {
            Utility.handleException(ex);
        }
    }

    private Map<String, List<String>> buildMap(File path) throws Exception {
        HashMap<String, List<String>> collection = new HashMap<>();

        File[] files = path.listFiles();

        for (File file : files) {
            if (file.isDirectory()) {
                buildMap(file);
            } else {
                String absoluteString = file.getAbsolutePath();

                if (Utility.isFileBroken(file)) {
                    continue;
                }

                AudioFile audio = AudioFileIO.read(file);
                Tag tag = audio.getTag();
                String artists = tag.getFirst(FieldKey.GENRE);

                String artist = Utility.extractFirstEntry(artists);

                List<String> songs = collection.getOrDefault(artist, new ArrayList<>());
                songs.add(absoluteString);
                collection.put(artist, songs);
            }
        }

        return collection;
    }
}