package com.github.ogaltsov.amzscouttesttask;

import okhttp3.OkHttpClient;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
public class BaseRestQuotingTest {

    protected final OkHttpClient client = new OkHttpClient();

    protected static final String LOCAL_SERVICE_BASE_URL = "http://localhost:%s/quote";
    protected static final String USER_IP_HEADER = "REMOTE_ADDR";

    @LocalServerPort
    protected int port;
}
