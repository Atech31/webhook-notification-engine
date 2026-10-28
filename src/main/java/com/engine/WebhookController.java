package com.engine.controller;

import com.engine.model.WebhookSubscription;
import com.engine.repository.WebhookSubscriptionRepository;
import com.engine.service.WebhookDeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/webhooks")
@RequiredArgsConstructor
public class WebhookController {

    private final WebhookSubscriptionRepository subscriptionRepository;
    private final WebhookDeliveryService deliveryService;

    @PostMapping("/subscriptions")
    public ResponseEntity<WebhookSubscription> createSubscription(@RequestBody WebhookSubscription subscription) {
        WebhookSubscription saved = subscriptionRepository.save(subscription);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/subscriptions")
    public ResponseEntity<List<WebhookSubscription>> getAllSubscriptions() {
        return ResponseEntity.ok(subscriptionRepository.findAll());
    }

    @PostMapping("/trigger")
    public ResponseEntity<String> triggerEvent(
            @RequestParam String eventType,
            @RequestBody String payload) {
        deliveryService.dispatchEvent(eventType, payload);
        return ResponseEntity.ok("Event triggered and webhooks dispatched asynchronously.");
    }
}
