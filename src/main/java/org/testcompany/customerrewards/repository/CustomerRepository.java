package org.testcompany.customerrewards.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.testcompany.customerrewards.domain.Customer;

import java.util.Optional;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, Long> {
    Optional<Customer> getCustomerById(Long id);
}
