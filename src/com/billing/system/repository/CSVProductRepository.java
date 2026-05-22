package com.billing.system.repository;

import com.billing.system.model.Product;
import java.math.BigDecimal;

public class CSVProductRepository extends CSVRepository<Product> {

    public CSVProductRepository() {
        super("products.csv");
    }

    @Override
    protected Product parseLine(String line) {
        // Format: id,name,description,price,category,quantity
        String[] parts = line.split(",");
        if (parts.length < 5)
            throw new IllegalArgumentException("Invalid product line");

        int quantity = 0;
        if (parts.length >= 6) {
            try {
                quantity = Integer.parseInt(parts[5]);
            } catch (NumberFormatException e) {
                quantity = 0; // Default to 0 if invalid
            }
        }

        int soldQuantity = 0;
        if (parts.length >= 7) {
            try {
                soldQuantity = Integer.parseInt(parts[6]);
            } catch (NumberFormatException e) {
                soldQuantity = 0; // Default to 0 if invalid
            }
        }

        return new Product(
                parts[0],
                parts[1],
                parts[2],
                new BigDecimal(parts[3]),
                parts[4],
                quantity,
                soldQuantity);
    }

    @Override
    protected String toCSV(Product p) {
        return String.join(",",
                p.getId(),
                p.getName(),
                p.getDescription(),
                p.getPrice().toString(),
                p.getCategory(),
                String.valueOf(p.getQuantity()),
                String.valueOf(p.getSoldQuantity()));
    }

    @Override
    protected String getId(Product p) {
        return p.getId();
    }
}
