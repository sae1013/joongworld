package com.softworks.joongworld.notification.support;

import com.softworks.joongworld.notification.dto.NotificationListResponse;
import com.softworks.joongworld.notification.dto.NotificationPushPayload;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class NotificationSseService {

    private static final long DEFAULT_TIMEOUT = Duration.ofMinutes(30).toMillis();

    private final Map<Long, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(Long userId) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        emitters.computeIfAbsent(userId, id -> new CopyOnWriteArrayList<>()).add(emitter);

        emitter.onCompletion(() -> removeEmitter(userId, emitter));
        emitter.onTimeout(() -> removeEmitter(userId, emitter));
        emitter.onError((ex) -> removeEmitter(userId, emitter));

        return emitter;
    }

    public void sendInitial(SseEmitter emitter, NotificationListResponse payload) {
        try {
            emitter.send(SseEmitter.event().name("init").data(payload));
        } catch (IOException e) {
            emitter.completeWithError(e);
        }
    }

    public void sendNotification(Long userId, NotificationPushPayload payload) {
        sendEvent(userId, "notification", payload);
    }

    private void sendEvent(Long userId, String eventName, Object data) {
        List<SseEmitter> userEmitters = emitters.get(userId);
        if (userEmitters == null || userEmitters.isEmpty()) {
            return;
        }
        List<SseEmitter> deadEmitters = new ArrayList<>();
        for (SseEmitter emitter : userEmitters) {
            try {
                emitter.send(SseEmitter.event().name(eventName).data(data));
            } catch (IOException e) {
                emitter.completeWithError(e);
                deadEmitters.add(emitter);
            }
        }
        if (!deadEmitters.isEmpty()) {
            userEmitters.removeAll(deadEmitters);
            if (userEmitters.isEmpty()) {
                emitters.remove(userId);
            }
        }
    }

    private void removeEmitter(Long userId, SseEmitter emitter) {
        List<SseEmitter> userEmitters = emitters.get(userId);
        if (userEmitters == null) {
            return;
        }
        userEmitters.remove(emitter);
        if (userEmitters.isEmpty()) {
            emitters.remove(userId);
        }
    }
}
