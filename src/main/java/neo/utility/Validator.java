package neo.utility;

import java.util.regex.Pattern;


public class Validator {

    private static Pattern latinRangePatter = Pattern.compile("[\u0000-\u007F]");
    private static Pattern cyrillicRangePattern = Pattern.compile("[\u0400-\u04FF]");

    public static String validate(String text) {
        if (text.isBlank()) {
            return "empty";
        }

        int incorrectSymbols = text.length();

        byte[] bytes = text.getBytes();

        for (byte symbol : bytes) {
            String symbolAsString = String.valueOf(symbol);
            if (latinRangePatter.matcher(symbolAsString).find()
                    || cyrillicRangePattern.matcher(symbolAsString).find()) {
                incorrectSymbols--;
            }
        }

        return incorrectSymbols == 0 ? text : "broken, " + text;
    }

}
