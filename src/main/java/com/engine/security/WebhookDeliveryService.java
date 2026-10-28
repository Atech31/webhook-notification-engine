package com.engine.service;

import com.engine.model.DeliveryStatus;
import com.engine.model.WebhookDeliveryAttempt;
import com.engine.model.WebhookSubscription;
import com.engine.repository.WebhookDeliveryAttemptRepository;
import com.engine.repository.WebhookSubscriptionRepository;
import com.engine.security.WebhookSigner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebhookDeliveryService {

    private final WebhookSubscriptionRepository subscriptionRepository;
    private final WebhookDeliveryAttemptRepository attemptRepository;
    private final WebClient webClient = WebClient.create();

    @Async
    public void dispatchEvent(String eventType, String payload) {
        List<WebhookSubscription> subscriptions = subscriptionRepository.findByEventTypeAndActiveTrue(eventType);
        log.info("Found {} active subscription(s) for event type: {}", subscriptions.size(), eventType);

        for (WebhookSubscription sub : subscriptions) {
            sendWebhook(sub, payload);
        }
    }

    public void sendWebhook(WebhookSubscription subscription, String payload) {
        String signature = WebhookSigner.generateSignature(payload, subscription.getSecretKey());

        WebhookDeliveryAttempt attempt = WebhookDeliveryAttempt.builder()
                .subscriptionId(subscription.getId())
                .payload(payload)
                .status(DeliveryStatus.PENDING)
                .attemptCount(1)
                .build();
        attempt = attemptRepository.save(attempt);

        try {
            var response = webClient.post()
                    .uri(subscription.getTargetUrl())
                    .header("Content-Type", "application/json")
                    .header("X-Webhook-Signature", signature)
                    .header("X-Event-Type", subscription.getEventType())
                    .bodyValue(payload)
                    .retrieve()
                    .toBodilessEntity()
                    .block();

            int statusCode = response != null ? response.getStatusCode().value() : 500;

            if (statusCode >= 200 && statusCode < 300) {
                attempt.setStatus(DeliveryStatus.SUCCESS);
                attempt.setResponseCode(statusCode);
                attempt.setResponseMessage("Delivery successful");
                log.info("Successfully delivered webhook to URL: {}", subscription.getTargetUrl());
            } else {
                attempt.setStatus(DeliveryStatus.FAILED);
                attempt.setResponseCode(statusCode);
                attempt.setResponseMessage("Received non-success HTTP status code");
                log.warn("Webhook delivery failed with status {} for URL: {}", statusCode, subscription.getTargetUrl());
            }
        } catch (Exception e) {
            attempt.setStatus(DeliveryStatus.FAILED);
            attempt.setResponseCode(500);
            attempt.setResponseMessage(e.getMessage());
            log.error("Exception occurred while delivering webhook to URL: {}", subscription.getTargetUrl(), e);
        }

        attemptRepository.save(attempt);
    }
}
