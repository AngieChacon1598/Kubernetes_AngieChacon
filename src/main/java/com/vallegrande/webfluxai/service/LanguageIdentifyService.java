package com.vallegrande.webfluxai.service;

import com.vallegrande.webfluxai.config.ApiProperties;
import com.vallegrande.webfluxai.config.ApiProperties.LanguageIdentify;
import com.vallegrande.webfluxai.dto.LanguageDetectionRequest;
import com.vallegrande.webfluxai.dto.LanguageDetectionResponse;
import com.vallegrande.webfluxai.exception.ApiException;
import com.vallegrande.webfluxai.model.LanguageDetection;
import com.vallegrande.webfluxai.model.LanguageDetection.DetectedLanguage;
import com.vallegrande.webfluxai.repository.LanguageDetectionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;
import com.fasterxml.jackson.databind.JsonNode;

@Slf4j
@Service
@RequiredArgsConstructor
public class LanguageIdentifyService {

    @Qualifier("languageIdentifyWebClient")
    private final WebClient languageIdentifyWebClient;
    private final LanguageDetectionRepository repository;
    private final ApiProperties apiProperties;

    public Mono<JsonNode> detectLanguage(String text) {
        log.info("Detecting language for text: {}", text);
        
        LanguageIdentify config = apiProperties.getLanguageIdentify();
        if (config == null) {
            return Mono.error(new ApiException(
                HttpStatus.INTERNAL_SERVER_ERROR, 
                "Language identification configuration is missing"
            ));
        }
        
        return languageIdentifyWebClient.post()
                .uri("/languageIdentify")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("text", text))
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(error -> {
                                    log.error("Error detecting language: {}", error);
                                    return Mono.error(new ApiException(
                                            HttpStatus.valueOf(clientResponse.statusCode().value()),
                                            "Error detecting language: " + error
                                    ));
                                })
                )
                .bodyToMono(JsonNode.class)
                .map(response -> {
                    // Extraemos el primer resultado
                    JsonNode codes = response.get("languageCodes");
                    if (codes != null && codes.isArray() && codes.size() > 0) {
                        // Solo tomamos el primero (el más confiable)
                        log.info("Idioma detectado: {} con confianza: {}", 
                                codes.get(0).get("code").asText(),
                                codes.get(0).get("confidence").asDouble());
                        return codes.get(0);
                    }
                    log.warn("No se encontraron códigos de idioma en la respuesta");
                    return response;
                })
                .doOnNext(result -> log.info("Resultado final: {}", result));
    }
    
    public Mono<LanguageDetection> getDetectionById(String id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new ApiException(
                        HttpStatus.NOT_FOUND,
                        "Language detection not found with id: " + id
                )));
    }
    
    private String getLanguageName(String code) {
        // Simple mapping of language codes to names
        // You might want to use a proper localization solution in production
        return switch (code.toLowerCase()) {
            case "en" -> "English";
            case "es" -> "Spanish";
            case "fr" -> "French";
            case "de" -> "German";
            case "it" -> "Italian";
            case "pt" -> "Portuguese";
            case "ru" -> "Russian";
            case "zh" -> "Chinese";
            case "ja" -> "Japanese";
            case "ko" -> "Korean";
            default -> "Unknown";
        };
    }
}
