package org.testcompany.customerrewards.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcompany.customerrewards.converter.CustomerRewardsConverter;
import org.testcompany.customerrewards.domain.CustomerRewardsDetails;
import org.testcompany.customerrewards.domain.RewardsPeriodType;
import org.testcompany.customerrewards.dto.GetCustomerRewardsPointsResponse;
import org.testcompany.customerrewards.repository.CustomerRepository;
import org.testcompany.customerrewards.services.CustomerRewardsService;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerRewardsController.class)
public class CustomerRewardsControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private CustomerRewardsService customerRewardsService;
    @MockitoBean
    private CustomerRewardsConverter customerRewardsConverter;
    @MockitoBean
    private CustomerRepository customerRepository;

    @Test
    public void getCustomerRewardsPoints() throws Exception {
        Mockito.when(customerRewardsService.calculateCustomerRewardsPoints(
                        1L, RewardsPeriodType.MONTH, 1))
                .thenReturn(Mockito.mock(CustomerRewardsDetails.class));
        var mockResponse = buildMockResponse();
        Mockito.when(customerRewardsConverter.convert(any(CustomerRewardsDetails.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(get("/customer-rewards/points/1")
                        .queryParam("rewardsPeriodType", RewardsPeriodType.MONTH.name())
                        .queryParam("rewardsPeriod", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(mockResponse.customerId()))
                .andExpect(jsonPath("$.personalInfo.customerName").value(
                        mockResponse.personalInfo().customerName()))
                .andExpect(jsonPath("$.personalInfo.phoneNumber").value(
                        mockResponse.personalInfo().phoneNumber()))
                .andExpect(jsonPath("$.rewards.description").value(
                        mockResponse.rewards().description()))
                .andExpect(jsonPath("$.rewards.rewardsPeriodType").value(
                        mockResponse.rewards().rewardsPeriodType().name()))
                .andExpect(jsonPath("$.rewards.monthlyPointsList.length()").value(
                        mockResponse.rewards().monthlyPointsList().size()))
                .andExpect(jsonPath("$.rewards.totalPoints").value(
                        mockResponse.rewards().totalPoints()))
                .andExpect(jsonPath("$.rewards.totalAmount").value(
                        mockResponse.rewards().totalAmount()));
    }

    @Test
    public void getCustomerRewardsPointsWithInvalidCustomerId() throws Exception {
        mockMvc.perform(get("/customer-rewards/points/null")
                        .queryParam("rewardsPeriodType", RewardsPeriodType.MONTH.name())
                        .queryParam("rewardsPeriod", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getCustomerRewardsPointsWithInvalidRewardsPeriodType() throws Exception {
        mockMvc.perform(get("/customer-rewards/points/1")
                        .queryParam("rewardsPeriodType", "DAYS")
                        .queryParam("rewardsPeriod", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getCustomerRewardsPointsWithoutRewardsPeriodType() throws Exception {
        mockMvc.perform(get("/customer-rewards/points/1")
                        .queryParam("rewardsPeriod", "1"))
                .andExpect(status().isOk());
    }

    @Test
    public void getCustomerRewardsPointsWithoutRewardsPeriod() throws Exception {
        mockMvc.perform(get("/customer-rewards/points/1")
                        .queryParam("rewardsPeriodType", RewardsPeriodType.MONTH.name()))
                .andExpect(status().isOk());
    }

    @Test
    public void getCustomerRewardsPointsWithoutRewardsPeriodTypeAndPeriod() throws Exception {
        mockMvc.perform(get("/customer-rewards/points/1"))
                .andExpect(status().isOk());
    }

    private GetCustomerRewardsPointsResponse buildMockResponse() {
        var customerRewardsPointsResponse = Mockito.mock(
                GetCustomerRewardsPointsResponse.class);
        Mockito.when(customerRewardsPointsResponse.customerId()).thenReturn(1L);

        var customerPersonalInfo = Mockito.mock(
                GetCustomerRewardsPointsResponse.CustomerPersonalInfo.class);
        Mockito.when(customerPersonalInfo.customerName()).thenReturn("test");
        Mockito.when(customerPersonalInfo.phoneNumber()).thenReturn("100-000-0001");
        Mockito.when(customerRewardsPointsResponse.personalInfo()).thenReturn(
                customerPersonalInfo);

        var customerRewards = Mockito.mock(
                GetCustomerRewardsPointsResponse.CustomerRewards.class);
        Mockito.when(customerRewards.description()).thenReturn("test");
        Mockito.when(customerRewards.rewardsPeriodType())
                .thenReturn(RewardsPeriodType.MONTH);
        Mockito.when(customerRewards.totalPoints()).thenReturn(50);
        Mockito.when(customerRewards.totalAmount()).thenReturn(
                BigDecimal.valueOf(100));
        var currentDate = Calendar.getInstance();
        var monthlyPoints = Mockito.mock(
                GetCustomerRewardsPointsResponse.CustomerRewards.MonthlyPoints.class);
        Mockito.when(monthlyPoints.points()).thenReturn(50);
        Mockito.when(monthlyPoints.month()).thenReturn(currentDate.get(Calendar.MONTH) - 1);
        Mockito.when(monthlyPoints.year()).thenReturn(currentDate.get(Calendar.YEAR));
        Mockito.when(customerRewards.monthlyPointsList()).thenReturn(
                List.of(monthlyPoints));
        Mockito.when(customerRewardsPointsResponse.rewards()).thenReturn(customerRewards);
        return customerRewardsPointsResponse;
    }
}
