package com.github.ogaltsov.amzscouttesttask.component.quoting;

public interface UserQuotingService {

    boolean isQuoteEnabled(String userIp, String resource);
    void cleanOutdatedData();

}
