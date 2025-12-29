package webtech.online.course.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Common {

    public static String parserTime(Long t) {
        long hours = t / 3600;
        long minutes = (t % 3600) / 60;
        long seconds = t % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
    public static String extractVideoId(String url) {
        String regex = "(?<=v=|v/|embed/|youtu.be/|watch\\?v=|&v=|/videos/)[^#&?]*";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(url);

        if (matcher.find()) {
            return matcher.group();
        } else {
            return "Not found ID video";
        }
    }
    public static String extractDriveFileId(String url) {
        if (url == null) return null;

        String pattern = "https?://drive\\.google\\.com/(?:file/d/|open\\?id=|uc\\?export=download&id=)([^/&?]+)";

        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(url);

        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }


}
