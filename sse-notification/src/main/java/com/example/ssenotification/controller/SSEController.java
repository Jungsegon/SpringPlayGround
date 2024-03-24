package com.example.ssenotification.controller;

import com.example.ssenotification.service.SSEService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.awt.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class SSEController {
    @Autowired
    private final SSEService sseService;

    @GetMapping(value = "/subscribe/{id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subcribe(@PathVariable Long id){
        return sseService.subscribe(id);
    }

    @PostMapping("/send-data/{id}")
    public void sendDataTEst(@PathVariable Long id) {
        sseService.notify(id, "data");
    }

}
