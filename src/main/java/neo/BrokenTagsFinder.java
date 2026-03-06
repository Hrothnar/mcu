package neo;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.flac.FlacTag;

import neo.dto.FieldState;
import neo.dto.FileState;
import neo.utility.Env;
import neo.utility.Utility;
import neo.utility.Validator;
import tools.jackson.databind.ObjectMapper;

public class BrokenTagsFinder {

    private ObjectMapper om = new ObjectMapper();
    private List<FileState> reports = new ArrayList<>();
    private Set<String> supportedExtensions = Env.supportedExtensions();
    private String brokenDirectory = Env.brokenDirectory();
    private String tagsDirectory = Env.tagsDirectory();
    private String rootDirectory = Env.rootMusicDirectory();

    public void run() {
        try {
            readAndReportFilesTags(new File(rootDirectory), (byte) 0);
            reports.sort((x, y) -> y.breakages - x.breakages);
            List<List<FileState>> partitions = Utility.splitList(reports, 32);

            for (int i = 0; i < partitions.size(); i++) {
                String path = String.format("%s%s%d.json", tagsDirectory, "/tags-", i);
                om.writeValue(new File(path), partitions.get(i));
                Thread.sleep(512);
            }
        } catch (Exception ex) {
            System.out.println("There was and error, the program is terminating");
            ex.printStackTrace();
            System.exit(1);
        }
    }

    private void readAndReportFilesTags(File directory, byte depth) throws Exception {
        File[] files = directory.listFiles();

        for (File file : files) {
            if (file.isDirectory()) {
                readAndReportFilesTags(file, ++depth);
            } else {
                String fileName = file.getName();
                String location = file.getAbsolutePath().replace(fileName, "");

                String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);

                if (supportedExtensions.contains(fileExtension)) {
                    AudioFile audio = AudioFileIO.read(file);
                    AudioHeader header = audio.getAudioHeader();
                    Tag tag = audio.getTag();

                    if (tag == null) {
                        continue;
                    }

                    FieldState title = Validator.validateText(tag.getFirst(FieldKey.TITLE));
                    FieldState artist = Validator.validateText(tag.getFirst(FieldKey.ARTIST));
                    FieldState album = Validator.validateText(tag.getFirst(FieldKey.ALBUM));
                    FieldState year = Validator.validateYear(tag.getFirst(FieldKey.YEAR));
                    FieldState albumCover = Validator.validateText(tag instanceof FlacTag ? "NOT_EMPTY" : tag.getFirst(FieldKey.COVER_ART));
                    FieldState genre = Validator.validateText(tag.getFirst(FieldKey.GENRE));
                    FieldState bitrate = Validator.validateBitrate(header.getBitRateAsNumber());

                    FileState fileState = new FileState(fileName, location, title, artist, album, year, bitrate,
                            albumCover, genre);

                    if (fileState.breakages > 0) {
                        if (!Files.exists(Path.of(brokenDirectory + "/" + fileName))) {
                            Files.copy(Path.of(file.getAbsolutePath()), Path.of(brokenDirectory + "/" + fileName));
                        }

                        reports.add(fileState);
                    }
                }
            }
        }
    }
}