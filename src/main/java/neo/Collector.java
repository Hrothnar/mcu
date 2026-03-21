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
    private String discardedDirectory = Utility.createDirectory(workingDirectory + "/discarded");
    private String transferredDirectory = Utility.createDirectory(workingDirectory + "/transferred");

    private List<String> ignoreFolders = List.of(
            "System of a Down",
            "Король и Шут",
            "КняZz",
            "Caravan Palace");

    public void run() {
        try {
            File music = new File(musicDirectory);

            collectMusic(music, 0);
        } catch (Exception ex) {
            Utility.handleException(ex);
        }
    }

    private void collectMusic(File path, int depth) throws Exception {
        File[] files = path.listFiles();

        for (File file : files) {
            if (ignoreFolders.contains(file.getName())) {
                Utility.copyDirectory(file.getAbsolutePath(), transferredDirectory + "/" + file.getName());
                continue;
            }

            if (file.isDirectory()) {
                collectMusic(file, ++depth);
            } else {
                String absoluteString = file.getAbsolutePath();
                Path absolutePath = Path.of(absoluteString);
                String fileName = file.getName();
                String location = absoluteString.replace(fileName, "");

                if (!Utility.isFileLegit(file)) {
                    Files.copy(absolutePath, Path.of(discardedDirectory + "/" + fileName),
                            StandardCopyOption.REPLACE_EXISTING);
                } else {
                    Files.copy(absolutePath, Path.of(collectedDirectory + "/" + fileName),
                            StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }
}