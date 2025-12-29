package webtech.online.course.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class DatetimeUtils {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String formatLocalDateTimeToString(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(FORMATTER);
    }

    public static LocalDateTime parseStringToLocalDateTime(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.trim().isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(dateTimeString, FORMATTER);
    }
    public static  String convertSecond2ISO(Long duration) {
        long hours = duration / 3600;
        long minutes = (duration % 3600) / 60;
        long seconds = duration % 60;

        return String.format("%d:%02d:%02d", hours, minutes, seconds);
    }
    public static Long convertIso2Second(String isoSecond){
        List<String> temp= Arrays.stream(isoSecond.trim().split(":")).toList();
        return Long.parseLong(temp.get(0))*3600 + Long.parseLong(temp.get(1))*60 + Long.parseLong(temp.get(2));
    }
    public static Instant toInstant(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.of("Asia/Ho_Chi_Minh"))
                .toInstant();
    }
}
