package org.testcompany.customerrewards.domain;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

public class CustomerRewardsDetails {
    private Long customerId;
    private String rewardsDesc;
    private RewardsPeriodType rewardsPeriodType;
    private List<MonthlyPoints> monthlyPointsList = Collections.emptyList();
    private BigDecimal totalAmount = BigDecimal.ZERO;

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getRewardsDesc() {
        return rewardsDesc;
    }

    public void setRewardsDesc(String rewardsDesc) {
        this.rewardsDesc = rewardsDesc;
    }

    public RewardsPeriodType getRewardsPeriodType() {
        return rewardsPeriodType;
    }

    public void setRewardsPeriodType(RewardsPeriodType rewardsPeriodType) {
        this.rewardsPeriodType = rewardsPeriodType;
    }

    public List<MonthlyPoints> getMonthlyPointsList() {
        return monthlyPointsList;
    }

    public void setMonthlyPointsList(List<MonthlyPoints> monthlyPointsList) {
        this.monthlyPointsList = monthlyPointsList;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Integer getTotalPoints() {
        if (monthlyPointsList == null) {
            return 0;
        }
        return monthlyPointsList.stream().mapToInt(MonthlyPoints::getPoints).sum();
    }
}
