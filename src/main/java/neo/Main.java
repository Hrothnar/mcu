package neo;

import java.util.Set;

import neo.enums.CommandArgument;

public class Main {

    public static void main(String[] args) {
        System.out.println("==========================================================================================");
        System.out.println("The program is in the process...");

        Set<String> passedArguments = Set.of(args.length != 0 ? args : CommandArgument.getDefaultArguments());

        passedArguments.forEach((argument) -> {
            if (CommandArgument.FIND_BROKEN_TAGS.getArgument().equals(argument)) {
                new BrokenTagsFinder().run();
            } else if (CommandArgument.MODIFY_TAGS.getArgument().equals(argument)) {
                new TagModifier().run();
            } else if (CommandArgument.SORT.getArgument().equals(argument)) {
                new Sorter().run();
            } else if (CommandArgument.BUILD_STATUS_MAP.getArgument().equals(argument)) {
                new StatusMapBuilder().run();
            }
        });

        System.out.println("The program has finished.");
        System.out.println("==========================================================================================");
    }
}