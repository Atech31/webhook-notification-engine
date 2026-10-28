package com.engine.repository;

import com.engine.model.WebhookDeliveryAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WebhookDeliveryAttemptRepository extends JpaRepository<WebhookDeliveryAttempt, Long> {
    List<WebhookDeliveryAttempt> findBySubscriptionId(Long subscriptionId);
}
