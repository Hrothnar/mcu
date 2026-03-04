package neo.dto;

public class TopStructure {

    public FileReport original = null;
    public FileReport modified = null;


    public TopStructure(FileReport original) {
        this.original = original;
    }

    public TopStructure(FileReport original, FileReport modified) {
        this.original = original;
        this.modified = modified;
    }
}