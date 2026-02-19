package org.testcompany.customerrewards.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.testcompany.customerrewards.domain.PurchaseOrder;

import java.util.List;


@Repository
public interface OrderRepository extends CrudRepository<PurchaseOrder, Long> {
    List<PurchaseOrder> getOrdersByCustomerId(Long customerId);
}

