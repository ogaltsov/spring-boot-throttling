package com.github.ogaltsov.amzscouttesttask.component.quoting;

import com.github.ogaltsov.amzscouttesttask.component.MutexFactory;
import com.github.ogaltsov.amzscouttesttask.configuration.properties.QuoteLimitConfig;
import com.github.ogaltsov.amzscouttesttask.model.TimeSeries;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class InMemoryUserQuotingService implements UserQuotingService {

    private static final int CLEAN_UP_DELAY_IN_MINUTES = 10;

    private final QuoteLimitConfig quoteLimitConfig;
    private final MutexFactory<String> mutexFactory;

    private final Map<String, TimeSeries> identityToTimeSeries = new HashMap<>();


    @SneakyThrows
    public boolean isQuoteEnabled(String userIdentity, String resource) {

        synchronized ( mutexFactory.getMutex(userIdentity + resource) ) {

            final var userRequestTimeSeries = identityToTimeSeries.computeIfAbsent(
                userIdentity + resource,
                it -> new TimeSeries()
            );
            final var quoteWindowStartTime = Instant.now()
                .minus( quoteLimitConfig.getTimeInMinutes(), ChronoUnit.MINUTES )
                .toEpochMilli();

            int requestCountInWindow = userRequestTimeSeries.getRequestCountAfterTime(quoteWindowStartTime);

            if ( requestCountInWindow < quoteLimitConfig.getRequestCount() ) {

                userRequestTimeSeries.addRequestAtTime(Instant.now().toEpochMilli());
                return true;
            } else return false;
        }
    }

    @Override
    @Scheduled(
        fixedDelay = CLEAN_UP_DELAY_IN_MINUTES,
        initialDelay = CLEAN_UP_DELAY_IN_MINUTES,
        timeUnit = TimeUnit.MINUTES
    )
    public void cleanOutdatedData() {

        for ( var entry: identityToTimeSeries.entrySet() ) {

            var outdatedSeriesCount = entry.getValue().getSeriesCountBeforeTime(Instant.now().toEpochMilli());
            var totalSeriesCount = entry.getValue().getSeriesCount();

            //remove user
            if (Objects.equals(outdatedSeriesCount, totalSeriesCount)) {

                synchronized ( mutexFactory.getMutex(entry.getKey()) ) {
                    identityToTimeSeries.remove(entry.getKey());
                }

            //remove user's outdated time-series
            } else if (outdatedSeriesCount > 0) {

                final var quoteWindowStartTime = Instant.now()
                    .minus( quoteLimitConfig.getTimeInMinutes(), ChronoUnit.MINUTES )
                    .toEpochMilli();

                synchronized ( mutexFactory.getMutex(entry.getKey()) ) {
                    entry.getValue().removeBeforeTime(quoteWindowStartTime);
                }
            }
        }
    }
}
