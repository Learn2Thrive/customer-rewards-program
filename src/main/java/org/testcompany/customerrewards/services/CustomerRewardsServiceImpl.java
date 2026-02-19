package org.testcompany.customerrewards.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcompany.customerrewards.domain.CustomerRewardsDetails;
import org.testcompany.customerrewards.domain.RewardsPeriodType;
import org.testcompany.customerrewards.exceptions.CustomerRewardsValidationException;
import org.testcompany.customerrewards.repository.CustomerRepository;
import org.testcompany.customerrewards.repository.OrderRepository;
import org.testcompany.customerrewards.util.CustomerRewardsUtil;
import org.springframework.stereotype.Service;


@Service
public class CustomerRewardsServiceImpl implements CustomerRewardsService {
    private static final Logger logger = LoggerFactory.getLogger(
            CustomerRewardsServiceImpl.class);
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;

    public CustomerRewardsServiceImpl(OrderRepository orderRepository,
                                      CustomerRepository customerRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public CustomerRewardsDetails calculateCustomerRewardsPoints(Long customerId,
                                                                 RewardsPeriodType rewardsPeriodType,
                                                                 Integer rewardsPeriod) {
        if (rewardsPeriodType != RewardsPeriodType.MONTH) {
            throw new CustomerRewardsValidationException("Operation not supported");
        }
        var customerOptional = customerRepository.getCustomerById(customerId);
        if (customerOptional.isEmpty()) {
            throw new CustomerRewardsValidationException(String.format("Customer with " +
                            "id: %s not found", customerId));
        }
        var orders = orderRepository.getOrdersByCustomerId(customerId);

        return CustomerRewardsUtil.calculateMonthlyCustomerRewardsPoints(
                customerId, orders, rewardsPeriod);
    }
}
