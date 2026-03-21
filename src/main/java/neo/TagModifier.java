package neo;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import neo.dto.FileState;
import neo.utility.Env;
import neo.utility.Utility;
import tools.jackson.databind.ObjectMapper;

public class TagModifier {

    private ObjectMapper om = new ObjectMapper();

    private String workingDirectory = Env.getWorkingDirectory();
    private String collectedDirectory = Utility.createDirectory(workingDirectory + "/collected");
    private String newTagsDirectory = Utility.createDirectory(workingDirectory + "/new_tags");

    public void run() {
        try {
            File newTags = new File(newTagsDirectory);
            File collected = new File(collectedDirectory);

            modifyTags(newTags);
            renameFiles(collected);
        } catch (Exception ex) {
            Utility.handleException(ex);
        }
    }

    private void modifyTags(File path) {
        File[] tagFiles = path.listFiles();

        for (int i = 0; i < tagFiles.length; i++) {
            FileState[] properTags = om.readValue(tagFiles[i], FileState[].class);

            for (int j = 0; j < properTags.length; j++) {
                FileState fileState = properTags[j];

                try {
                    File file = new File(fileState.location + "/" + fileState.name);

                    if (file == null || !Utility.isFileLegit(file)) {
                        System.out.printf("The file %s is not a proper audio file or it does not exist, skip.\n",
                                file.getName());
                        continue;
                    }

                    AudioFile audio = AudioFileIO.read(file);
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
                } catch (Exception ex) {
                    Utility.handleException(ex);
                }
            }
        }
    }

    private void renameFiles(File path) throws Exception {
        File[] files = path.listFiles();

        for (File file : files) {
            if (file.isDirectory()) {
                renameFiles(file);
            } else {
                if (Utility.isFileLegit(file)) {
                    AudioFile audio = AudioFileIO.read(file);
                    Tag tag = audio.getTag();

                    renameFile(file, tag);
                }
            }
        }
    }

    private void renameFile(File file, Tag tag) {
        String absoluteString = file.getAbsolutePath();
        String fileName = file.getName();
        String location = absoluteString.replace(fileName, "");
        String fileExtension = file.getName().substring(file.getName().lastIndexOf(".") + 1);

        String artist = tag.getFirst(FieldKey.ARTIST);
        String title = tag.getFirst(FieldKey.TITLE);

        artist = artist == null ? fileName : artist;
        title = title == null ? fileName : title;

        file.renameTo(new File(location + "/" + artist + " - " + title + "." + fileExtension));
    }
}