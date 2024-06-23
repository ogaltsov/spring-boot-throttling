package com.github.ogaltsov.amzscouttesttask.component.quoting;

import com.github.ogaltsov.amzscouttesttask.component.MutexFactory;
import com.github.ogaltsov.amzscouttesttask.configuration.properties.QuoteLimitConfig;
import com.github.ogaltsov.amzscouttesttask.exception.UserRequestOutOfQuotaException;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import org.apache.commons.collections4.QueueUtils;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

@Service
@RequiredArgsConstructor
public class UserQuotingService {

    private final QuoteLimitConfig quoteLimitConfig;

    private final MutexFactory<QuoteKey> mutexFactory;

    private final Map<QuoteKey, Queue<Long>> identityToTimeSeries = new HashMap<>();

    @SneakyThrows
    public void validateQuota(String ip, String resource) {

        QuoteKey quoteKey = new QuoteKey(ip, resource);
        synchronized (mutexFactory.getMutex(quoteKey)) {

            final var timeSeries = identityToTimeSeries.computeIfAbsent(quoteKey,
                    it -> QueueUtils.synchronizedQueue(
                            new CircularFifoQueue<>(quoteLimitConfig.getRequestCount())));

            boolean isAllowed = timeSeries.isEmpty()
                    || timeSeries.size() < quoteLimitConfig.getRequestCount()
                    || timeSeries.peek() < Instant.now()
                            .minus(Duration.ofMinutes(quoteLimitConfig.getTimeInMinutes()))
                            .toEpochMilli();

            timeSeries.add(Instant.now().toEpochMilli());

            if (!isAllowed) {
                throw new UserRequestOutOfQuotaException();
            }
        }
    }
}
