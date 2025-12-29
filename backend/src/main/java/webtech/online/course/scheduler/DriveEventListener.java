package webtech.online.course.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import webtech.online.course.domains.DriveFilesDeletedEvent;
import webtech.online.course.services.DriveService;

@Component
@RequiredArgsConstructor
@Slf4j
public class DriveEventListener {

    private final DriveService googleDriveService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onDriveFilesDeleted(DriveFilesDeletedEvent event) {
        googleDriveService.deleteFiles(event.getFileIds());
    }
}
