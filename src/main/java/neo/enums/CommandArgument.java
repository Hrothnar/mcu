package neo.enums;

public enum CommandArgument {

    FIND_BROKEN_TAGS("--find-broken-tags"),
    MODIFY_TAGS("--modify-tags"),
    SORT("--sort"),
    BUILD_STATUS_MAP("--build-status-map");

    private static String[] defaultArguments = new String[] {
            FIND_BROKEN_TAGS.getArgument(),
            MODIFY_TAGS.getArgument(),
            SORT.getArgument(),
            BUILD_STATUS_MAP.getArgument()
    };

    private String argument = null;

    private CommandArgument(String argument) {
        this.argument = argument;
    }

    public String getArgument() {
        return argument;
    }

    public static String[] getDefaultArguments() {
        return defaultArguments;
    }
}
