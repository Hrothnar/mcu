package neo.enums;

public enum CommandArgument {

    FIND_BROKEN_TAGS("--find-broken-tags"),
    MODIFY_TAGS("--modify-tags"),
    COLLECT("--collect"),
    SORT("--sort"),
    BREAKDOWN("--breakdown");

    private static String[] defaultArguments = new String[] {
            COLLECT.getArgument(),
            FIND_BROKEN_TAGS.getArgument(),
            MODIFY_TAGS.getArgument(),
            SORT.getArgument(),
            BREAKDOWN.getArgument()
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
