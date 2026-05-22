package com.billing.system.repository;

import com.billing.system.model.Customer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryCustomerRepository implements Repository<Customer> {
    private final Map<String, Customer> storage = new HashMap<>();

    @Override
    public void save(Customer entity) {
        storage.put(entity.getId(), entity);
    }

    @Override
    public Optional<Customer> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Customer> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void delete(String id) {
        storage.remove(id);
    }
}
