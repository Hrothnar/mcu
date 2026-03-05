package neo.utility;

import java.util.regex.Pattern;

import neo.dto.FieldState;
import neo.enums.Status;

public class Validator {

    private static Pattern latinRangePatter = Pattern.compile("[\u0000-\u007F]");
    private static Pattern cyrillicRangePattern = Pattern.compile("[\u0400-\u04FF]");

    public static FieldState validateText(String text) {
        if (text.isBlank()) {
            return new FieldState(text, Status.EMPTY.toString());
        }

        int incorrectSymbols = text.length();

        char[] bytes = text.toCharArray();

        for (char symbol : bytes) {
            String symbolAsString = String.valueOf(symbol);
            boolean isLatin = latinRangePatter.matcher(symbolAsString).find();
            boolean isCyrillic = cyrillicRangePattern.matcher(symbolAsString).find();

            if (isLatin || isCyrillic) {
                incorrectSymbols--;
            }
        }

        return incorrectSymbols == 0 ? new FieldState(text) : new FieldState(text, Status.BROKEN.toString());
    }


    public static FieldState validateBitrate(long bitrate) {
        return bitrate > 255 ? new FieldState(String.valueOf(bitrate)) : new FieldState(String.valueOf(bitrate), Status.BROKEN.toString());
    }

}
