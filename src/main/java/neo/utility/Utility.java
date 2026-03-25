package neo.utility;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.tag.Tag;

public class Utility {

    private static String workingDirectory = Env.getWorkingDirectory();
    private static String brokenFilesDirectory = Utility.createDirectory(workingDirectory + "/broken_files");

    public static <T> List<List<T>> splitList(List<T> list, int partitionSize) {
        List<List<T>> partitions = new ArrayList<>();

        for (int i = 0; i < list.size(); i += partitionSize) {
            partitions.add(list.subList(i, Math.min(i + partitionSize, list.size())));
        }

        return partitions;
    }

    public static void handleException(Exception ex) {
        System.out.println("There was and error, the program is terminating");
        ex.printStackTrace();
        System.exit(1);
    }

    public static String checkDirectory(String path) {
        try {
            if (path == null) {
                throw new RuntimeException(String.format("Directory path is not provided"));
            }

            if (!Files.exists(Paths.get(path))) {
                throw new RuntimeException(String.format("Directory under %s does not exist", path));
            }
        } catch (Exception ex) {
            Utility.handleException(ex);
        }

        return path;
    }

    public static String createDirectory(String path) {
        try {
            if (path == null) {
                throw new RuntimeException(String.format("Directory path %s is not provided"));
            }

            Path requiredDirectory = Paths.get(path);

            if (!Files.exists(requiredDirectory)) {
                Files.createDirectories(requiredDirectory);
            }
        } catch (Exception ex) {
            Utility.handleException(ex);
        }

        return path;
    }

    public static void copyDirectory(String sourceDirectory, String destinationDirectory) throws IOException {
        Files.walk(Paths.get(sourceDirectory)).forEach((source) -> {
            try {
                Path destination = Paths.get(destinationDirectory,
                        source.toString().substring(sourceDirectory.length()));

                if (!Files.exists(destination)) {
                    Files.copy(source, destination);
                }
            } catch (IOException ex) {
                handleException(ex);
            }
        });
    }

    public static boolean isFileBroken(File file) {
        try {
            if (file == null) {
                return true;
            }

            String fileExtension = file.getName().substring(file.getName().lastIndexOf(".") + 1);

            AudioFile audio = AudioFileIO.read(file);
            AudioHeader header = audio.getAudioHeader();
            Tag tag = audio.getTag();

            if (audio == null || header == null || tag == null) {
                throw new Exception("File could now be handled");
            }

            return false;
        } catch (Exception ex) {
            System.out.printf("There's something wrong with this file %s\n", file.getAbsolutePath());

            if (file.exists()) {
                try {
                    Files.move(Path.of(file.getAbsolutePath()), Path.of(brokenFilesDirectory + "/" + file.getName()));
                } catch (IOException e) {
                    handleException(e);
                }
            }

            return true;
        }
    }

    public static String extractFirstEntry(String text) {
        String[] artistsChain1 = text.split(",");
        String[] artistsChain2 = artistsChain1[0].split("&");
        String[] artistsChain3 = artistsChain2[0].split("\\|");
        String[] artistsChain4 = artistsChain3[0].split("/");
        String[] artistsChain5 = artistsChain4[0].split("feat.");
        String[] artistsChain6 = artistsChain5[0].split(";");

        return artistsChain6[0];
    }
}