package webtech.online.course.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import webtech.online.course.domains.VideoDeletedEvent;
import webtech.online.course.services.YoutubeService;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class YoutubeDeleteListener {
    private final YoutubeService youtubeService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleVideoDeleted(VideoDeletedEvent event) throws IOException {
        for (String videoId : event.getVideoIds()) {

            try {
                youtubeService.deleteVideo(videoId);
                log.info("Deleted YouTube video: {}", videoId);

            } catch (Exception e) {
                log.error("Failed to delete YouTube video: {}", videoId, e);
            }
        }
    }
}
