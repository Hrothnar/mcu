package neo;

import java.io.IOException;
import java.util.Set;

import neo.enums.CommandArgument;
import neo.utility.Env;

public class Main {

    public static void main(String[] args) throws IOException {
        System.out
                .println("==========================================================================================");
        System.out.println("The program is in the process...");

        Env.load();

        Set<String> passedArguments = Set.of(args.length != 0 ? args : CommandArgument.getDefaultArguments());

        passedArguments.forEach((argument) -> {
            if (CommandArgument.COLLECT.getArgument().equals(argument)) {
                // new Collector().run();
            } else if (CommandArgument.OVERVIEW.getArgument().equals(argument)) {
                // new OverviewBuilder().run();
            } else if (CommandArgument.REPORT.getArgument().equals(argument)) {
                new ReportBuilder().run();
            } else if (CommandArgument.MODIFY_TAGS.getArgument().equals(argument)) {
                // new TagModifier().run();
            } else if (CommandArgument.SORT.getArgument().equals(argument)) {
                // new Sorter().run();
            } else if (CommandArgument.BREAKDOWN.getArgument().equals(argument)) {
                // new BreakdownBuilder().run();
            }
        });

        System.out.println("The program has finished.");
        System.out
                .println("==========================================================================================");
    }
}