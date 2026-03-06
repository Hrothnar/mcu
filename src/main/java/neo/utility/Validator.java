package neo.utility;

import java.sql.Time;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.regex.Pattern;

import neo.dto.FieldState;
import neo.dto.FileState;
import neo.enums.Status;

public class Validator {

    private static Pattern latinRangePatter = Pattern.compile("[\u0000-\u007F]");
    private static Pattern cyrillicRangePattern = Pattern.compile("[\u0400-\u04FF]");
    private static Pattern symbols = Pattern.compile(".ru|.com|.net|\\[");

    public static FieldState validateText(String text) {
        if (text.isBlank()) {
            return new FieldState(text, Status.EMPTY.toString());
        }

        int incorrectSymbols = text.length();

        if (symbols.matcher(text).find()) {
            return new FieldState(text, Status.BROKEN.toString());
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

        return incorrectSymbols / text.length() < 0.2 ? new FieldState(text)
                : new FieldState(text, Status.BROKEN.toString());
    }

    public static FieldState validateBitrate(long bitrate) {
        return bitrate > Env.bitrateThreshold() ? new FieldState(String.valueOf(bitrate))
                : new FieldState(String.valueOf(bitrate), Status.BROKEN.toString());
    }

    public static FieldState validateYear(String year) {
        if (year.isBlank()) {
            return new FieldState(year, Status.EMPTY.toString());
        }

        try {
            LocalDate date = LocalDate.parse(year);

            if (date.isAfter(LocalDate.of(1777, 1, 1))) {
                return new FieldState(year);
            }
        } catch (DateTimeException ex) {
            try {
                int parsed = Integer.parseInt(year);

                if (parsed < 1777) {
                    return new FieldState(year, Status.BROKEN.toString());
                }
            } catch (NumberFormatException ex2) {
                return new FieldState(year, Status.BROKEN.toString());
            }
        }

        return new FieldState(year);
    }
}
