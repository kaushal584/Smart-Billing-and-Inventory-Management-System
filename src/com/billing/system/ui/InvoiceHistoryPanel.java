package com.billing.system.ui;

import com.billing.system.model.Invoice;
import com.billing.system.service.InvoiceService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class InvoiceHistoryPanel extends JPanel {
    private final InvoiceService invoiceService;
    private final DefaultTableModel tableModel;
    private final TableRowSorter<DefaultTableModel> sorter;

    // Filter Fields
    private final JTextField dateField;
    private final JTextField customerField;
    private final JTextField idField;

    // Stats Labels
    private final JLabel totalRevenueLabel;
    private final JLabel totalInvoicesLabel;
    private final JLabel avgValueLabel;

    public InvoiceHistoryPanel(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
        setLayout(new BorderLayout(0, UIHelper.GAP_LARGE));
        UIHelper.stylePanel(this);
        setBorder(new EmptyBorder(UIHelper.PADDING_LARGE, UIHelper.PADDING_LARGE,
                UIHelper.PADDING_LARGE, UIHelper.PADDING_LARGE));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        UIHelper.styleHeaderPanel(headerPanel);
        JLabel headerLabel = new JLabel("Invoice History & Analytics");
        UIHelper.styleHeader(headerLabel);
        headerPanel.add(headerLabel, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // Center Content (Filters + Table)
        JPanel centerPanel = new JPanel(new BorderLayout(0, UIHelper.GAP_MEDIUM));
        UIHelper.stylePanel(centerPanel);

        // Filter Panel
        JPanel filterPanel = new JPanel(new GridBagLayout());
        UIHelper.styleCardPanel(filterPanel);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, UIHelper.GAP_MEDIUM, 0, UIHelper.GAP_MEDIUM);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Date Filter
        gbc.gridx = 0;
        gbc.weightx = 0;
        JLabel dateLabel = new JLabel("Date (YYYY-MM-DD):");
        UIHelper.styleLabel(dateLabel);
        filterPanel.add(dateLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        dateField = new JTextField();
        UIHelper.styleTextField(dateField);
        filterPanel.add(dateField, gbc);

        // Customer Filter
        gbc.gridx = 2;
        gbc.weightx = 0;
        JLabel custLabel = new JLabel("Customer Name:");
        UIHelper.styleLabel(custLabel);
        filterPanel.add(custLabel, gbc);

        gbc.gridx = 3;
        gbc.weightx = 1;
        customerField = new JTextField();
        UIHelper.styleTextField(customerField);
        filterPanel.add(customerField, gbc);

        // ID Filter
        gbc.gridx = 4;
        gbc.weightx = 0;
        JLabel idLabel = new JLabel("Invoice ID:");
        UIHelper.styleLabel(idLabel);
        filterPanel.add(idLabel, gbc);

        gbc.gridx = 5;
        gbc.weightx = 1;
        idField = new JTextField();
        UIHelper.styleTextField(idField);
        filterPanel.add(idField, gbc);

        // Filter Button
        gbc.gridx = 6;
        gbc.weightx = 0;
        JButton filterButton = new JButton("Apply Filters          ");
        UIHelper.styleButton(filterButton);
        filterButton.addActionListener(e -> applyFilters());
        filterPanel.add(filterButton, gbc);

        // Reset Button
        gbc.gridx = 7;
        JButton resetButton = new JButton("Reset           ");
        UIHelper.styleDangerButton(resetButton);
        resetButton.addActionListener(e -> resetFilters());
        filterPanel.add(resetButton, gbc);

        centerPanel.add(filterPanel, BorderLayout.NORTH);

        // Table
        String[] columns = { "Invoice ID", "Date", "Customer", "Total Amount (Rs)", "Status" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(tableModel);
        UIHelper.styleTable(table);

        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        JScrollPane scrollPane = UIHelper.createStyledScrollPane(table);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Stats Panel (South)
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, UIHelper.GAP_LARGE, 0));
        UIHelper.stylePanel(statsPanel);
        statsPanel.setBorder(new EmptyBorder(UIHelper.PADDING_MEDIUM, 0, 0, 0));

        totalRevenueLabel = createStatCard(statsPanel, "Total Revenue", "Rs 0.00");
        totalInvoicesLabel = createStatCard(statsPanel, "Total Invoices", "0");
        avgValueLabel = createStatCard(statsPanel, "Average Value", "Rs 0.00");

        add(statsPanel, BorderLayout.SOUTH);

        // Action Panel (Bottom)
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        UIHelper.stylePanel(actionPanel);

        JButton openPdfButton = new JButton("Open PDF");
        UIHelper.styleButton(openPdfButton);
        openPdfButton.addActionListener(e -> openSelectedPdf(table));

        actionPanel.add(openPdfButton);
        add(actionPanel, BorderLayout.SOUTH);

        // Wrap stats and actions in a south container
        JPanel southContainer = new JPanel(new BorderLayout());
        southContainer.add(statsPanel, BorderLayout.CENTER);
        southContainer.add(actionPanel, BorderLayout.SOUTH);

        add(southContainer, BorderLayout.SOUTH);

        // Initial Load
        refreshData();
    }

    private JLabel createStatCard(JPanel parent, String title, String initialValue) {
        JPanel card = new JPanel(new BorderLayout(0, UIHelper.GAP_SMALL));
        UIHelper.styleCardPanel(card);

        JLabel titleLabel = new JLabel(title);
        UIHelper.styleSecondaryLabel(titleLabel);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel valueLabel = new JLabel(initialValue);
        UIHelper.styleHeader(valueLabel);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        valueLabel.setForeground(UIHelper.ACCENT_BLUE);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        parent.add(card);
        return valueLabel;
    }

    private void resetFilters() {
        dateField.setText("");
        customerField.setText("");
        idField.setText("");
        applyFilters();
    }

    public void refreshData() {
        applyFilters();
    }

    private void applyFilters() {
        tableModel.setRowCount(0);
        List<Invoice> allInvoices = invoiceService.getAllInvoices();

        String dateFilter = dateField.getText().trim().toLowerCase();
        String custFilter = customerField.getText().trim().toLowerCase();
        String idFilter = idField.getText().trim().toLowerCase();

        BigDecimal totalRevenue = BigDecimal.ZERO;
        int count = 0;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (Invoice inv : allInvoices) {
            boolean match = true;

            if (!dateFilter.isEmpty() && !inv.getDate().toString().toLowerCase().contains(dateFilter)) {
                match = false;
            }
            if (!custFilter.isEmpty() && !inv.getCustomer().getName().toLowerCase().contains(custFilter)) {
                match = false;
            }
            if (!idFilter.isEmpty() && !inv.getId().toLowerCase().contains(idFilter)) {
                match = false;
            }

            if (match) {
                tableModel.addRow(new Object[] {
                        inv.getId(),
                        inv.getDate().format(formatter),
                        inv.getCustomer().getName(),
                        String.format("Rs %.2f", inv.getTotalAmount()),
                        inv.getStatus()
                });

                totalRevenue = totalRevenue.add(inv.getTotalAmount());
                count++;
            }
        }

        // Update Stats
        totalRevenueLabel.setText(String.format("Rs %.2f", totalRevenue));
        totalInvoicesLabel.setText(String.valueOf(count));

        if (count > 0) {
            BigDecimal avg = totalRevenue.divide(new BigDecimal(count), 2, java.math.RoundingMode.HALF_UP);
            avgValueLabel.setText(String.format("Rs %.2f", avg));
        } else {
            avgValueLabel.setText("Rs 0.00");
        }
    }

    private void openSelectedPdf(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an invoice to open.", "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String invoiceId = (String) table.getValueAt(selectedRow, 0);
        try {
            invoiceService.openInvoicePdf(invoiceId);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Could not open PDF: " + ex.getMessage() + "\n(It may not have been generated yet)", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
