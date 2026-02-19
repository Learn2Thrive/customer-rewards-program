package org.testcompany.customerrewards.dto;

import org.testcompany.customerrewards.domain.RewardsPeriodType;

import java.math.BigDecimal;
import java.util.List;

public record GetCustomerRewardsPointsResponse(Long customerId,
                                               CustomerPersonalInfo personalInfo,
                                               CustomerRewards rewards) {
    public record CustomerPersonalInfo(String customerName, String phoneNumber){}
    public record CustomerRewards(String description,
                                  RewardsPeriodType rewardsPeriodType,
                                  List<MonthlyPoints> monthlyPointsList,
                                  Integer totalPoints,
                                  BigDecimal totalAmount) {
        public record MonthlyPoints(int points, int month, int year) {}
    }
}
