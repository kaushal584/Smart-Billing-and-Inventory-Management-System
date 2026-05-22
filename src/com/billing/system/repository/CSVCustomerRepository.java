package com.billing.system.repository;

import com.billing.system.model.Customer;

public class CSVCustomerRepository extends CSVRepository<Customer> {

    public CSVCustomerRepository() {
        super("customers.csv");
    }

    @Override
    protected Customer parseLine(String line) {
        // Format: id,name,email,phone,type
        String[] parts = line.split(",");
        if (parts.length < 5) {
            throw new IllegalArgumentException("Invalid customer line");
        }

        // Handle legacy data (6 columns) by ignoring the state column (index 4)
        if (parts.length == 6) {
            return new Customer(
                    parts[0],
                    parts[1],
                    parts[2],
                    parts[3],
                    Customer.CustomerType.valueOf(parts[5]));
        }

        return new Customer(
                parts[0],
                parts[1],
                parts[2],
                parts[3],
                Customer.CustomerType.valueOf(parts[4]));
    }

    @Override
    protected String toCSV(Customer c) {
        return String.join(",",
                c.getId(),
                c.getName(),
                c.getEmail(),
                c.getPhone(),
                c.getType().name());
    }

    @Override
    protected String getId(Customer c) {
        return c.getId();
    }
}
