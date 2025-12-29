package webtech.online.course.services;

import livekit.LivekitWebhook;

public interface LiveKitService {
    public String createToken(String roomName, String userId, String name, boolean isTeacher, String avatar);

    void sendDataToRoom(String roomName, String jsonData);

    LivekitWebhook.WebhookEvent verifyWebhook(String body, String authHeader);

    void removeParticipant(String roomName, String identity);

    void endRoom(String roomName);

    String startRecording(String roomName);

    void stopRecording(String roomName, String egressId);

    void muteParticipant(String roomName, String identity, boolean mute);

    void muteParticipantTrack(String roomName, String identity, String trackSource, boolean mute);

    void muteAllParticipantsTrack(String roomName, String trackSource, boolean mute);

    void updatePermissions(String roomName, String identity, boolean canPublish, boolean canPublishData);
}
