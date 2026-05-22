package com.billing.system.strategy;

import com.billing.system.model.Invoice;
import com.billing.system.model.InvoiceItem;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;

public class PDFInvoiceExporter {

    private static final Font HEADER_FONT = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
    private static final Font SUBHEADER_FONT = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
    private static final Font NORMAL_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
    private static final Font BOLD_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);

    public void exportToPdf(Invoice invoice, String filePath) throws Exception {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(filePath));

        document.open();

        // Header
        Paragraph header = new Paragraph("INVOICE", HEADER_FONT);
        header.setAlignment(Element.ALIGN_CENTER);
        header.setSpacingAfter(20);
        document.add(header);

        // Invoice Details
        PdfPTable detailsTable = new PdfPTable(2);
        detailsTable.setWidthPercentage(100);
        detailsTable.setSpacingAfter(20);

        // Customer Info
        PdfPCell customerCell = new PdfPCell();
        customerCell.setBorder(Rectangle.NO_BORDER);
        customerCell.addElement(new Paragraph("Bill To:", BOLD_FONT));
        customerCell.addElement(new Paragraph(invoice.getCustomer().getName(), NORMAL_FONT));
        customerCell.addElement(new Paragraph(invoice.getCustomer().getPhone(), NORMAL_FONT));
        customerCell.addElement(new Paragraph(invoice.getCustomer().getEmail(), NORMAL_FONT));
        detailsTable.addCell(customerCell);

        // Invoice Info
        PdfPCell invoiceInfoCell = new PdfPCell();
        invoiceInfoCell.setBorder(Rectangle.NO_BORDER);
        invoiceInfoCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        invoiceInfoCell.addElement(new Paragraph("Invoice ID: " + invoice.getId(), NORMAL_FONT));
        invoiceInfoCell.addElement(new Paragraph(
                "Date: " + invoice.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), NORMAL_FONT));
        invoiceInfoCell.addElement(new Paragraph("Status: " + invoice.getStatus(), NORMAL_FONT));
        detailsTable.addCell(invoiceInfoCell);

        document.add(detailsTable);

        // Items Table
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[] { 4, 2, 2, 2 });
        table.setSpacingBefore(10);
        table.setSpacingAfter(10);

        // Table Headers
        addTableHeader(table, "Item");
        addTableHeader(table, "Price");
        addTableHeader(table, "Qty");
        addTableHeader(table, "Total");

        // Table Rows
        for (InvoiceItem item : invoice.getItems()) {
            table.addCell(new Paragraph(item.getProduct().getName(), NORMAL_FONT));
            table.addCell(new Paragraph(String.format("Rs %.2f", item.getProduct().getPrice()), NORMAL_FONT));
            table.addCell(new Paragraph(String.valueOf(item.getQuantity()), NORMAL_FONT));
            table.addCell(new Paragraph(String.format("Rs %.2f", item.getTotalPrice()), NORMAL_FONT));
        }

        document.add(table);

        // Totals
        Paragraph totals = new Paragraph();
        totals.setAlignment(Element.ALIGN_RIGHT);
        totals.add(new Paragraph(
                String.format("Subtotal: Rs %.2f",
                        invoice.getTotalAmount().subtract(invoice.getTaxAmount()).add(invoice.getDiscountAmount())),
                NORMAL_FONT));
        totals.add(new Paragraph(String.format("Discount: -Rs %.2f", invoice.getDiscountAmount()), NORMAL_FONT));
        totals.add(new Paragraph(String.format("Tax: +Rs %.2f", invoice.getTaxAmount()), NORMAL_FONT));
        totals.add(new Paragraph(String.format("TOTAL: Rs %.2f", invoice.getTotalAmount()), BOLD_FONT));

        document.add(totals);

        // Footer
        Paragraph footer = new Paragraph("Thank you for your business!", SUBHEADER_FONT);
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(30);
        document.add(footer);

        document.close();
    }

    private void addTableHeader(PdfPTable table, String headerTitle) {
        PdfPCell header = new PdfPCell();
        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
        header.setBorderWidth(1);
        header.setPhrase(new Phrase(headerTitle, BOLD_FONT));
        header.setPadding(5);
        table.addCell(header);
    }
}
