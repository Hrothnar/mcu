package neo.utility;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Set;

public class Env {

    private static Properties properties = null;

    private static String ENV_ROOT_MUSIC_DIRECTORY = "ROOT_MUSIC_DIRECTORY";
    private static String ENV_ROOT_WORKING_DIRECTORY = "ROOT_WORKING_DIRECTORY";

    public static void load() {
        properties = new Properties();

        Path path = Paths.get("src/main/resources/environments.env").toAbsolutePath();

        try (InputStream is = Files.newInputStream(path)) {
            properties.load(is);

            Utility.checkDirectory(getMusicDirectory());
            Utility.createDirectory(getWorkingDirectory());

        } catch (IOException ex) {
            Utility.handleException(ex);
        }
    }

    public static String get(String env) {
        return properties.getProperty(env);
    }

    public static String getMusicDirectory() {
        return get(ENV_ROOT_MUSIC_DIRECTORY);
    }

    public static String getWorkingDirectory() {
        return get(ENV_ROOT_WORKING_DIRECTORY);
    }

    public static Set<String> supportedExtensions() {
        Set<String> supportedExtensions = Set.of(get("MUSIC_EXTENSIONS").replaceAll("\s", "").split(","));
        return supportedExtensions.size() > 0 ? supportedExtensions : Set.of("mp3", "flac");
    }

    public static long bitrateThreshold() {
        String bitrateThreshold = get("BITRATE_THRESHOLD");
        return Long.parseLong(bitrateThreshold != null ? bitrateThreshold : "256");
    }
}