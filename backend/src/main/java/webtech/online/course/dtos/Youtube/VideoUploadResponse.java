package webtech.online.course.dtos.Youtube;

public record VideoUploadResponse (
        String videoId,
        String videoUrl,
        String title,
        String status,
        String message,
        Long duration
)
{}