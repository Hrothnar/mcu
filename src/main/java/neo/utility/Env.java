package neo.utility;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class Env {

    private static String RESOURCE_DIRECTORY = "src/main/resources";
    private static String PATH_TO_ENV_FILE = RESOURCE_DIRECTORY + "/environments.env";

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
}
