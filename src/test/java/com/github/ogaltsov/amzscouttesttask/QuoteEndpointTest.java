package com.github.ogaltsov.amzscouttesttask;

import com.github.ogaltsov.amzscouttesttask.util.RandomStringUtil;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;

@ActiveProfiles("integration")
class QuoteEndpointTest extends BaseRestQuotingTest {

	@Test
	@DisplayName("User request reach the quota")
	void successQuotedRequest() throws IOException {

		// GIVEN
		Request request = new Request.Builder()
			.url(String.format(LOCAL_SERVICE_BASE_URL, port))
			.addHeader(USER_IP_HEADER, RandomStringUtil.getRandomString())
			.build();

		// WHEN
		Response response = client.newCall(request).execute();

		// THEN
		Assertions.assertEquals(HttpStatus.OK.value(), response.code());
	}


	@Test
	@DisplayName("User request goes out of quota")
	void outOfQuotaRequest() throws IOException {

		// GIVEN
		Request request = new Request.Builder()
			.url(String.format(LOCAL_SERVICE_BASE_URL, port))
			.addHeader(USER_IP_HEADER, RandomStringUtil.getRandomString())
			.build();

		// WHEN
		Response response1 = client.newCall(request).execute();
		Response response2 = client.newCall(request).execute();

		// THEN
		Assertions.assertEquals(HttpStatus.OK.value(), response1.code());
		Assertions.assertEquals(HttpStatus.BAD_GATEWAY.value(), response2.code());
	}
}
