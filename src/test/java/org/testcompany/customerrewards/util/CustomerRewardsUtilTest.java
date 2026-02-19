package org.testcompany.customerrewards.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcompany.customerrewards.domain.Customer;
import org.testcompany.customerrewards.domain.CustomerRewardsDetails;
import org.testcompany.customerrewards.domain.PurchaseOrder;

import java.math.BigDecimal;
import java.time.InstantSource;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class CustomerRewardsUtilTest {

    @Test
    public void calculatePointsForLast3Months() {
        List<PurchaseOrder> orders = getOrdersByCustomerId(1L);
        var customerRewardsDetails =
                CustomerRewardsUtil.calculateMonthlyCustomerRewardsPoints(1L, orders, 3);
        assertCustomerRewardsDetails(customerRewardsDetails, 1L, 520,
                BigDecimal.valueOf(460.0), 3);
    }

    @Test
    public void calculatePointsForLastMonth() {
        List<PurchaseOrder> orders = getOrdersByCustomerId(1L);
        var customerRewardsDetails =
                CustomerRewardsUtil.calculateMonthlyCustomerRewardsPoints(1L, orders, 1);
        assertCustomerRewardsDetails(customerRewardsDetails, 1L, 450,
                BigDecimal.valueOf(300.0), 1);
    }

    @Test
    public void calculatePointsWithTransactionAmounts() {
        var orderAmount = BigDecimal.valueOf(49.99);
        List<PurchaseOrder> orders = buildOrdersInLastMonth(1L, orderAmount);
        var customerRewardsDetails =
                CustomerRewardsUtil.calculateMonthlyCustomerRewardsPoints(1L, orders, 1);
        assertCustomerRewardsDetails(customerRewardsDetails, 1L, 0,
                orderAmount, 1);

        orderAmount =  BigDecimal.valueOf(50.0);
        orders = buildOrdersInLastMonth(1L, orderAmount);
        customerRewardsDetails =
                CustomerRewardsUtil.calculateMonthlyCustomerRewardsPoints(1L, orders, 1);
        assertCustomerRewardsDetails(customerRewardsDetails, 1L, 0,
                orderAmount, 1);

        orderAmount = BigDecimal.valueOf(50.01);
        orders = buildOrdersInLastMonth(1L, orderAmount);
        customerRewardsDetails =
                CustomerRewardsUtil.calculateMonthlyCustomerRewardsPoints(1L, orders, 1);
        assertCustomerRewardsDetails(customerRewardsDetails, 1L, 0,
                orderAmount, 1);

        orderAmount = BigDecimal.valueOf(75);
        orders = buildOrdersInLastMonth(1L, orderAmount);
        customerRewardsDetails =
                CustomerRewardsUtil.calculateMonthlyCustomerRewardsPoints(1L, orders, 1);
        assertCustomerRewardsDetails(customerRewardsDetails, 1L, 25,
                orderAmount, 1);

        orderAmount = BigDecimal.valueOf(100);
        orders = buildOrdersInLastMonth(1L, orderAmount);
        customerRewardsDetails =
                CustomerRewardsUtil.calculateMonthlyCustomerRewardsPoints(1L, orders, 1);
        assertCustomerRewardsDetails(customerRewardsDetails, 1L, 50,
                orderAmount, 1);

        orderAmount = BigDecimal.valueOf(100.01);
        orders = buildOrdersInLastMonth(1L, orderAmount);
        customerRewardsDetails =
                CustomerRewardsUtil.calculateMonthlyCustomerRewardsPoints(1L, orders, 1);
        assertCustomerRewardsDetails(customerRewardsDetails, 1L, 50,
                orderAmount, 1);

        orderAmount = BigDecimal.valueOf(120);
        orders = buildOrdersInLastMonth(1L, orderAmount);
        customerRewardsDetails =
                CustomerRewardsUtil.calculateMonthlyCustomerRewardsPoints(1L, orders, 1);
        assertCustomerRewardsDetails(customerRewardsDetails, 1L, 90,
                orderAmount, 1);
    }

    @Test
    public void calculatePointsWithoutOrders() {
        List<PurchaseOrder> orders = getOrdersByCustomerId(4L);
        var customerRewardsDetails =
                CustomerRewardsUtil.calculateMonthlyCustomerRewardsPoints(4L, orders, 3);
        assertCustomerRewardsDetails(customerRewardsDetails, 4L, 0,
                BigDecimal.ZERO, 3);
    }

    private void assertCustomerRewardsDetails(CustomerRewardsDetails customerRewardsDetails,
                                              Long customerId, int totalPoints, BigDecimal totalAmount,
                                              int monthlyPointsListSize) {
        Assertions.assertNotNull(customerRewardsDetails);
        Assertions.assertEquals(customerId, customerRewardsDetails.getCustomerId(),
                "Customer Id is incorrect");
        Assertions.assertNotNull(customerRewardsDetails.getRewardsDesc(),
                "Customer rewards description should not be null");
        Assertions.assertNotNull(customerRewardsDetails.getRewardsPeriodType(),
                "Customer rewards period type should not be null");
        Assertions.assertNotNull(customerRewardsDetails.getMonthlyPointsList(),
                "Customer rewards monthly points list should not be null");
        Assertions.assertEquals(monthlyPointsListSize, customerRewardsDetails
                        .getMonthlyPointsList().size(),
                "Customer rewards monthly points list size is incorrect");
        Assertions.assertEquals(totalPoints, customerRewardsDetails.getTotalPoints(),
                "Total rewards points is incorrect");
        Assertions.assertEquals(totalAmount, customerRewardsDetails.getTotalAmount(),
                "Total amount is incorrect");
    }

    private List<PurchaseOrder> getOrdersByCustomerId(Long customerId) {
        return getOrders().stream()
                .filter(purchaseOrder ->
                        purchaseOrder.getCustomer().getId().equals(customerId))
                .toList();
    }

    private List<PurchaseOrder> getOrders() {
        var customers = buildCustomers();
        var currentDate = InstantSource.system().instant();
        var customer1 = getCustomerById(1L, customers);

        return List.of(
                new PurchaseOrder(1L, customer1,
                        BigDecimal.valueOf(100.0), currentDate.minus(200, ChronoUnit.DAYS)),
                new PurchaseOrder(2L, customer1,
                        BigDecimal.valueOf(110.0), currentDate.minus(60, ChronoUnit.DAYS)),
                new PurchaseOrder(3L, customer1,
                        BigDecimal.valueOf(300.0), currentDate.minus(35, ChronoUnit.DAYS)),
                new PurchaseOrder(4L, customer1,
                        BigDecimal.valueOf(50.0), currentDate.minus(50, ChronoUnit.DAYS)),
                new PurchaseOrder(5L, customer1,
                        BigDecimal.valueOf(150.0), currentDate.minus(2, ChronoUnit.DAYS)),
                new PurchaseOrder(6L, customer1,
                        BigDecimal.valueOf(350.0), currentDate.minus(300, ChronoUnit.DAYS)),
                new PurchaseOrder(7L, getCustomerById(2L, customers),
                        BigDecimal.valueOf(75.0), currentDate.minus(56, ChronoUnit.DAYS)),
                new PurchaseOrder(8L, getCustomerById(3L, customers),
                        BigDecimal.valueOf(150.0), currentDate.minus(56, ChronoUnit.DAYS)));
    }

    private List<PurchaseOrder> buildOrdersInLastMonth(Long customerId, BigDecimal transAmount) {
        var customer = getCustomerById(customerId, buildCustomers());
        var lastMonthDate = LocalDate.now().minusMonths(1)
                .atStartOfDay().toInstant(ZoneOffset.UTC);
        return List.of(
                new PurchaseOrder(1L, customer,
                        transAmount, lastMonthDate));

    }

    private List<Customer> buildCustomers() {
        return List.of(
                new Customer(1L, "Test Name 1", "100-000-0001"),
                new Customer(2L, "Test Name 2", "100-000-0002"),
                new Customer(3L, "Test Name 3", "100-000-0003"),
                new Customer(4L, "Test Name 4", "100-000-0004")
        );
    }

    private Customer getCustomerById(Long customerId, List<Customer> customers) {
        return customers.stream()
                .filter(customer -> customer.getId().equals(customerId))
                .findFirst().orElse(null);
    }
}
