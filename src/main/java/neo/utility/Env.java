package neo.utility;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Set;

public class Env {

    private static String ROOT_MUSIC_DIRECTORY = null;
    private static String OUTPUTS_DIRECTORY = null;
    private static String MUSIC_EXTENSIONS = null;
    private static String BITRATE_THRESHOLD = null;

    private static String PATH_TO_ENV_FILE = "src/main/resources/environments.env";

    private static Properties properties = null;

    public static void load() {
        properties = new Properties();
        Path path = Paths.get(PATH_TO_ENV_FILE).toAbsolutePath();

        try (InputStream is = Files.newInputStream(path)) {
            properties.load(is);
        } catch (IOException ex) {
            System.out.println("There was and error, the program is terminating");
            ex.printStackTrace();
            System.exit(1);
        }
    }

    public static String get(String env) {
        if (properties == null) {
            load();
        }

        return properties.getProperty(env);
    }

    public static String tagsDirectory() {
        String outputDirectory = get("TAGS_DIRECTORY");

        if (outputDirectory == null) {
            System.err.print("Environment TAGS_DIRECTORY is not provided. Shutdown.");
            System.exit(1);
        }

        if (!Files.exists(Paths.get(outputDirectory))) {
            try {
                Files.createDirectories(Paths.get(outputDirectory));
            } catch (Exception ex) {
                System.err.println("There was a problem while creating the output directory. Shutdown.");
                System.exit(1);
            }
        }

        return outputDirectory;
    }

    public static String modifiedDirectory() {
        String outputDirectory = get("MODIFIED_DIRECTORY");

        if (outputDirectory == null) {
            System.err.print("Environment MODIFIED_DIRECTORY is not provided. Shutdown.");
            System.exit(1);
        }

        if (!Files.exists(Paths.get(outputDirectory))) {
            try {
                Files.createDirectories(Paths.get(outputDirectory));
            } catch (Exception ex) {
                System.err.println("There was a problem while creating the output directory. Shutdown.");
                System.exit(1);
            }
        }

        return outputDirectory;
    }

    public static String brokenDirectory() {
        String outputDirectory = get("BROKEN_DIRECTORY");

        if (outputDirectory == null) {
            System.err.print("Environment BROKEN_DIRECTORY is not provided. Shutdown.");
            System.exit(1);
        }

        if (!Files.exists(Paths.get(outputDirectory))) {
            try {
                Files.createDirectories(Paths.get(outputDirectory));
            } catch (Exception ex) {
                System.err.println("There was a problem while creating the output directory. Shutdown.");
                System.exit(1);
            }
        }

        return outputDirectory;
    }

    public static Set<String> supportedExtensions() {
        Set<String> supportedExtensions = Set.of(get("MUSIC_EXTENSIONS").replaceAll("\s", "").split(","));
        return supportedExtensions.size() > 0 ? supportedExtensions : Set.of("mp3", "flac");
    }

    public static String rootMusicDirectory() {
        String rootMusicDirectory = get("ROOT_MUSIC_DIRECTORY");

        if (rootMusicDirectory == null) {
            System.err.print("Environment ROOT_MUSIC_DIRECTORY is not provided. Shutdown.");
            System.exit(1);
        }

        return rootMusicDirectory;
    }

    public static long bitrateThreshold() {
        String bitrateThreshold = get("BITRATE_THRESHOLD");
        return Long.parseLong(bitrateThreshold != null ? bitrateThreshold : "256");
    }
}