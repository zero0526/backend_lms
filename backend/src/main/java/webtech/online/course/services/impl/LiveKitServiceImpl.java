package webtech.online.course.services.impl;

import com.google.gson.Gson;
import io.livekit.server.*;
import jakarta.annotation.PostConstruct;
import livekit.LivekitEgress;
import livekit.LivekitModels;
import livekit.LivekitWebhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Response;
import webtech.online.course.configs.LiveKitConfig;
import webtech.online.course.exceptions.BaseError;
import webtech.online.course.services.LiveKitService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class LiveKitServiceImpl implements LiveKitService {

    private final LiveKitConfig liveKitConfig;
    private RoomServiceClient roomClient;
    private final Gson gson;
    private WebhookReceiver webhookReceiver;
    private EgressServiceClient egressClient;

    @PostConstruct
    @SuppressWarnings("deprecation")
    public void init() {
        this.roomClient = RoomServiceClient.create(
                liveKitConfig.getUrl(),
                liveKitConfig.getApiKey(),
                liveKitConfig.getApiSecret());

        this.egressClient = EgressServiceClient.create(
                liveKitConfig.getUrl(),
                liveKitConfig.getApiKey(),
                liveKitConfig.getApiSecret());

        this.webhookReceiver = new WebhookReceiver(
                liveKitConfig.getApiKey(),
                liveKitConfig.getApiSecret());
    }

    @Override
    public String createToken(String roomName, String userId, String name, boolean isTeacher, String avatar) {
        AccessToken token = new AccessToken(liveKitConfig.getApiKey(), liveKitConfig.getApiSecret());

        token.setName(name);
        token.setIdentity(userId);

        Map<String, String> metadataMap = new HashMap<>();
        metadataMap.put("role", isTeacher ? "TEACHER" : "STUDENT");
        metadataMap.put("avatar", avatar != null ? avatar : "");
        token.setMetadata(gson.toJson(metadataMap));

        // Use specific Grant objects instead of VideoGrant to avoid abstract class
        // issues.
        token.addGrants(
                new RoomJoin(true),
                new RoomName(roomName),
                new CanPublish(true),
                new CanSubscribe(true),
                new CanPublishData(true));

        return token.toJwt();
    }

    /**
     * Hàm gửi tin nhắn Chat (Broadcast) qua WebRTC
     */
    @Override
    public void sendDataToRoom(String roomName, String jsonData) {
        try {
            roomClient.sendData(roomName, jsonData.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                    LivekitModels.DataPacket.Kind.RELIABLE,
                    java.util.Collections.emptyList());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BaseError(e.getMessage());
        }
    }

    /**
     * Hàm xác thực Webhook (Quan trọng cho bảo mật)
     */
    @Override
    public LivekitWebhook.WebhookEvent verifyWebhook(String body, String authHeader) {
        try {
            return webhookReceiver.receive(body, authHeader);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("Invalid webhook signature", e);
        }
    }

    @Override
    public void removeParticipant(String roomName, String identity) {
        try {
            roomClient.removeParticipant(roomName, identity).execute();
            log.info("Successfully removed participant {} from room {}", identity, roomName);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BaseError(e.getMessage());
        }
    }

    @Override
    public void endRoom(String roomName) {
        try {
            roomClient.deleteRoom(roomName).execute();
            log.info("Successfully ended/deleted room: {}", roomName);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BaseError(e.getMessage());
        }
    }

    @Override
    public String startRecording(String roomName) {
        try {
            String fileName = "/out/" + roomName + "_" + System.currentTimeMillis() + ".mp4";

            LivekitEgress.EncodedFileOutput fileOutput = LivekitEgress.EncodedFileOutput.newBuilder()
                    .setFilepath(fileName)
                    .build();

            log.info("===> [HỆ THỐNG GHI HÌNH] Gửi yêu cầu tới LiveKit: Room={}, Path={}", roomName, fileName);

            Call<LivekitEgress.EgressInfo> call = egressClient.startRoomCompositeEgress(
                    roomName,
                    fileOutput,
                    "speaker",
                    LivekitEgress.EncodingOptionsPreset.H264_720P_30);

            Response<LivekitEgress.EgressInfo> response = call.execute();

            if (response.isSuccessful() && response.body() != null) {
                LivekitEgress.EgressInfo info = response.body();
                log.info("Khởi tạo ghi hình thành công! EgressID: {}", info.getEgressId());
                return info.getEgressId();
            } else {
                log.error("LiveKit trả về lỗi khi START recording: code={}, error={}",
                        response.code(), response.errorBody() != null ? response.errorBody().string() : "N/A");
                throw new BaseError("Server LiveKit từ chối bắt đầu ghi hình: " + response.code());
            }

        } catch (Exception e) {
            log.error("Lỗi khi gọi API startRecording: {}", e.getMessage(), e);
            throw new BaseError("Không thể khởi tạo ghi hình: " + e.getMessage());
        }
    }

    @Override
    public void stopRecording(String roomName, String egressId) {
        try {
            log.info("Đang dừng ghi hình cho phòng: {}, EgressID: {}", roomName, egressId);

            Call<LivekitEgress.EgressInfo> call = egressClient.stopEgress(egressId);
            Response<LivekitEgress.EgressInfo> response = call.execute();

            if (response.isSuccessful() && response.body() != null) {
                log.info("Đã dừng ghi hình thành công cho phòng {}: {}", roomName, egressId);
            } else if (response.code() == 412) {
                log.warn("Recording {} đã ở trạng thái dừng hoặc lỗi trước đó (412). Bỏ qua lỗi này.", egressId);
            } else {
                log.error("Lỗi khi STOP recording: code={}, error={}",
                        response.code(), response.errorBody() != null ? response.errorBody().string() : "N/A");
                throw new BaseError("Không thể dừng ghi hình: Server trả về lỗi " + response.code());
            }

        } catch (BaseError be) {
            throw be;
        } catch (Exception e) {
            log.error("Lỗi hệ thống khi dừng ghi hình: {}", e.getMessage(), e);
            throw new BaseError("Lỗi hệ thống khi dừng ghi hình: " + e.getMessage());
        }
    }

    @Override
    public void muteParticipant(String roomName, String identity, boolean mute) {
        muteParticipantTrack(roomName, identity, "MICROPHONE", mute);
    }

    @Override
    public void muteAllParticipantsTrack(String roomName, String trackSource, boolean mute) {
        if (roomName == null || roomName.trim().isEmpty()) {
            log.error("Room name is required for bulk muting");
            return;
        }

        try {
            Response<List<LivekitModels.ParticipantInfo>> response = roomClient.listParticipants(roomName).execute();
            if (!response.isSuccessful() || response.body() == null) {
                log.error("Failed to list participants for bulk muting in room {}: HTTP {}", roomName, response.code());
                return;
            }

            for (LivekitModels.ParticipantInfo participant : response.body()) {
                String metadata = participant.getMetadata();
                if (metadata != null && metadata.contains("\"role\":\"TEACHER\"")) {
                    log.info("Skipping bulk mute for teacher: {}", participant.getIdentity());
                    continue;
                }

                log.info("Bulk muting track {} for participant: {}", trackSource, participant.getIdentity());
                muteParticipantTrack(roomName, participant.getIdentity(), trackSource, mute);
            }
            log.info("Completed bulk {} for track {} in room {}", mute ? "mute" : "unmute", trackSource, roomName);
        } catch (Exception e) {
            log.error("Error in bulk muting: {}", e.getMessage());
            throw new BaseError("Failed to bulk mute: " + e.getMessage());
        }
    }

    @Override
    public void muteParticipantTrack(String roomName, String identity, String trackSource, boolean mute) {
        if (roomName == null || roomName.trim().isEmpty() || identity == null || identity.trim().isEmpty()) {
            log.error("Room name and identity are required for muting");
            return;
        }

        try {
            Response<List<LivekitModels.ParticipantInfo>> response = roomClient.listParticipants(roomName).execute();
            if (!response.isSuccessful() || response.body() == null) {
                log.error("Failed to list participants in room {}: HTTP {}", roomName, response.code());
                return;
            }

            for (LivekitModels.ParticipantInfo participant : response.body()) {
                if (participant.getIdentity().equals(identity)) {
                    for (LivekitModels.TrackInfo track : participant.getTracksList()) {
                        String sourceStr = track.getSource().name(); // CAMERA, MICROPHONE, SCREEN_SHARE, etc.
                        boolean match = false;

                        if ("MICROPHONE".equalsIgnoreCase(trackSource)
                                && track.getType() == LivekitModels.TrackType.AUDIO) {
                            match = true;
                        } else if ("CAMERA".equalsIgnoreCase(trackSource)
                                && track.getSource() == LivekitModels.TrackSource.CAMERA) {
                            match = true;
                        } else if ("SCREEN_SHARE".equalsIgnoreCase(trackSource) &&
                                (track.getSource() == LivekitModels.TrackSource.SCREEN_SHARE ||
                                        track.getSource() == LivekitModels.TrackSource.SCREEN_SHARE_AUDIO)) {
                            match = true;
                        }

                        if (match) {
                            roomClient.mutePublishedTrack(roomName, identity, track.getSid(), mute).execute();
                            log.info("Successfully {} track {} ({}) for participant {} in room {}",
                                    mute ? "muted" : "unmuted", track.getSid(), trackSource, identity, roomName);
                        }
                    }
                    break;
                }
            }
        } catch (Exception e) {
            log.error("Error muting participant track: {}", e.getMessage());
            throw new BaseError("Failed to mute participant track: " + e.getMessage());
        }
    }

    @Override
    public void updatePermissions(String roomName, String identity, boolean canPublish, boolean canPublishData) {
        try {
            LivekitModels.ParticipantPermission permission = LivekitModels.ParticipantPermission.newBuilder()
                    .setCanPublish(canPublish)
                    .setCanPublishData(canPublishData)
                    .setCanSubscribe(true)
                    .build();

            roomClient.updateParticipant(roomName, identity, null, null, permission).execute();
            log.info("Updated permissions for participant {}: canPublish={}, canPublishData={}", identity, canPublish,
                    canPublishData);
        } catch (Exception e) {
            log.error("Failed to update permissions for {}: {}", identity, e.getMessage());
            throw new BaseError("Failed to update permissions: " + e.getMessage());
        }
    }
}
