package com.billing.system.ui;

import com.billing.system.model.Customer;
import com.billing.system.repository.Repository;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.UUID;

public class CustomerPanel extends JPanel {
    private final Repository<Customer> repository;
    private final DefaultTableModel tableModel;
    private final JTextField nameField;
    private final JTextField emailField;
    private final JTextField phoneField;
    private final JComboBox<Customer.CustomerType> typeCombo;

    public CustomerPanel(Repository<Customer> repository) {
        this.repository = repository;
        setLayout(new BorderLayout(0, UIHelper.GAP_LARGE));
        UIHelper.stylePanel(this);
        setBorder(new EmptyBorder(UIHelper.PADDING_LARGE, UIHelper.PADDING_LARGE,
                UIHelper.PADDING_LARGE, UIHelper.PADDING_LARGE));

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        UIHelper.styleHeaderPanel(headerPanel);

        JLabel headerLabel = new JLabel("Customer Management");
        UIHelper.styleHeader(headerLabel);

        JButton refreshButton = new JButton("Refresh    ");
        UIHelper.styleButton(refreshButton);
        refreshButton.addActionListener(e -> refreshTable());

        headerPanel.add(headerLabel, BorderLayout.WEST);
        headerPanel.add(refreshButton, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Table Panel with styled scroll
        String[] columns = { "ID", "Name", "Email", "Phone", "Type" };
        tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);
        UIHelper.styleTable(table);

        // Fix: Make table header and selection more visible
        table.getTableHeader().setBackground(UIHelper.ACCENT_BLUE);
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(UIHelper.ACCENT_HOVER);
        table.setSelectionForeground(Color.WHITE);

        JScrollPane scrollPane = UIHelper.createStyledScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Form Card
        JPanel formCard = new JPanel(new GridBagLayout());
        // Fix: Use a dark surface for the form card for better contrast
        formCard.setBackground(UIHelper.SURFACE_DARK);
        UIHelper.styleCardPanel(formCard);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, UIHelper.GAP_MEDIUM, UIHelper.GAP_MEDIUM);

        nameField = new JTextField();
        UIHelper.styleTextField(nameField);
        // Fix: Set dark background and light text
        nameField.setBackground(UIHelper.SURFACE_LIGHTER);
        nameField.setForeground(UIHelper.TEXT_PRIMARY);

        emailField = new JTextField();
        UIHelper.styleTextField(emailField);
        emailField.setBackground(UIHelper.SURFACE_LIGHTER);
        emailField.setForeground(UIHelper.TEXT_PRIMARY);

        phoneField = new JTextField();
        UIHelper.styleTextField(phoneField);
        phoneField.setBackground(UIHelper.SURFACE_LIGHTER);
        phoneField.setForeground(UIHelper.TEXT_PRIMARY);

        typeCombo = new JComboBox<>(Customer.CustomerType.values());
        UIHelper.styleComboBox(typeCombo);
        // Fix: Explicitly set renderer to ensure dark background and light text
        typeCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected,
                        cellHasFocus);
                if (isSelected) {
                    label.setBackground(UIHelper.ACCENT_BLUE);
                    label.setForeground(Color.WHITE);
                } else {
                    label.setBackground(UIHelper.SURFACE_LIGHTER);
                    label.setForeground(UIHelper.TEXT_PRIMARY);
                }
                label.setOpaque(true);
                return label;
            }
        });

        // Row 1: Name and Email
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        JLabel nameLabel = new JLabel("Customer Name:");
        UIHelper.styleLabel(nameLabel);
        formCard.add(nameLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        formCard.add(nameField, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        JLabel emailLabel = new JLabel("Email:");
        UIHelper.styleLabel(emailLabel);
        formCard.add(emailLabel, gbc);

        gbc.gridx = 3;
        gbc.weightx = 1;
        gbc.insets = new Insets(0, 0, UIHelper.GAP_MEDIUM, 0);
        formCard.add(emailField, gbc);

        // Row 2: Phone and Type
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.insets = new Insets(0, 0, UIHelper.GAP_MEDIUM, UIHelper.GAP_MEDIUM);
        JLabel phoneLabel = new JLabel("Phone:");
        UIHelper.styleLabel(phoneLabel);
        formCard.add(phoneLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        formCard.add(phoneField, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        JLabel typeLabel = new JLabel("Type:");
        UIHelper.styleLabel(typeLabel);
        formCard.add(typeLabel, gbc);

        gbc.gridx = 3;
        gbc.weightx = 1;
        gbc.insets = new Insets(0, 0, UIHelper.GAP_MEDIUM, 0);
        formCard.add(typeCombo, gbc);

        // Row 3: Buttons
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(UIHelper.PADDING_LARGE, 0, 0, UIHelper.GAP_MEDIUM);
        JButton addButton = new JButton("Add Customer");
        UIHelper.styleSuccessButton(addButton);
        addButton.addActionListener(e -> addCustomer());
        formCard.add(addButton, gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(UIHelper.PADDING_LARGE, 0, 0, 0);
        JButton deleteButton = new JButton("Delete Selected");
        UIHelper.styleDangerButton(deleteButton);
        deleteButton.addActionListener(e -> deleteCustomer(table));
        formCard.add(deleteButton, gbc);

        add(formCard, BorderLayout.SOUTH);

        refreshTable();
    }

    private void addCustomer() {
        try {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            Customer.CustomerType type = (Customer.CustomerType) typeCombo.getSelectedItem();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Customer name is required!", "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!email.isEmpty() && !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                JOptionPane.showMessageDialog(this, "Please enter a valid email address!", "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            Customer customer = new Customer(UUID.randomUUID().toString(), name, email, phone, type);
            repository.save(customer);

            nameField.setText("");
            emailField.setText("");
            phoneField.setText("");

            refreshTable();
            JOptionPane.showMessageDialog(this, "Customer added successfully!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteCustomer(JTable table) {
        int row = table.getSelectedRow();
        if (row != -1) {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this customer?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                String id = (String) tableModel.getValueAt(row, 0);
                repository.delete(id);
                refreshTable();
                JOptionPane.showMessageDialog(this, "Customer deleted successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a customer to delete.", "No Selection",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Customer c : repository.findAll()) {
            tableModel.addRow(new Object[] {
                    c.getId(),
                    c.getName(),
                    c.getEmail(),
                    c.getPhone(),
                    c.getType()
            });
        }
    }
}