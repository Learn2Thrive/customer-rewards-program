package org.testcompany.customerrewards.controller;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import org.testcompany.customerrewards.converter.CustomerRewardsConverter;
import org.testcompany.customerrewards.domain.RewardsPeriodType;
import org.testcompany.customerrewards.dto.GetCustomerRewardsPointsResponse;
import org.testcompany.customerrewards.services.CustomerRewardsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import java.util.Optional;


@RestController
@Validated
@RequestMapping("/customer-rewards")
public class CustomerRewardsController {
    private static final Logger logger =
            LoggerFactory.getLogger(CustomerRewardsController.class);
    private final CustomerRewardsService customerRewardsService;
    private final CustomerRewardsConverter customerRewardsConverter;

    public CustomerRewardsController(
            CustomerRewardsService customerRewardsService, CustomerRewardsConverter customerRewardsConverter) {
        this.customerRewardsService = customerRewardsService;
        this.customerRewardsConverter = customerRewardsConverter;
    }

    @GetMapping("/points/{customerId}")
    public ResponseEntity<GetCustomerRewardsPointsResponse> getCustomerRewardsPoints(
            @PathVariable
            Long customerId,
            @RequestParam(value = "rewardsPeriodType", required = false)
            RewardsPeriodType rewardsPeriodType,
            @RequestParam(value = "rewardsPeriod", defaultValue = "3")
            @Min(value = 1, message = "Minimum rewards period required is 1")
            @Max(value = 240, message = "Maximum rewards period supported is 240")
            Integer rewardsPeriod) {
        logger.info("Calculating customer rewards points for customer id: {}", customerId);
        rewardsPeriodType = Optional.ofNullable(rewardsPeriodType)
                .orElse(RewardsPeriodType.MONTH);
        var customerRewardsDetails =
                customerRewardsService.calculateCustomerRewardsPoints(
                        customerId,
                        rewardsPeriodType,
                        rewardsPeriod);
        var customerRewardsPointsResponse =
                customerRewardsConverter.convert(customerRewardsDetails);
        return ResponseEntity.ok(customerRewardsPointsResponse);
    }
}
