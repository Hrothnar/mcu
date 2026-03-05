package neo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import neo.dto.FieldState;
import neo.dto.FileState;
import neo.utility.Env;
import neo.utility.Validator;
import tools.jackson.databind.ObjectMapper;

public class BrokenTagsFinder {

    private ObjectMapper om = new ObjectMapper();
    private List<FileState> reports = new ArrayList<>();
    private Set<String> supportedExtensions = Set.of(Env.get("MUSIC_EXTENSIONS").replaceAll("\s", "").split(","));

    public void run() {
        String root = Env.get("ROOT_MUSIC_DIRECTORY");

        try {
            readAndReportFilesTags(new File(root), (byte) 0);
            om.writeValue(new File(Env.get("OUTPUTS_DIRECTORY") + "/tags.json"), reports);
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
                    FieldState year = Validator.validateText(tag.getFirst(FieldKey.YEAR));
                    FieldState albumCover = Validator.validateText(tag.getFirst(FieldKey.COVER_ART));
                    FieldState genre = Validator.validateText(tag.getFirst(FieldKey.GENRE));
                    FieldState bitrate = Validator.validateBitrate(header.getBitRateAsNumber());

                    FileState fileState = new FileState(fileName, location, title, artist, album, year, bitrate, albumCover, genre);
                    
                    if (fileState.breakages > 0) {
                        reports.add(fileState);
                    }
                    
                    // Thread.sleep(2000);
                }
            }
        }

        reports.sort((x, y) -> y.breakages - x.breakages);
    }
}
