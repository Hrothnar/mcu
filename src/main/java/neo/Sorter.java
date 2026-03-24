package neo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
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

public class Sorter {

    private String workingDirectory = Env.getWorkingDirectory();

    private String collectedPath = "/collected";
    private String artistsPath = "/artists";
    private String otherArtistsPath = "/artists_others";

    private String collectedDirectory = Utility.createDirectory(workingDirectory + collectedPath);
    private String artistsDirectory = Utility.createDirectory(workingDirectory + artistsPath);
    private String otherArtistsDirectory = Utility.createDirectory(workingDirectory + otherArtistsPath);

    private int bigPresenceThreshold = 10;

    public void run() {
        try {
            File collected = new File(collectedDirectory);

            Map<String, List<String>> artistMap = buildMap(collected);

            sort(artistMap);
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

                String artist = extractFirstArtist(tag);

                List<String> songs = collection.getOrDefault(artist, new ArrayList<>());
                songs.add(absoluteString);
                collection.put(artist, songs);
            }
        }

        return collection;
    }

    private void sort(Map<String, List<String>> artists) {
        try {
            for (String one : artists.keySet()) {
                List<String> songs = artists.get(one);

                for (String absolutePath : songs) {
                    String fileName = absolutePath.split(collectedPath)[1];

                    String copyTo = songs.size() >= bigPresenceThreshold
                            ? artistsDirectory + "/" + one
                            : otherArtistsDirectory;

                    Utility.createDirectory(copyTo.toString());
                    Files.copy(Path.of(absolutePath), Path.of(copyTo + fileName), StandardCopyOption.REPLACE_EXISTING);
                }
            }
        } catch (IOException ex) {
            Utility.handleException(ex);
        }
    }

    private String extractFirstArtist(Tag tag) {
        String artist = tag.getFirst(FieldKey.ARTIST);

        String[] artistsChain1 = artist.split(",");
        String[] artistsChain2 = artistsChain1[0].split("&");
        String[] artistsChain3 = artistsChain2[0].split("\\|");
        String[] artistsChain4 = artistsChain3[0].split("/");

        return artistsChain4[0];
    }
}