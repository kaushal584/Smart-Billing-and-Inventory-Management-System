package com.billing.system.model;

import java.math.BigDecimal;

public class InvoiceItem {
    private Product product;
    private int quantity;
    private BigDecimal unitPrice; // Snapshot of price at time of invoice

    public InvoiceItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = product.getPrice();
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public BigDecimal getTotalPrice() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    @Override
    public String toString() {
        return String.format("%s x %d @ %s = %s", product.getName(), quantity, unitPrice, getTotalPrice());
    }
}
