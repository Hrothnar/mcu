package neo;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

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

    private String workingDirectory = Env.getWorkingDirectory();
    private String collectedDirectory = Utility.createDirectory(workingDirectory + "/collected");
    private String brokenDirectory = Utility.createDirectory(workingDirectory + "/broken_tags");

    public void run() {
        try {
            File music = new File(collectedDirectory);

            List<FileState> report = reportFilesTags(music, 0);

            List<List<FileState>> partitions = Utility.splitList(report, 20);

            for (int i = 0; i < partitions.size(); i++) {
                String path = String.format("%s%s%d.json", brokenDirectory, "/report-", i);
                om.writeValue(new File(path), partitions.get(i));
                System.out.printf("Broken tags report #%d is successfully written down\n", i);
                Thread.sleep(512);
            }
        } catch (Exception ex) {
            Utility.handleException(ex);
        }
    }

    private List<FileState> reportFilesTags(File path, int depth) throws Exception {
        List<FileState> reports = new ArrayList<>();

        File[] files = path.listFiles();

        for (File file : files) {
            if (file.isDirectory()) {
                reportFilesTags(file, ++depth);
            } else {
                String absoluteString = file.getAbsolutePath();
                Path absolutePath = Path.of(absoluteString);
                String fileName = file.getName();
                String location = absoluteString.replace(fileName, "");

                if (!Utility.isFileLegit(file)) {
                    System.out.printf("The file %s is not a proper audio file, skip.\n", fileName);
                    continue;
                }

                AudioFile audio = AudioFileIO.read(file);
                AudioHeader header = audio.getAudioHeader();
                Tag tag = audio.getTag();

                FieldState title = Validator.validateText(tag.getFirst(FieldKey.TITLE));
                FieldState artist = Validator.validateText(tag.getFirst(FieldKey.ARTIST));
                FieldState album = Validator.validateText(tag.getFirst(FieldKey.ALBUM));
                FieldState year = Validator.validateYear(tag.getFirst(FieldKey.YEAR));
                FieldState albumCover = Validator
                        .validateText(tag instanceof FlacTag ? "NOT_EMPTY" : tag.getFirst(FieldKey.COVER_ART));
                FieldState genre = Validator.validateText(tag.getFirst(FieldKey.GENRE));
                FieldState bitrate = Validator.validateBitrate(header.getBitRateAsNumber());

                FileState fileState = new FileState(fileName, location, title, artist, album, year, bitrate, albumCover,
                        genre);

                if (fileState.breakages > 0) {
                    reports.add(fileState);
                }
            }
        }

        reports.sort((x, y) -> y.breakages - x.breakages);

        return reports;
    }
}