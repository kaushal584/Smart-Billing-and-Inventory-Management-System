package com.billing.system.repository;

import com.billing.system.model.Product;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryProductRepository implements Repository<Product> {
    private final Map<String, Product> storage = new HashMap<>();

    @Override
    public void save(Product entity) {
        storage.put(entity.getId(), entity);
    }

    @Override
    public Optional<Product> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void delete(String id) {
        storage.remove(id);
    }
}
