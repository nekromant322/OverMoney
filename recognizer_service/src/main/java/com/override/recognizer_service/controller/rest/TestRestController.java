//package com.override.recognizer_service.controller.rest;
//
//import com.override.recognizer_service.service.VoiceConvertingService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@Slf4j
//public class TestRestController {
//
//
//    @Autowired
//    VoiceConvertingService voiceConvertingService;
//
//    @GetMapping("/test")
//    public String getRecognizer() {
//        voiceConvertingService.processWithSphinx(null);
//        return "Recognizer";
//    }
//}
