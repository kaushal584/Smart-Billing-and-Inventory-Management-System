package com.billing.system.ui;

import com.billing.system.model.Product;
import com.billing.system.repository.Repository;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.UUID;

public class ProductPanel extends JPanel {
    private final Repository<Product> repository;
    private final DefaultTableModel tableModel;
    private final JTextField nameField;
    private final JTextField descField;
    private final JTextField priceField;
    private final JTextField catField;
    private final JTextField quantityField;

    public ProductPanel(Repository<Product> repository) {
        this.repository = repository;
        setLayout(new BorderLayout(0, UIHelper.GAP_LARGE));
        UIHelper.stylePanel(this);
        setBorder(new EmptyBorder(UIHelper.PADDING_LARGE, UIHelper.PADDING_LARGE,
                UIHelper.PADDING_LARGE, UIHelper.PADDING_LARGE));

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        UIHelper.styleHeaderPanel(headerPanel);

        JLabel headerLabel = new JLabel("Product Management");
        UIHelper.styleHeader(headerLabel);

        JButton refreshButton = new JButton("Refresh    ");
        UIHelper.styleButton(refreshButton);
        refreshButton.addActionListener(e -> refreshTable());

        headerPanel.add(headerLabel, BorderLayout.WEST);
        headerPanel.add(refreshButton, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Table Panel with styled scroll
        String[] columns = { "ID", "Name", "Description", "Price", "Category", "Quantity" };
        tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);
        UIHelper.styleTable(table);

        // Make table header and selection more visible
        table.getTableHeader().setBackground(UIHelper.ACCENT_BLUE);
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(UIHelper.ACCENT_HOVER);
        table.setSelectionForeground(Color.WHITE);

        JScrollPane scrollPane = UIHelper.createStyledScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Form Card
        JPanel formCard = new JPanel(new GridBagLayout());
        // Use a dark surface for the form card for better contrast
        formCard.setBackground(UIHelper.SURFACE_DARK);
        UIHelper.styleCardPanel(formCard);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, UIHelper.GAP_MEDIUM, UIHelper.GAP_MEDIUM);

        nameField = new JTextField();
        UIHelper.styleTextField(nameField);
        nameField.setBackground(UIHelper.SURFACE_LIGHTER);
        nameField.setForeground(UIHelper.TEXT_PRIMARY);

        descField = new JTextField();
        UIHelper.styleTextField(descField);
        descField.setBackground(UIHelper.SURFACE_LIGHTER);
        descField.setForeground(UIHelper.TEXT_PRIMARY);

        priceField = new JTextField();
        UIHelper.styleTextField(priceField);
        priceField.setBackground(UIHelper.SURFACE_LIGHTER);
        priceField.setForeground(UIHelper.TEXT_PRIMARY);

        catField = new JTextField();
        UIHelper.styleTextField(catField);
        catField.setBackground(UIHelper.SURFACE_LIGHTER);
        catField.setForeground(UIHelper.TEXT_PRIMARY);

        quantityField = new JTextField();
        UIHelper.styleTextField(quantityField);
        quantityField.setBackground(UIHelper.SURFACE_LIGHTER);
        quantityField.setForeground(UIHelper.TEXT_PRIMARY);

        // Row 0: Name and Price
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        JLabel nameLabel = new JLabel("Product Name:");
        UIHelper.styleLabel(nameLabel);
        formCard.add(nameLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        formCard.add(nameField, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        JLabel priceLabel = new JLabel("Price (Rs):");
        UIHelper.styleLabel(priceLabel);
        formCard.add(priceLabel, gbc);

        gbc.gridx = 3;
        gbc.weightx = 1;
        gbc.insets = new Insets(0, 0, UIHelper.GAP_MEDIUM, 0);
        formCard.add(priceField, gbc);

        // Row 1: Category and Quantity
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, UIHelper.GAP_MEDIUM, UIHelper.GAP_MEDIUM);

        gbc.gridx = 0;
        gbc.weightx = 0;
        JLabel catLabel = new JLabel("Category:");
        UIHelper.styleLabel(catLabel);
        formCard.add(catLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        formCard.add(catField, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        JLabel qtyLabel = new JLabel("Quantity:");
        UIHelper.styleLabel(qtyLabel);
        formCard.add(qtyLabel, gbc);

        gbc.gridx = 3;
        gbc.weightx = 1;
        gbc.insets = new Insets(0, 0, UIHelper.GAP_MEDIUM, 0);
        formCard.add(quantityField, gbc);

        // Row 2: Description
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, UIHelper.GAP_MEDIUM, UIHelper.GAP_MEDIUM);

        gbc.gridx = 0;
        gbc.weightx = 0;
        JLabel descLabel = new JLabel("Description:");
        UIHelper.styleLabel(descLabel);
        formCard.add(descLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(0, 0, UIHelper.GAP_MEDIUM, 0);
        formCard.add(descField, gbc);

        // Row 3: Buttons
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(UIHelper.PADDING_LARGE, 0, 0, UIHelper.GAP_MEDIUM);
        JButton addButton = new JButton("Add Product");
        UIHelper.styleSuccessButton(addButton);
        addButton.addActionListener(e -> addProduct());
        formCard.add(addButton, gbc);

        gbc.gridx = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(UIHelper.PADDING_LARGE, 0, 0, 0);
        JButton deleteButton = new JButton("Delete Selected");
        UIHelper.styleDangerButton(deleteButton);
        deleteButton.addActionListener(e -> deleteProduct(table));
        formCard.add(deleteButton, gbc);

        add(formCard, BorderLayout.SOUTH);

        refreshTable();
    }

    private void addProduct() {
        try {
            String name = nameField.getText().trim();
            String desc = descField.getText().trim();
            String priceText = priceField.getText().trim();
            String cat = catField.getText().trim();
            String qtyText = quantityField.getText().trim();

            if (name.isEmpty() || priceText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Product name and price are required!", "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            BigDecimal price = new BigDecimal(priceText);
            if (price.compareTo(BigDecimal.ZERO) <= 0) {
                JOptionPane.showMessageDialog(this, "Price must be greater than zero!", "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int quantity = 0;
            if (!qtyText.isEmpty()) {
                quantity = Integer.parseInt(qtyText);
                if (quantity < 0) {
                    JOptionPane.showMessageDialog(this, "Quantity cannot be negative!", "Validation Error",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            Product product = new Product(UUID.randomUUID().toString(), name, desc, price, cat, quantity);
            repository.save(product);

            nameField.setText("");
            descField.setText("");
            priceField.setText("");
            catField.setText("");
            quantityField.setText("");

            refreshTable();
            JOptionPane.showMessageDialog(this, "Product added successfully!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid price or quantity format! Please enter valid numbers.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteProduct(JTable table) {
        int row = table.getSelectedRow();
        if (row != -1) {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this product?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                String id = (String) tableModel.getValueAt(row, 0);
                repository.delete(id);
                refreshTable();
                JOptionPane.showMessageDialog(this, "Product deleted successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a product to delete.", "No Selection",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Product p : repository.findAll()) {
            tableModel.addRow(new Object[] {
                    p.getId(),
                    p.getName(),
                    p.getDescription(),
                    p.getPrice(),
                    p.getCategory(),
                    p.getQuantity()
            });
        }
    }
}