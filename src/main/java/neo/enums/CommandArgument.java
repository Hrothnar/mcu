package neo.enums;

public enum CommandArgument {

    COLLECT("--collect"),
    OVERVIEW("--overview"),
    REPORT("--report"),
    MODIFY_TAGS("--modify-tags"),
    SORT("--sort"),
    BREAKDOWN("--breakdown");

    private static String[] defaultArguments = new String[] {
            COLLECT.getArgument(),
            OVERVIEW.getArgument(),
            REPORT.getArgument(),
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
