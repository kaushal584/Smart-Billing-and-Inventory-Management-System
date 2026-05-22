package com.billing.system.repository;

import com.billing.system.model.Invoice;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryInvoiceRepository implements Repository<Invoice> {
    private final Map<String, Invoice> storage = new HashMap<>();

    @Override
    public void save(Invoice entity) {
        storage.put(entity.getId(), entity);
    }

    @Override
    public Optional<Invoice> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Invoice> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void delete(String id) {
        storage.remove(id);
    }
}
