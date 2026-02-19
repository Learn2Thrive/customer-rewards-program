package org.testcompany.customerrewards.converter;

import org.testcompany.customerrewards.domain.CustomerRewardsDetails;
import org.testcompany.customerrewards.dto.GetCustomerRewardsPointsResponse;
import org.testcompany.customerrewards.repository.CustomerRepository;
import org.springframework.stereotype.Component;

@Component
public class CustomerRewardsConverter {

    private final CustomerRepository customerRepository;

    public CustomerRewardsConverter(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public GetCustomerRewardsPointsResponse convert(CustomerRewardsDetails customerRewardsDetails) {
        var monthlyPointsList =
                customerRewardsDetails.getMonthlyPointsList().stream()
                        .map(monthlyPoints ->
                                new GetCustomerRewardsPointsResponse.CustomerRewards.MonthlyPoints(
                                        monthlyPoints.getPoints(), monthlyPoints.getMonth(),
                                        monthlyPoints.getYear()))
                        .toList();
        var rewardsPointsDTO =
                new GetCustomerRewardsPointsResponse.CustomerRewards(customerRewardsDetails.getRewardsDesc(),
                        customerRewardsDetails.getRewardsPeriodType(),
                        monthlyPointsList, customerRewardsDetails.getTotalPoints(),
                        customerRewardsDetails.getTotalAmount());
        var customer = customerRepository.getCustomerById(customerRewardsDetails.getCustomerId())
                .orElseThrow();
        var customerPersonalInfo = new GetCustomerRewardsPointsResponse.CustomerPersonalInfo(
                customer.getName(), customer.getPhoneNumber());
        return new GetCustomerRewardsPointsResponse(customer.getId(), customerPersonalInfo, rewardsPointsDTO);
    }
}
