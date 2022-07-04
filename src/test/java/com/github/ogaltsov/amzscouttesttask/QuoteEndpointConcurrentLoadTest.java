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
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

@ActiveProfiles("concurrency")
class QuoteEndpointConcurrentLoadTest extends BaseRestQuotingTest {

	@Test
	@DisplayName("Generates concurrent requests. The count of request is bigger the available quota.")
	void test() throws InterruptedException, ExecutionException {

		ExecutorService executor = Executors.newFixedThreadPool(10);

		Request request = new Request.Builder()
			.url(String.format(LOCAL_SERVICE_BASE_URL, port))
			.addHeader(USER_IP_HEADER, RandomStringUtil.getRandomString())
			.build();

		List<Callable<Response>> taskList = IntStream.range(0, 50)
			.mapToObj(it -> (Callable<Response>) () -> client.newCall(request).execute())
			.toList();

		int successRequestCount = 0;
		int outOfQuotaRequestCount = 0;

		for (Future<Response> responseFuture : executor.invokeAll(taskList)) {
			switch (HttpStatus.resolve(responseFuture.get().code())) {
				case OK -> successRequestCount++;
				case BAD_GATEWAY -> outOfQuotaRequestCount++;
			}
		}

		Assertions.assertEquals(30, successRequestCount);
		Assertions.assertEquals(20, outOfQuotaRequestCount);
	}
}
