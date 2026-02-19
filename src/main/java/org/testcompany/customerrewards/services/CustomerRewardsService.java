package org.testcompany.customerrewards.services;

import org.testcompany.customerrewards.domain.CustomerRewardsDetails;
import org.testcompany.customerrewards.domain.RewardsPeriodType;

public interface CustomerRewardsService {
    CustomerRewardsDetails calculateCustomerRewardsPoints(Long customerId,
                                                          RewardsPeriodType rewardsPeriodType,
                                                          Integer rewardsPeriod);
}
