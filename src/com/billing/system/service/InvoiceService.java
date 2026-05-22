package com.billing.system.service;

import com.billing.system.model.Customer;
import com.billing.system.model.Invoice;
import com.billing.system.model.InvoiceItem;
import com.billing.system.model.Product;
import com.billing.system.repository.Repository;
import com.billing.system.strategy.DiscountStrategy;
import com.billing.system.strategy.InvoiceExporter;
import com.billing.system.strategy.PDFInvoiceExporter;
import com.billing.system.strategy.TaxStrategy;

public class InvoiceService {
    private final Repository<Invoice> invoiceRepository;
    private final Repository<Product> productRepository;
    private final InvoiceCalculator calculator;
    private final InvoiceExporter exporter;
    private final PDFInvoiceExporter pdfExporter;

    public InvoiceService(Repository<Invoice> invoiceRepository,
            Repository<Product> productRepository,
            InvoiceCalculator calculator,
            InvoiceExporter exporter,
            PDFInvoiceExporter pdfExporter) {
        this.invoiceRepository = invoiceRepository;
        this.productRepository = productRepository;
        this.calculator = calculator;
        this.exporter = exporter;
        this.pdfExporter = pdfExporter;
    }

    public Invoice createInvoice(Customer customer) {
        return new Invoice(customer);
    }

    public void addProduct(Invoice invoice, Product product, int quantity) {
        invoice.addItem(new InvoiceItem(product, quantity));
    }

    public void generateInvoice(Invoice invoice, DiscountStrategy discountStrategy, TaxStrategy taxStrategy) {
        calculator.calculate(invoice, discountStrategy, taxStrategy);
        invoiceRepository.save(invoice);

        // Deduct stock
        // Deduct stock and update sold quantity
        for (InvoiceItem item : invoice.getItems()) {
            Product product = item.getProduct();
            int newQuantity = product.getQuantity() - item.getQuantity();
            // Ensure quantity doesn't go negative (though UI should prevent this)
            if (newQuantity < 0)
                newQuantity = 0;
            product.setQuantity(newQuantity);
            product.setSoldQuantity(product.getSoldQuantity() + item.getQuantity());
            productRepository.save(product);
        }
    }

    public String exportInvoice(Invoice invoice) {
        return exporter.export(invoice);
    }

    public java.util.List<Invoice> getAllInvoices() {
        java.util.List<Invoice> invoices = new java.util.ArrayList<>(invoiceRepository.findAll());
        invoices.sort((i1, i2) -> i2.getDate().compareTo(i1.getDate()));
        return invoices;
    }

    private static final String PDF_DIRECTORY = "generated_invoices";

    public void exportInvoiceToPdf(Invoice invoice, String filePath) throws Exception {
        pdfExporter.exportToPdf(invoice, filePath);
    }

    public void generateAndOpenPdf(Invoice invoice) throws Exception {
        java.io.File directory = new java.io.File(PDF_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String fileName = "Invoice_" + invoice.getId() + ".pdf";
        java.io.File pdfFile = new java.io.File(directory, fileName);
        String filePath = pdfFile.getAbsolutePath();

        exportInvoiceToPdf(invoice, filePath);

        if (java.awt.Desktop.isDesktopSupported()) {
            java.awt.Desktop.getDesktop().open(pdfFile);
        }
    }

    public void openInvoicePdf(String invoiceId) throws Exception {
        java.io.File pdfFile = new java.io.File(PDF_DIRECTORY, "Invoice_" + invoiceId + ".pdf");
        if (pdfFile.exists() && java.awt.Desktop.isDesktopSupported()) {
            java.awt.Desktop.getDesktop().open(pdfFile);
        } else {
            throw new java.io.FileNotFoundException("PDF not found for Invoice ID: " + invoiceId);
        }
    }
}
