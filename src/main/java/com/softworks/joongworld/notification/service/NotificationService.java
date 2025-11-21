package com.softworks.joongworld.notification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.softworks.joongworld.notification.dto.NotificationCreateCommand;
import com.softworks.joongworld.notification.dto.NotificationView;
import com.softworks.joongworld.notification.exception.NotificationSerializationException;
import com.softworks.joongworld.notification.model.NotificationEntity;
import com.softworks.joongworld.notification.model.NotificationOutboxEntity;
import com.softworks.joongworld.notification.model.NotificationStatus;
import com.softworks.joongworld.notification.repository.NotificationMapper;
import com.softworks.joongworld.notification.repository.NotificationOutboxMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final int DEFAULT_RECENT_LIMIT = 20;
    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {};

    private final NotificationMapper notificationMapper;
    private final NotificationOutboxMapper notificationOutboxMapper;
    private final ObjectMapper objectMapper;

    @Transactional
    public NotificationView create(NotificationCreateCommand command) {
        if (command == null || command.getType() == null || command.getRecipientId() == null) {
            throw new IllegalArgumentException("유효하지 않은 알림 생성 요청입니다.");
        }

        NotificationEntity entity = new NotificationEntity();
        entity.setType(command.getType());
        entity.setRecipientId(command.getRecipientId());
        entity.setActorId(command.getActorId());
        entity.setTargetType(command.getTargetType());
        entity.setTargetId(command.getTargetId());
        entity.setMessage(StringUtils.hasText(command.getMessage()) ? command.getMessage() : "");
        entity.setMetadataJson(toJson(command.getMetadata()));
        entity.setStatus(NotificationStatus.CREATED);

        notificationMapper.insert(entity);
        enqueueOutbox(entity, command.getMetadata());

        return toView(entity);
    }

    public List<NotificationView> getRecent(Long recipientId, Integer limit) {
        if (recipientId == null) {
            return Collections.emptyList();
        }
        int size = (limit == null || limit <= 0) ? DEFAULT_RECENT_LIMIT : Math.min(limit, 100);
        return notificationMapper.findRecentByRecipient(recipientId, size)
                .stream()
                .map(this::toView)
                .collect(Collectors.toList());
    }

    public int countUnread(Long recipientId) {
        if (recipientId == null) {
            return 0;
        }
        return notificationMapper.countUnread(recipientId);
    }

    @Transactional
    public void markAsRead(Long notificationId, Long recipientId) {
        if (notificationId == null || recipientId == null) {
            return;
        }
        notificationMapper.markAsRead(notificationId, recipientId);
    }

    @Transactional
    public void markAllAsRead(Long recipientId) {
        if (recipientId == null) {
            return;
        }
        notificationMapper.markAllAsRead(recipientId);
    }

    private NotificationView toView(NotificationEntity entity) {
        Map<String, Object> metadata = parseJson(entity.getMetadataJson());
        boolean read = entity.getReadAt() != null || entity.getStatus() == NotificationStatus.READ;
        return NotificationView.builder()
                .id(entity.getId())
                .type(entity.getType())
                .status(entity.getStatus())
                .recipientId(entity.getRecipientId())
                .actorId(entity.getActorId())
                .targetType(entity.getTargetType())
                .targetId(entity.getTargetId())
                .message(entity.getMessage())
                .metadata(metadata)
                .read(read)
                .createdAt(entity.getCreatedAt())
                .readAt(entity.getReadAt())
                .build();
    }

    private String toJson(Map<String, Object> metadata) {
        if (CollectionUtils.isEmpty(metadata)) {
            return "{}";
        }
        try {
            return objectMapper.writeValueAsString(metadata);
        } catch (JsonProcessingException e) {
            throw new NotificationSerializationException("알림 메타데이터 직렬화에 실패했습니다.", e);
        }
    }

    private Map<String, Object> parseJson(String json) {
        if (!StringUtils.hasText(json)) {
            return Collections.emptyMap();
        }
        try {
            return objectMapper.readValue(json, MAP_TYPE);
        } catch (JsonProcessingException e) {
            throw new NotificationSerializationException("알림 메타데이터 역직렬화에 실패했습니다.", e);
        }
    }

    private void enqueueOutbox(NotificationEntity entity, Map<String, Object> metadata) {
        NotificationOutboxEntity outbox = new NotificationOutboxEntity();
        outbox.setNotificationId(entity.getId());
        outbox.setEventType(entity.getType().name());
        outbox.setPayload(buildOutboxPayload(entity, metadata));
        notificationOutboxMapper.insert(outbox);
    }

    private String buildOutboxPayload(NotificationEntity entity, Map<String, Object> metadata) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("notificationId", entity.getId());
        payload.put("recipientId", entity.getRecipientId());
        payload.put("type", entity.getType());
        payload.put("targetType", entity.getTargetType());
        payload.put("targetId", entity.getTargetId());
        payload.put("message", entity.getMessage());
        payload.put("metadata", CollectionUtils.isEmpty(metadata) ? Collections.emptyMap() : metadata);
        payload.put("createdAt", entity.getCreatedAt() != null ? entity.getCreatedAt() : OffsetDateTime.now());
        return toJson(payload);
    }
}
