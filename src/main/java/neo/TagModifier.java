package neo;

import java.io.File;
import java.util.Arrays;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import neo.dto.FileState;
import neo.utility.Env;
import tools.jackson.databind.ObjectMapper;

public class TagModifier {

    private ObjectMapper om = new ObjectMapper();
    private String brokenDirectory = Env.brokenDirectory();
    private String modifiedDirectory = Env.modifiedDirectory();

    public void run() {
        File[] tagsFiles = new File(modifiedDirectory).listFiles();

        for (int i = 0; i < tagsFiles.length; i++) {
            FileState[] music = om.readValue(tagsFiles[i], FileState[].class);

            for (int j = 0; j < music.length; j++) {
                FileState fileState = music[j];

                try {
                    File audioFile = new File(brokenDirectory + "/" + fileState.name);
                    if (audioFile != null) {
                        AudioFile audio = AudioFileIO.read(audioFile);
                        Tag tag = audio.getTag();

                        if (fileState.title.modified != null)
                            tag.setField(FieldKey.TITLE, fileState.title.modified);
                        if (fileState.artist.modified != null)
                            tag.setField(FieldKey.ARTIST, fileState.artist.modified);
                        if (fileState.album.modified != null)
                            tag.setField(FieldKey.ALBUM, fileState.album.modified);
                        if (fileState.year.modified != null)
                            tag.setField(FieldKey.YEAR, fileState.year.modified);
                        if (fileState.genre.modified != null)
                            tag.setField(FieldKey.GENRE, fileState.genre.modified);

                        audio.commit();
                    }
                } catch (Exception ex) {
                    System.out.println("There was and error, the program is terminating");
                    ex.printStackTrace();
                    System.exit(1);
                }
            }
        }
    }
}