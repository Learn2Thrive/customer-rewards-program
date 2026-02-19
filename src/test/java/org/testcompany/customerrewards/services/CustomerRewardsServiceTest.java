package org.testcompany.customerrewards.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.testcompany.customerrewards.domain.Customer;
import org.testcompany.customerrewards.domain.PurchaseOrder;
import org.testcompany.customerrewards.domain.RewardsPeriodType;
import org.testcompany.customerrewards.exceptions.CustomerRewardsValidationException;
import org.testcompany.customerrewards.repository.CustomerRepository;
import org.testcompany.customerrewards.repository.OrderRepository;

import java.math.BigDecimal;
import java.time.InstantSource;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;

@SpringJUnitConfig
public class CustomerRewardsServiceTest {

    @MockitoBean
    private OrderRepository orderRepository;
    @MockitoBean
    private CustomerRepository customerRepository;
    private CustomerRewardsService customerRewardsService;

    @BeforeEach
    public void setup() {
        customerRewardsService = new CustomerRewardsServiceImpl(orderRepository, customerRepository);
    }

    @Test
    public void calculateMonthlyPoints() {
        var customer = new Customer(1L, "test", "1000000001");
        var orders = List.of(new PurchaseOrder(1L, customer,
                BigDecimal.valueOf(100.0), InstantSource.system().instant().minus(32, ChronoUnit.DAYS)));
        Mockito.when(orderRepository.getOrdersByCustomerId(1L)).thenReturn(orders);
        Mockito.when(customerRepository.getCustomerById(1L)).thenReturn(Optional.of(customer));

        var customerRewardsDetails = customerRewardsService.calculateCustomerRewardsPoints(1L,
                RewardsPeriodType.MONTH, 3);

        Mockito.verify(customerRepository, Mockito.times(1)).getCustomerById(anyLong());
        Mockito.verify(orderRepository, Mockito.times(1)).getOrdersByCustomerId(anyLong());

        Assertions.assertNotNull(customerRewardsDetails);
        Assertions.assertEquals(1L, customerRewardsDetails.getCustomerId(),
                "Customer Id is incorrect");
        Assertions.assertNotNull(customerRewardsDetails.getRewardsDesc(),
                "Customer rewards description should not be null");
        Assertions.assertNotNull(customerRewardsDetails.getRewardsPeriodType(),
                "Customer rewards period type should not be null");
        Assertions.assertNotNull(customerRewardsDetails.getMonthlyPointsList(),
                "Customer rewards monthly points list should not be null");
        Assertions.assertEquals(3, customerRewardsDetails
                        .getMonthlyPointsList().size(),
                "Customer rewards monthly points list size is incorrect");
        Assertions.assertEquals(50, customerRewardsDetails.getTotalPoints(),
                "Total rewards points is incorrect");
        Assertions.assertEquals(BigDecimal.valueOf(100.0), customerRewardsDetails.getTotalAmount(),
                "Total amount is incorrect");
    }

    @Test
    public void calculateMonthPointsWithInvalidRewardsPeriodType() {
        Assertions.assertThrows(CustomerRewardsValidationException.class, () -> {
            customerRewardsService.calculateCustomerRewardsPoints(1L,
                    RewardsPeriodType.UNKNOWN, 3);
        });
    }

    @Test
    public void calculateMonthPointsWithoutValidCustomer() {
        Assertions.assertThrows(CustomerRewardsValidationException.class, () -> {
            customerRewardsService.calculateCustomerRewardsPoints(1L,
                    RewardsPeriodType.MONTH, 3);
        });
    }
}
