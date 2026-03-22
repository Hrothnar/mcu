package neo;

import java.io.File;
import java.util.UUID;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
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
    private String newTagsDirectory = Utility.checkDirectory(workingDirectory + "/tags_new");

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

    private void modifyTags(File path) throws Exception {
        File[] tagFiles = path.listFiles();

        for (int i = 0; i < tagFiles.length; i++) {
            FileState[] updTags = om.readValue(tagFiles[i], FileState[].class);

            for (int j = 0; j < updTags.length; j++) {
                FileState fileState = updTags[j];

                File file = new File(collectedDirectory + "/" + fileState.name);

                if (Utility.isFileBroken(file)) {
                    continue;
                }

                AudioFile audio = AudioFileIO.read(file);
                Tag tag = audio.getTag();

                if (fileState.title.upd != null) {
                    tag.setField(FieldKey.TITLE, fileState.title.upd);
                }

                if (fileState.artist.upd != null) {
                    tag.setField(FieldKey.ARTIST, fileState.artist.upd);
                }

                if (fileState.album.upd != null) {
                    tag.setField(FieldKey.ALBUM, fileState.album.upd);
                }

                if (fileState.year.upd != null) {
                    tag.setField(FieldKey.YEAR, fileState.year.upd);
                }

                if (fileState.genre.upd != null) {
                    tag.setField(FieldKey.GENRE, fileState.genre.upd);
                }

                audio.commit();
            }
        }
    }

    private void renameFiles(File path) throws Exception {
        File[] files = path.listFiles();

        for (File file : files) {
            if (file.isDirectory()) {
                renameFiles(file);
            } else {
                if (Utility.isFileBroken(file)) {
                    continue;
                }

                AudioFile audio = AudioFileIO.read(file);
                Tag tag = audio.getTag();

                renameFile(file, tag);
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

        String uuid = UUID.randomUUID().toString();
        String temporalFileName = location + "/" + uuid + "." + fileExtension;
        String newFileName = location + "/" + artist + " - " + title + "." + fileExtension;
        String alternativeFileName = location + "/" + artist + " - " + title + "-" + uuid + "." + fileExtension;

        File newName = null;

        File temporal = new File(temporalFileName);
        file.renameTo(temporal);

        File newFile = new File(newFileName);

        if (newFile.exists()) {
            newName = new File(alternativeFileName);
        } else {
            newName = new File(newFileName);
        }

        temporal.renameTo(newName);
    }
}