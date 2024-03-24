package com.example.ssenotification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class SSEService {
    // 기본 타임아웃 설정
    private static final Long defaultTimeOut = 60L * 1000 * 60;

    private final EmitterRepository emitterRepository;


    /**
     * 클라이언트가 구독을 위해 호출하는 메서드.
     *
     * @param userId - 구독하는 클라이언트의 사용자 아이디.
     * @return SseEmitter - 서버에서 보낸 이벤트 Emitter
     */
    public SseEmitter subscribe(Long userId) {
        SseEmitter emitter = createEmitter(userId);

        sendToClient(userId, "EventStream Created. [userId=" + userId + "]");
        return emitter;
    }
    public void notify(Long userId, Object event) {
        sendToClient(userId, event);
    }

    private void sendToClient(Long userId, Object data) {
        SseEmitter emitter = emitterRepository.get(userId);
        if(emitter != null){
            try{
                emitter.send(SseEmitter.event().id(String.valueOf(userId)).name("sse").data(data));
            } catch (IOException exception){
                emitterRepository.deleteById(userId);
                throw new RuntimeException("연결 오류입니다");
            }
        }
    }

    private SseEmitter createEmitter(Long userId) {
        SseEmitter emitter = new SseEmitter(defaultTimeOut);
        emitterRepository.save(userId, emitter);
        // Emitter 완료 시 (데이터가 전송되었을 때) 삭제
        emitter.onCompletion(() -> emitterRepository.deleteById(userId));
        // 아무 이벤트도 없을 때 삭제
        emitter.onTimeout(()-> emitterRepository.deleteById(userId));
        return emitter;
    }
}
