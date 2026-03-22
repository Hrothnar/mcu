package neo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import neo.dto.TagState;
import neo.dto.FileReport;
import neo.utility.Env;
import neo.utility.Utility;
import neo.utility.Validator;
import tools.jackson.databind.ObjectMapper;

public class ReportBuilder {

    private ObjectMapper om = new ObjectMapper();

    private String workingDirectory = Env.getWorkingDirectory();
    private String collectedDirectory = Utility.createDirectory(workingDirectory + "/collected");
    private String brokenTagsDirectory = Utility.createDirectory(workingDirectory + "/broken_tags");

    public void run() {
        try {
            File music = new File(collectedDirectory);

            List<FileReport> report = reportAudio(music);

            List<List<FileReport>> partitions = Utility.splitList(report, 20);

            for (int i = 0; i < partitions.size(); i++) {
                String path = String.format("%s%s%d.json", brokenTagsDirectory, "/report-", i);
                om.writeValue(new File(path), partitions.get(i));

                System.out.printf("Report #%d has been written down successfully\n", i);
                Thread.sleep(512);
            }
        } catch (Exception ex) {
            Utility.handleException(ex);
        }
    }

    private List<FileReport> reportAudio(File path) throws Exception {
        List<FileReport> reports = new ArrayList<>();

        File[] files = path.listFiles();

        for (File file : files) {
            if (file.isDirectory()) {
                reportAudio(file);
            } else {
                if (Utility.isFileBroken(file)) {
                    continue;
                }

                String fileName = file.getName();

                AudioFile audio = AudioFileIO.read(file);
                AudioHeader header = audio.getAudioHeader();
                Tag tag = audio.getTag();

                TagState title = Validator.validateText(tag.getFirst(FieldKey.TITLE));
                TagState artist = Validator.validateText(tag.getFirst(FieldKey.ARTIST));
                TagState album = Validator.validateText(tag.getFirst(FieldKey.ALBUM));
                TagState year = Validator.validateYear(tag.getFirst(FieldKey.YEAR));
                TagState genre = Validator.validateText(tag.getFirst(FieldKey.GENRE));
                TagState bitrate = Validator.validateBitrate(header.getBitRateAsNumber());
                TagState cover = Validator.validateText(tag.getFirst(FieldKey.COVER_ART));

                FileReport report = new FileReport(fileName, title, artist, album, year, genre, bitrate, cover);

                if (report.breakages > 0) {
                    reports.add(report);
                }
            }
        }

        reports.sort((x, y) -> y.breakages - x.breakages);

        return reports;
    }
}