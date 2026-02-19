package org.testcompany.customerrewards;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcompany.customerrewards.domain.RewardsPeriodType;
import org.testcompany.customerrewards.dto.Error;
import org.testcompany.customerrewards.dto.GetCustomerRewardsPointsResponse;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomerRewardsApiTests {
	@LocalServerPort
	private int port;
	private RestTestClient restTestClient;

	private static final String CALCULATE_CUSTOMER_REWARDS_POINTS_URI_PATH =
			"/customer-rewards/points/";

	@BeforeEach
	public void init() {
		restTestClient = RestTestClient.bindToServer()
				.baseUrl("http://localhost:".concat(String.valueOf(port))).build();
	}

	@Test
	public void getPointsForLast3Months() {
		var responseSpec = getCustomerRewardsPoints(1L,
				RewardsPeriodType.MONTH.name(), 3);
		var response = responseSpec
				.expectStatus().is2xxSuccessful()
				.expectBody(GetCustomerRewardsPointsResponse.class)
				.returnResult().getResponseBody();
		assertCustomerRewardsInfo(1L, response);
		Assertions.assertEquals(520, response.rewards().totalPoints(),
				"Total rewards points is incorrect");
		Assertions.assertEquals(3, response.rewards().monthlyPointsList().size(),
				"Customer rewards monthly points list size is incorrect");
	}

	@Test
	public void getPointsForLastMonth() {
		var responseSpec = getCustomerRewardsPoints(1L,
				RewardsPeriodType.MONTH.name(), 1);
		var response = responseSpec
				.expectStatus().is2xxSuccessful()
				.expectBody(GetCustomerRewardsPointsResponse.class)
				.returnResult().getResponseBody();
		assertCustomerRewardsInfo(1L, response);
		Assertions.assertEquals(450, response.rewards().totalPoints(),
				"Total rewards points is incorrect");
		Assertions.assertEquals(1, response.rewards().monthlyPointsList().size(),
				"Customer rewards monthly points list size is incorrect");
	}

	@Test
	public void getPointsForLast6Months() {
		var responseSpec = getCustomerRewardsPoints(1L,
				RewardsPeriodType.MONTH.name(), 6);
		var response = responseSpec
				.expectStatus().is2xxSuccessful()
				.expectBody(GetCustomerRewardsPointsResponse.class)
				.returnResult().getResponseBody();
		assertCustomerRewardsInfo(1L, response);
		Assertions.assertEquals(570, response.rewards().totalPoints(),
				"Total rewards points is incorrect");
		Assertions.assertEquals(6,
				response.rewards().monthlyPointsList().size(),
				"Customer rewards monthly points list size is incorrect");
	}

	@Test
	public void getPointsForLast11Months() {
		var responseSpec = getCustomerRewardsPoints(1L,
				RewardsPeriodType.MONTH.name(), 11);
		var response = responseSpec
				.expectStatus().is2xxSuccessful()
				.expectBody(GetCustomerRewardsPointsResponse.class)
				.returnResult().getResponseBody();
		assertCustomerRewardsInfo(1L, response);
		Assertions.assertEquals(1120, response.rewards().totalPoints(),
				"Total rewards points is incorrect");
		Assertions.assertEquals(11, response.rewards().monthlyPointsList().size(),
				"Customer rewards monthly points list size is incorrect");
	}

	@Test
	public void getPointsForCustomerWithoutTransactions() {
		var responseSpec = getCustomerRewardsPoints(4L,
				RewardsPeriodType.MONTH.name(), 11);
		var response = responseSpec
				.expectStatus().is2xxSuccessful()
				.expectBody(GetCustomerRewardsPointsResponse.class)
				.returnResult().getResponseBody();
		assertCustomerRewardsInfo(4L, response);
		Assertions.assertEquals(0, response.rewards().totalPoints(),
				"Total rewards points is incorrect");
		Assertions.assertEquals(11L, response.rewards().monthlyPointsList().size(),
				"Customer rewards monthly points list size is incorrect");
	}

	@Test
	public void getPointsForInvalidCustomer() {
		var responseSpec = getCustomerRewardsPoints(5L,
				RewardsPeriodType.MONTH.name(), 3);
		var response = responseSpec
				.expectStatus().isBadRequest()
				.expectBody(Error.class)
				.returnResult().getResponseBody();
		Assertions.assertNotNull(response, "response should not be null");
		Assertions.assertNotNull(response.errorMessage(),
				"Error message should not be null");
		Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(),
				response.status(),
				"Error http status is incorrect");
	}

	@Test
	public void getPointsWithRewardsTypeNotSupported() {
		var responseSpec = getCustomerRewardsPoints(1L,
				"DAYS", 3);
		var response = responseSpec
				.expectStatus().isBadRequest()
				.expectBody(Error.class)
				.returnResult().getResponseBody();
		Assertions.assertNotNull(response, "response should not be null");
		Assertions.assertNotNull(response.errorMessage(),
				"Error message should not be null");
		Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(),
				response.status(),
				"Error http status is incorrect");
	}

	private RestTestClient.ResponseSpec getCustomerRewardsPoints(Long customerId,
			String rewardsPeriodType,
			Integer rewardsPeriod) {
		return restTestClient.get()
				.uri(UriComponentsBuilder.fromPath(CALCULATE_CUSTOMER_REWARDS_POINTS_URI_PATH.concat(customerId.toString()))
						.queryParam("rewardsPeriodType", rewardsPeriodType)
						.queryParam("rewardsPeriod", rewardsPeriod)
						.build().toUri())
				.exchange();
	}

	private void assertCustomerRewardsInfo(Long customerId,
										   GetCustomerRewardsPointsResponse response) {
		Assertions.assertNotNull(response, "response should not be null");
		Assertions.assertNotNull(response.customerId(), "CustomerId should not be null");
		Assertions.assertEquals(customerId, response.customerId(),
				"Customer Id does not match");
		Assertions.assertNotNull(response.personalInfo(),
				"Customer personal info should not be null");
		Assertions.assertNotNull(response.personalInfo().customerName(),
				"Customer name should not be null");
		Assertions.assertNotNull(response.personalInfo().phoneNumber(),
				"Customer phone number should not be null");
		Assertions.assertNotNull(response.rewards(),
				"Customer rewards should not be null");
		Assertions.assertNotNull(response.rewards().description(),
				"Customer rewards description should not be null");
		Assertions.assertNotNull(response.rewards().rewardsPeriodType(),
				"Customer rewards period type should not be null");
		Assertions.assertNotNull(response.rewards().totalPoints(),
				"Customer rewards total points should not be null");
		Assertions.assertNotNull(response.rewards().monthlyPointsList(),
				"Customer rewards monthly points list should not be null");
	}
}
