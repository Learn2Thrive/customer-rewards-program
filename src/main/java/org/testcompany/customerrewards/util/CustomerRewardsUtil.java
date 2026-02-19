package org.testcompany.customerrewards.util;

import jakarta.annotation.Nonnull;
import org.testcompany.customerrewards.domain.CustomerRewardsDetails;
import org.testcompany.customerrewards.domain.MonthlyPoints;
import org.testcompany.customerrewards.domain.PurchaseOrder;
import org.testcompany.customerrewards.domain.RewardsPeriodType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Objects;
import java.util.LinkedHashMap;
import java.util.Collections;
import java.util.stream.Collectors;

public class CustomerRewardsUtil {

    private static final BigDecimal REWARDS_POINTS_HIGHER_THRESHOLD_AMOUNT = BigDecimal.valueOf(100.0);
    private static final BigDecimal REWARDS_POINTS_LOWER_THRESHOLD_AMOUNT = BigDecimal.valueOf(50);

    public static CustomerRewardsDetails calculateMonthlyCustomerRewardsPoints(@Nonnull Long customerId,
                                                                @Nonnull List<PurchaseOrder> orders,
                                                                @Nonnull Integer rewardsPeriodInMonths) {
        Objects.requireNonNull(customerId, "Customer Id must not be null");
        Objects.requireNonNull(orders, "Orders must not be null");
        Objects.requireNonNull(rewardsPeriodInMonths, "Rewards period must not be null");

        var currentDate = LocalDate.now();
        var rewardsPeriodMonth = currentDate.minus(Period.ofMonths(rewardsPeriodInMonths));
        var rewardsPeriodStartDate =
                rewardsPeriodMonth.minusDays(rewardsPeriodMonth.getDayOfMonth() -1);
        var rewardsPeriodEndDate = currentDate.minusDays(currentDate.getDayOfMonth());

        var ordersByRewardsPeriod = filterOrdersByRewardsPeriod(orders, rewardsPeriodStartDate,
                rewardsPeriodEndDate);
        var monthlyPoints = calculateMonthlyPoints(ordersByRewardsPeriod,
                rewardsPeriodStartDate, rewardsPeriodEndDate);

        var customerRewardsDetails = new CustomerRewardsDetails();
        customerRewardsDetails.setCustomerId(customerId);
        customerRewardsDetails.setRewardsDesc("Customer Rewards Points per month");
        customerRewardsDetails.setRewardsPeriodType(RewardsPeriodType.MONTH);
        customerRewardsDetails.setMonthlyPointsList(monthlyPoints);
        customerRewardsDetails.setTotalAmount(getTotalAmount(ordersByRewardsPeriod));
        return customerRewardsDetails;
    }

    private static List<MonthlyPoints> calculateMonthlyPoints(List<PurchaseOrder> orders,
                                                                  LocalDate rewardsPeriodStartDate,
                                                                  LocalDate rewardsPeriodEndDate) {
        // calculate points earned for each month
        Objects.requireNonNull(orders, "Orders must not be null");
        Objects.requireNonNull(rewardsPeriodStartDate, "Rewards period start date must not be null");
        Objects.requireNonNull(rewardsPeriodEndDate, "Rewards period end date must not be null");

        var monthlyPointsMap = buildInitialMonthlyPointsMap(
                rewardsPeriodStartDate, rewardsPeriodEndDate);
        if (orders.isEmpty()) {
            return monthlyPointsMap.values().stream().toList();
        }
        var ordersPerMonthMap = buildOrdersPerMonthMap(orders);

        var pointsMultiplier = BigDecimal.valueOf(2);
        for (var orderEntry: ordersPerMonthMap.entrySet()) {
            int pointsPerMonth = 0;
            for (PurchaseOrder order: orderEntry.getValue()) {
                var transAmount = order.getTransactionAmount();
                if (transAmount.compareTo(REWARDS_POINTS_HIGHER_THRESHOLD_AMOUNT) >= 0) {
                    pointsPerMonth += transAmount
                            .subtract(REWARDS_POINTS_HIGHER_THRESHOLD_AMOUNT)
                            .setScale(0, RoundingMode.DOWN)
                            .multiply(pointsMultiplier)
                            .add(REWARDS_POINTS_LOWER_THRESHOLD_AMOUNT).intValue();
                } else if (transAmount.compareTo(REWARDS_POINTS_LOWER_THRESHOLD_AMOUNT) > 0) {
                    pointsPerMonth += transAmount.subtract(REWARDS_POINTS_LOWER_THRESHOLD_AMOUNT).intValue();
                }
            }
            monthlyPointsMap.get(orderEntry.getKey()).setPoints(pointsPerMonth);
        }
        return monthlyPointsMap.values().stream().toList();
    }

    private static BigDecimal getTotalAmount(List<PurchaseOrder> orders) {
        if (orders == null || orders.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return orders.stream()
                .map(PurchaseOrder::getTransactionAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static List<PurchaseOrder> filterOrdersByRewardsPeriod(List<PurchaseOrder> orders,
                                                                   LocalDate rewardsPeriodStartDate,
                                                                   LocalDate rewardsPeriodEndDate) {
        Objects.requireNonNull(orders, "Orders must not be null");
        Objects.requireNonNull(rewardsPeriodStartDate, "Rewards period start date must not be null");
        Objects.requireNonNull(rewardsPeriodEndDate, "Rewards period end date must not be null");

        return orders.stream()
                .filter(order -> {
                    var transactionDate =
                            getTransactionLocalDate(order.getTransactionDate());
                    return (transactionDate.isAfter(rewardsPeriodStartDate)
                            || transactionDate.equals(rewardsPeriodStartDate)) &&
                            transactionDate.isBefore(rewardsPeriodEndDate);
                }).toList();
    }

    private static Map<LocalDate, List<PurchaseOrder>> buildOrdersPerMonthMap(List<PurchaseOrder> orders) {
        if (orders == null) {
            return Collections.emptyMap();
        }
        return orders.stream()
                .collect(Collectors.groupingBy(order -> {
                    var transLocalDate = getTransactionLocalDate(order.getTransactionDate());
                    return transLocalDate.minusDays(transLocalDate.getDayOfMonth()-1);
                }));
    }

    private static LocalDate getTransactionLocalDate(Instant transactionDate) {
        return Optional.ofNullable(transactionDate)
                .map(transDate -> LocalDate.ofInstant(transDate, ZoneOffset.UTC))
                .orElse(null);
    }

    private static Map<LocalDate, MonthlyPoints> buildInitialMonthlyPointsMap(LocalDate rewardsPeriodStartDate,
                                                                 LocalDate rewardsPeriodEndDate) {
        Objects.requireNonNull(rewardsPeriodStartDate, "Rewards period start date must not be null");
        Objects.requireNonNull(rewardsPeriodEndDate, "Rewards period end date must not be null");

        var monthlyPointsMap = new LinkedHashMap<LocalDate, MonthlyPoints>();
        while (rewardsPeriodStartDate.isBefore(rewardsPeriodEndDate)) {
            monthlyPointsMap.put(rewardsPeriodStartDate, new MonthlyPoints(0,
                    rewardsPeriodStartDate.getMonthValue(),
                    rewardsPeriodStartDate.getYear()));
            rewardsPeriodStartDate = rewardsPeriodStartDate.plusMonths(1);
        }
        return monthlyPointsMap;
    }
}
