package neo;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

import neo.utility.Env;
import neo.utility.Utility;

public class Collector {

    private String musicDirectory = Env.getMusicDirectory();
    private String workingDirectory = Env.getWorkingDirectory();
    private String collectedDirectory = Utility.createDirectory(workingDirectory + "/collected");
    private String transferredDirectory = Utility.createDirectory(workingDirectory + "/transferred");

    private List<String> ignoreFolders = List.of(
            "System of a Down",
            "Король и Шут",
            "КняZz",
            "Caravan Palace");

    public void run() {
        try {
            File music = new File(musicDirectory);

            collectMusic(music);
        } catch (Exception ex) {
            Utility.handleException(ex);
        }
    }

    private void collectMusic(File path) throws Exception {
        File[] files = path.listFiles();

        for (File file : files) {
            if (ignoreFolders.contains(file.getName())) {
                Utility.copyDirectory(file.getAbsolutePath(), transferredDirectory + "/" + file.getName());
                continue;
            }

            if (file.isDirectory()) {
                collectMusic(file);
            } else {
                if (Utility.isFileBroken(file)) {
                    continue;
                }

                String absoluteString = file.getAbsolutePath();
                Path absolutePath = Path.of(absoluteString);
                String fileName = file.getName();

                Files.copy(absolutePath, Path.of(collectedDirectory + "/" + fileName), StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }
}