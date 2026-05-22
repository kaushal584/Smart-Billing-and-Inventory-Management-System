package com.billing.system.model;

import java.math.BigDecimal;
import java.util.Objects;

public class Product {
    private String id;
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private int quantity;
    private int soldQuantity;

    public Product(String id, String name, String description, BigDecimal price, String category, int quantity,
            int soldQuantity) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.quantity = quantity;
        this.soldQuantity = soldQuantity;
    }

    public Product(String id, String name, String description, BigDecimal price, String category, int quantity) {
        this(id, name, description, price, category, quantity, 0);
    }

    // Constructor for backward compatibility (defaults quantity to 0)
    public Product(String id, String name, String description, BigDecimal price, String category) {
        this(id, name, description, price, category, 0, 0);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getSoldQuantity() {
        return soldQuantity;
    }

    public void setSoldQuantity(int soldQuantity) {
        this.soldQuantity = soldQuantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return name + " (Rs " + price + ") [Stock: " + quantity + "]";
    }
}
