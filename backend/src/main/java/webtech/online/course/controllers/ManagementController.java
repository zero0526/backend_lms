package webtech.online.course.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import webtech.online.course.models.MeetingSession;
import webtech.online.course.repositories.SessionRepository;
import webtech.online.course.security.UserPrincipal;
import webtech.online.course.services.LiveKitService;

@RestController
@RequestMapping("/api/meeting/manage")
@RequiredArgsConstructor
public class ManagementController {

    private final SessionRepository sessionRepository;
    private final LiveKitService liveKitService;

    @PostMapping("/kick")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> kickUser(@RequestParam String roomName, @RequestParam String identity) {
        liveKitService.removeParticipant(roomName, identity);
        return ResponseEntity.ok("User kicked");
    }

    @PostMapping("/end")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> endMeeting(@RequestParam String roomName) {
        liveKitService.endRoom(roomName);
        return ResponseEntity.ok("Meeting ended");
    }

    @PostMapping("/mute")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> muteUser(@RequestParam String roomName, @RequestParam String identity,
            @RequestParam boolean mute) {
        liveKitService.muteParticipant(roomName, identity, mute);
        return ResponseEntity.ok("User muted status: " + mute);
    }

    @PostMapping("/mute-camera")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> muteCamera(@RequestParam String roomName, @RequestParam String identity,
            @RequestParam boolean mute) {
        liveKitService.muteParticipantTrack(roomName, identity, "CAMERA", mute);
        return ResponseEntity.ok("User camera muted status: " + mute);
    }

    @PostMapping("/stop-screen-share")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> stopScreenShare(@RequestParam String roomName, @RequestParam String identity) {
        liveKitService.muteParticipantTrack(roomName, identity, "SCREEN_SHARE", true);
        return ResponseEntity.ok("User screen share stopped");
    }

    @PostMapping("/mute-all")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> muteAll(@RequestParam String roomName, @RequestParam boolean mute) {
        liveKitService.muteAllParticipantsTrack(roomName, "MICROPHONE", mute);
        return ResponseEntity.ok("All users muted status: " + mute);
    }

    @PostMapping("/mute-camera-all")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> muteCameraAll(@RequestParam String roomName, @RequestParam boolean mute) {
        liveKitService.muteAllParticipantsTrack(roomName, "CAMERA", mute);
        return ResponseEntity.ok("All users camera muted status: " + mute);
    }

    @PostMapping("/stop-screen-share-all")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> stopScreenShareAll(@RequestParam String roomName) {
        liveKitService.muteAllParticipantsTrack(roomName, "SCREEN_SHARE", true);
        return ResponseEntity.ok("All screen shares stopped");
    }

    @PostMapping("/kick-all")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> kickAll(@RequestParam String roomName) {
        // We'll need to implement this in LiveKitService
        // but for now let's just use a simple loop or define it in service.
        // I'll add it to LiveKitService.
        liveKitService.endRoom(roomName); // endRoom is basically kick everyone and delete and end room
        return ResponseEntity.ok("All users kicked and room ended");
    }

    @PostMapping("/permissions")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> updatePermissions(@RequestParam String roomName, @RequestParam String identity,
            @RequestParam boolean canPublish, @RequestParam boolean canPublishData) {
        liveKitService.updatePermissions(roomName, identity, canPublish, canPublishData);
        return ResponseEntity.ok("Permissions updated");
    }

    @PostMapping("/screen-share/acquire")
    public ResponseEntity<?> acquireScreenShare(@RequestParam String roomName,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        MeetingSession session = sessionRepository.findByRoomName(roomName);
        if (session == null)
            return ResponseEntity.badRequest().body("Room not found");

        if (session.getCurrentPresenterId() != null && !session.getCurrentPresenterId().isBlank()) {
            if (!session.getCurrentPresenterId().equals(String.valueOf(userPrincipal.getId()))) {
                return ResponseEntity.status(409).body("Another user is sharing screen");
            }
        }

        session.setCurrentPresenterId(String.valueOf(userPrincipal.getId()));
        sessionRepository.save(session);
        return ResponseEntity.ok("Screen share acquired");
    }

    @PostMapping("/screen-share/release")
    public ResponseEntity<?> releaseScreenShare(@RequestParam String roomName,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        MeetingSession session = sessionRepository.findByRoomName(roomName);
        if (session == null)
            return ResponseEntity.badRequest().body("Room not found");

        if (String.valueOf(userPrincipal.getId()).equals(session.getCurrentPresenterId())) {
            session.setCurrentPresenterId(null);
            sessionRepository.save(session);
            return ResponseEntity.ok("Screen share released");
        }
        return ResponseEntity.badRequest().body("You are not the presenter");
    }
}
