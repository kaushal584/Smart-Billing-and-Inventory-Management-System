package com.billing.system.ui;

import com.billing.system.model.Customer;
import com.billing.system.model.Invoice;
import com.billing.system.model.Product;
import com.billing.system.repository.Repository;
import com.billing.system.service.InvoiceService;
import com.billing.system.strategy.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class InvoicePanel extends JPanel {
    private final Repository<Product> productRepo;
    private final Repository<Customer> customerRepo;
    private final InvoiceService invoiceService;

    private JComboBox<Customer> customerCombo;
    private JComboBox<Product> productCombo;
    private JTextField quantityField;
    private DefaultTableModel itemsModel;
    private JTextArea invoiceOutput;
    private Invoice currentInvoice;

    private JComboBox<String> discountCombo;
    private JComboBox<String> taxCombo;

    public InvoicePanel(Repository<Product> productRepo,
            Repository<Customer> customerRepo,
            InvoiceService invoiceService) {
        this.productRepo = productRepo;
        this.customerRepo = customerRepo;
        this.invoiceService = invoiceService;

        setLayout(new BorderLayout(0, UIHelper.GAP_LARGE));
        UIHelper.stylePanel(this);
        setBorder(new EmptyBorder(UIHelper.PADDING_LARGE, UIHelper.PADDING_LARGE,
                UIHelper.PADDING_LARGE, UIHelper.PADDING_LARGE));

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        UIHelper.styleHeaderPanel(headerPanel);

        JLabel headerLabel = new JLabel("Invoice Generation");
        UIHelper.styleHeader(headerLabel);

        JPanel topControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, UIHelper.GAP_MEDIUM, 0));
        topControls.setBackground(UIHelper.SURFACE_DARK);

        JLabel customerLabel = new JLabel("Select Customer:");
        UIHelper.styleLabel(customerLabel);

        customerCombo = new JComboBox<>();
        UIHelper.styleComboBox(customerCombo);
        customerCombo.setPreferredSize(new Dimension(250, 40));
        customerCombo.setBackground(UIHelper.SURFACE_LIGHTER);
        customerCombo.setForeground(UIHelper.TEXT_PRIMARY);

        JButton startButton = new JButton("Start Invoice    ");
        UIHelper.styleButton(startButton);
        startButton.addActionListener(e -> startInvoice());

        JButton refreshButton = new JButton("Refresh      ");
        UIHelper.styleButton(refreshButton);
        refreshButton.addActionListener(e -> refreshData());

        JButton newCustomerButton = new JButton("New Customer     ");
        UIHelper.styleSuccessButton(newCustomerButton);
        newCustomerButton.addActionListener(e -> showAddCustomerDialog());

        topControls.add(customerLabel);
        topControls.add(customerCombo);
        topControls.add(newCustomerButton);
        topControls.add(startButton);
        topControls.add(refreshButton);

        headerPanel.add(headerLabel, BorderLayout.WEST);
        headerPanel.add(topControls, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Center: Items and Output
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, UIHelper.GAP_LARGE, 0));
        centerPanel.setBackground(UIHelper.BACKGROUND_DARK);

        // Left: Item Entry
        JPanel itemPanel = new JPanel(new BorderLayout(0, UIHelper.GAP_MEDIUM));
        itemPanel.setBackground(UIHelper.BACKGROUND_DARK);

        JPanel entryCard = new JPanel(new GridBagLayout());
        UIHelper.styleCardPanel(entryCard);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, UIHelper.GAP_MEDIUM, UIHelper.GAP_MEDIUM);

        productCombo = new JComboBox<>();
        UIHelper.styleComboBox(productCombo);
        productCombo.setBackground(UIHelper.SURFACE_LIGHTER);
        productCombo.setForeground(UIHelper.TEXT_PRIMARY);

        // Custom Renderer for Dynamic Stock
        productCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected,
                        cellHasFocus);
                if (value instanceof Product) {
                    Product p = (Product) value;
                    int inCart = 0;
                    if (currentInvoice != null) {
                        for (com.billing.system.model.InvoiceItem item : currentInvoice.getItems()) {
                            if (item.getProduct().getId().equals(p.getId())) {
                                inCart += item.getQuantity();
                            }
                        }
                    }
                    int effectiveStock = p.getQuantity() - inCart;
                    label.setText(String.format("%s (Rs %.2f) [Stock: %d]", p.getName(), p.getPrice(), effectiveStock));

                    if (effectiveStock <= 0) {
                        label.setForeground(Color.RED);
                    } else if (isSelected) {
                        label.setForeground(Color.WHITE);
                    } else {
                        label.setForeground(UIHelper.TEXT_PRIMARY);
                    }
                }
                return label;
            }
        });

        quantityField = new JTextField("1");
        UIHelper.styleTextField(quantityField);
        quantityField.setPreferredSize(new Dimension(100, 40));
        quantityField.setBackground(UIHelper.SURFACE_LIGHTER);
        quantityField.setForeground(UIHelper.TEXT_PRIMARY);

        JButton addItemButton = new JButton("Add Item");
        UIHelper.styleSuccessButton(addItemButton);
        addItemButton.addActionListener(e -> addItem());

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        JLabel prodLabel = new JLabel("Select Product:");
        UIHelper.styleLabel(prodLabel);
        entryCard.add(prodLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.gridwidth = 2;
        entryCard.add(productCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.gridwidth = 1;
        JLabel qtyLabel = new JLabel("Quantity:");
        UIHelper.styleLabel(qtyLabel);
        entryCard.add(qtyLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.5;
        entryCard.add(quantityField, gbc);

        gbc.gridx = 2;
        gbc.insets = new Insets(0, 0, UIHelper.GAP_MEDIUM, 0);
        entryCard.add(addItemButton, gbc);

        String[] columns = { "Product", "Quantity", "Unit Price" };
        itemsModel = new DefaultTableModel(columns, 0);
        JTable itemsTable = new JTable(itemsModel);
        UIHelper.styleTable(itemsTable);

        itemsTable.getTableHeader().setBackground(UIHelper.ACCENT_BLUE);
        itemsTable.getTableHeader().setForeground(Color.WHITE);
        itemsTable.setSelectionBackground(UIHelper.ACCENT_HOVER);
        itemsTable.setSelectionForeground(Color.WHITE);

        JScrollPane itemScroll = UIHelper.createStyledScrollPane(itemsTable);

        itemPanel.add(entryCard, BorderLayout.NORTH);
        itemPanel.add(itemScroll, BorderLayout.CENTER);

        // Right: Output and Strategies
        JPanel rightPanel = new JPanel(new BorderLayout(0, UIHelper.GAP_MEDIUM));
        rightPanel.setBackground(UIHelper.BACKGROUND_DARK);

        invoiceOutput = new JTextArea();
        invoiceOutput.setEditable(false);
        UIHelper.styleTextArea(invoiceOutput);
        invoiceOutput.setBackground(UIHelper.SURFACE_LIGHTER);
        invoiceOutput.setForeground(UIHelper.TEXT_PRIMARY);

        JScrollPane outputScroll = UIHelper.createStyledScrollPane(invoiceOutput);

        JPanel strategyCard = new JPanel(new GridBagLayout());
        UIHelper.styleCardPanel(strategyCard);

        GridBagConstraints sgbc = new GridBagConstraints();
        sgbc.fill = GridBagConstraints.HORIZONTAL;
        sgbc.insets = new Insets(0, 0, UIHelper.GAP_MEDIUM, UIHelper.GAP_MEDIUM);

        discountCombo = new JComboBox<>(new String[] {
                "None", "10% Off", "Rs 50 Off", "Buy 2 Get 1 Free", "Custom Amount", "Custom %"
        });
        UIHelper.styleComboBox(discountCombo);
        discountCombo.setBackground(UIHelper.SURFACE_LIGHTER);
        discountCombo.setForeground(UIHelper.TEXT_PRIMARY);

        taxCombo = new JComboBox<>(new String[] {
                "None", "Simple 10%", "India GST (Delhi)", "India GST (Mumbai)", "Custom Tax %"
        });
        UIHelper.styleComboBox(taxCombo);
        taxCombo.setBackground(UIHelper.SURFACE_LIGHTER);
        taxCombo.setForeground(UIHelper.TEXT_PRIMARY);

        sgbc.gridx = 0;
        sgbc.gridy = 0;
        sgbc.weightx = 0;
        JLabel discLabel = new JLabel("Discount Strategy:");
        UIHelper.styleLabel(discLabel);
        strategyCard.add(discLabel, sgbc);

        sgbc.gridx = 1;
        sgbc.weightx = 1;
        sgbc.insets = new Insets(0, 0, UIHelper.GAP_MEDIUM, 0);
        strategyCard.add(discountCombo, sgbc);

        sgbc.gridx = 0;
        sgbc.gridy = 1;
        sgbc.weightx = 0;
        sgbc.insets = new Insets(0, 0, UIHelper.GAP_MEDIUM, UIHelper.GAP_MEDIUM);
        JLabel taxLabel = new JLabel("Tax Strategy:");
        UIHelper.styleLabel(taxLabel);
        strategyCard.add(taxLabel, sgbc);

        sgbc.gridx = 1;
        sgbc.weightx = 1;
        sgbc.insets = new Insets(0, 0, UIHelper.GAP_MEDIUM, 0);
        strategyCard.add(taxCombo, sgbc);

        sgbc.gridx = 0;
        sgbc.gridy = 2;
        sgbc.gridwidth = 2;
        sgbc.insets = new Insets(UIHelper.PADDING_LARGE, 0, 0, 0);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, UIHelper.GAP_MEDIUM, 0));
        buttonPanel.setOpaque(false);

        JButton generateButton = new JButton("Generate Invoice     ");
        UIHelper.styleSuccessButton(generateButton);
        generateButton.addActionListener(e -> generateInvoice());

        JButton pdfButton = new JButton("Export PDF    ");
        UIHelper.styleButton(pdfButton);
        pdfButton.addActionListener(e -> exportPdf());

        buttonPanel.add(generateButton);
        buttonPanel.add(pdfButton);

        strategyCard.add(buttonPanel, sgbc);

        rightPanel.add(outputScroll, BorderLayout.CENTER);
        rightPanel.add(strategyCard, BorderLayout.SOUTH);

        centerPanel.add(itemPanel);
        centerPanel.add(rightPanel);

        add(centerPanel, BorderLayout.CENTER);

        refreshData();
    }

    private void refreshData() {
        customerCombo.removeAllItems();
        java.util.List<Customer> customers = customerRepo.findAll();
        if (customers.isEmpty()) {
            customerCombo.addItem(new Customer("DUMMY", "No Customer Found", "", "", null) {
                @Override
                public String toString() {
                    return "No Customer Found";
                }
            });
        } else {
            for (Customer c : customers) {
                customerCombo.addItem(c);
            }
        }

        productCombo.removeAllItems();
        for (Product p : productRepo.findAll()) {
            productCombo.addItem(p);
        }
        productCombo.repaint();
    }

    private void showAddCustomerDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "New Customer", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridBagLayout());
        UIHelper.stylePanel(formPanel);
        formPanel.setBorder(new EmptyBorder(UIHelper.PADDING_LARGE, UIHelper.PADDING_LARGE, UIHelper.PADDING_LARGE,
                UIHelper.PADDING_LARGE));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, UIHelper.GAP_MEDIUM, UIHelper.GAP_MEDIUM);

        // Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        JLabel nameLabel = new JLabel("Name:");
        UIHelper.styleLabel(nameLabel);
        formPanel.add(nameLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        JTextField nameField = new JTextField();
        UIHelper.styleTextField(nameField);
        formPanel.add(nameField, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        JLabel emailLabel = new JLabel("Email:");
        UIHelper.styleLabel(emailLabel);
        formPanel.add(emailLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        JTextField emailField = new JTextField();
        UIHelper.styleTextField(emailField);
        formPanel.add(emailField, gbc);

        // Phone
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        JLabel phoneLabel = new JLabel("Phone:");
        UIHelper.styleLabel(phoneLabel);
        formPanel.add(phoneLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        JTextField phoneField = new JTextField();
        UIHelper.styleTextField(phoneField);
        formPanel.add(phoneField, gbc);

        // Type
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        JLabel typeLabel = new JLabel("Type:");
        UIHelper.styleLabel(typeLabel);
        formPanel.add(typeLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        JComboBox<Customer.CustomerType> typeCombo = new JComboBox<>(Customer.CustomerType.values());
        UIHelper.styleComboBox(typeCombo);
        typeCombo.setBackground(UIHelper.SURFACE_LIGHTER);
        typeCombo.setForeground(UIHelper.TEXT_PRIMARY);
        formPanel.add(typeCombo, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(UIHelper.BACKGROUND_DARK);

        JButton saveButton = new JButton("Save");
        UIHelper.styleSuccessButton(saveButton);
        saveButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            Customer.CustomerType type = (Customer.CustomerType) typeCombo.getSelectedItem();

            if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Customer newCustomer = new Customer(java.util.UUID.randomUUID().toString(), name, email, phone, type);
            customerRepo.save(newCustomer);

            refreshData();
            customerCombo.setSelectedItem(newCustomer);

            JOptionPane.showMessageDialog(this, "Customer added successfully!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
        });

        JButton cancelButton = new JButton("Cancel");
        UIHelper.styleDangerButton(cancelButton);
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void startInvoice() {
        Customer customer = (Customer) customerCombo.getSelectedItem();
        if (customer != null && !"DUMMY".equals(customer.getId())) {
            currentInvoice = invoiceService.createInvoice(customer);
            itemsModel.setRowCount(0);
            invoiceOutput.setText("═══════════════════════════════════════\n" +
                    "    NEW INVOICE STARTED\n" +
                    "═══════════════════════════════════════\n\n" +
                    "Customer: " + customer.getName() + "\n" +
                    "Type: " + customer.getType() + "\n\n" +
                    "Add products to continue...");
            JOptionPane.showMessageDialog(this, "Invoice started for " + customer.getName(), "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            productCombo.repaint();
        } else {
            JOptionPane.showMessageDialog(this, "Please select a valid customer or create a new one.",
                    "Invalid Customer",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void addItem() {
        if (currentInvoice == null) {
            JOptionPane.showMessageDialog(this, "Please start an invoice first by selecting a customer.",
                    "No Invoice", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Product product = (Product) productCombo.getSelectedItem();
        try {
            int qty = Integer.parseInt(quantityField.getText().trim());
            if (product != null && qty > 0) {
                // Calculate quantity already in the invoice for this product
                int existingQty = 0;
                for (com.billing.system.model.InvoiceItem item : currentInvoice.getItems()) {
                    if (item.getProduct().getId().equals(product.getId())) {
                        existingQty += item.getQuantity();
                    }
                }

                if (existingQty + qty > product.getQuantity()) {
                    JOptionPane.showMessageDialog(this,
                            "Insufficient stock! Available: " + product.getQuantity() +
                                    "\nAlready in Invoice: " + existingQty +
                                    "\nRequested: " + qty +
                                    "\nTotal would be: " + (existingQty + qty),
                            "Stock Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                invoiceService.addProduct(currentInvoice, product, qty);
                itemsModel.addRow(new Object[] {
                        product.getName(),
                        qty,
                        String.format("Rs %.2f", product.getPrice())
                });
                quantityField.setText("1");
                JOptionPane.showMessageDialog(this, "Item added successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                productCombo.repaint();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid product or quantity. Quantity must be greater than 0.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid quantity format. Please enter a valid number.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generateInvoice() {
        if (currentInvoice == null) {
            JOptionPane.showMessageDialog(this, "Please start an invoice first by selecting a customer.",
                    "No Invoice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (currentInvoice.getItems().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please add at least one item to the invoice.",
                    "Empty Invoice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Select Strategies
        String discountSelection = (String) discountCombo.getSelectedItem();
        Double discountValue = null;
        Product selectedProduct = (Product) productCombo.getSelectedItem();

        try {
            if ("Custom Amount".equals(discountSelection)) {
                String input = JOptionPane.showInputDialog(this, "Enter discount amount (Rs):");
                if (input != null && !input.trim().isEmpty()) {
                    discountValue = Double.parseDouble(input.trim());
                }
            } else if ("Custom %".equals(discountSelection)) {
                String input = JOptionPane.showInputDialog(this, "Enter discount percentage:");
                if (input != null && !input.trim().isEmpty()) {
                    discountValue = Double.parseDouble(input.trim());
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid number format for discount. Using default.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        DiscountStrategy discountStrategy = StrategyFactory.getDiscountStrategy(discountSelection, discountValue,
                selectedProduct);

        String taxSelection = (String) taxCombo.getSelectedItem();
        Double taxValue = null;

        try {
            if ("Custom Tax %".equals(taxSelection)) {
                String input = JOptionPane.showInputDialog(this, "Enter tax percentage:");
                if (input != null && !input.trim().isEmpty()) {
                    taxValue = Double.parseDouble(input.trim());
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid number format for tax. Using default.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        TaxStrategy taxStrategy = StrategyFactory.getTaxStrategy(taxSelection, taxValue);

        invoiceService.generateInvoice(currentInvoice, discountStrategy, taxStrategy);
        String output = invoiceService.exportInvoice(currentInvoice);
        invoiceOutput.setText(output);

        JOptionPane.showMessageDialog(this, "Invoice generated and saved successfully!", "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void exportPdf() {
        if (currentInvoice == null || currentInvoice.getItems().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No invoice to export!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            invoiceService.generateAndOpenPdf(currentInvoice);
            JOptionPane.showMessageDialog(this, "Invoice exported and opened successfully!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error exporting PDF:\n" + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}