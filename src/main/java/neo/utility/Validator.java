package neo.utility;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.regex.Pattern;

import neo.dto.TagState;
import neo.enums.Status;

public class Validator {

    private static Pattern latinRangePatter = Pattern.compile("[\u0000-\u007F]");
    private static Pattern cyrillicRangePattern = Pattern.compile("[\u0400-\u04FF]");
    private static Pattern symbols = Pattern.compile("\\.ru|\\.com|\\.net|\\[muz");

    public static TagState validateText(String text) {
        if (text.isBlank()) {
            return new TagState(text, Status.EMPTY);
        }

        int incorrectSymbols = text.length();

        if (symbols.matcher(text).find()) {
            return new TagState(text, Status.BROKEN);
        }

        char[] bytes = text.toCharArray();

        for (char symbol : bytes) {
            String symbolAsString = String.valueOf(symbol);
            boolean isLatin = latinRangePatter.matcher(symbolAsString).find();
            boolean isCyrillic = cyrillicRangePattern.matcher(symbolAsString).find();

            if (isLatin || isCyrillic) {
                incorrectSymbols--;
            }
        }

        return ((double) incorrectSymbols / text.length()) > 0.2 ? 
                    new TagState(text, Status.BROKEN) : 
                    new TagState(text);
        }

    public static TagState validateBitrate(long bitrate) {
        return bitrate >= Env.bitrateThreshold() ? new TagState(String.valueOf(bitrate))
                : new TagState(String.valueOf(bitrate), Status.BROKEN);
    }

    public static TagState validateYear(String year) {
        if (year.isBlank()) {
            return new TagState(year, Status.EMPTY);
        }

        try {
            LocalDate date = LocalDate.parse(year);

            if (date.isAfter(LocalDate.of(1777, 1, 1))) {
                return new TagState(year);
            }
        } catch (DateTimeException ex) {
            try {
                int parsed = Integer.parseInt(year);

                if (parsed < 1777) {
                    return new TagState(year, Status.BROKEN);
                }
            } catch (NumberFormatException ex2) {
                return new TagState(year, Status.BROKEN);
            }
        }

        return new TagState(year);
    }
}
