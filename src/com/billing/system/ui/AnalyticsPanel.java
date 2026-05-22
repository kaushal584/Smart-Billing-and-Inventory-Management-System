package com.billing.system.ui;

import com.billing.system.model.Customer;
import com.billing.system.model.Invoice;
import com.billing.system.model.Product;
import com.billing.system.repository.Repository;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AnalyticsPanel extends JPanel {
    private final Repository<Invoice> invoiceRepo;
    private final Repository<Product> productRepo;

    private JLabel totalRevenueLabel;
    private JLabel totalInvoicesLabel;
    private JLabel topCustomerLabel;
    private JPanel chartsPanel;

    public AnalyticsPanel(Repository<Invoice> invoiceRepo, Repository<Product> productRepo) {
        this.invoiceRepo = invoiceRepo;
        this.productRepo = productRepo;

        setLayout(new BorderLayout(0, UIHelper.GAP_LARGE));
        UIHelper.stylePanel(this);
        setBorder(new EmptyBorder(UIHelper.PADDING_LARGE, UIHelper.PADDING_LARGE,
                UIHelper.PADDING_LARGE, UIHelper.PADDING_LARGE));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        UIHelper.styleHeaderPanel(headerPanel);
        JLabel headerLabel = new JLabel("Analytics Dashboard");
        UIHelper.styleHeader(headerLabel);

        JButton refreshButton = new JButton("Refresh   ");
        UIHelper.styleButton(refreshButton);
        refreshButton.addActionListener(e -> refreshData());

        headerPanel.add(headerLabel, BorderLayout.WEST);
        headerPanel.add(refreshButton, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Content
        JPanel contentPanel = new JPanel(new BorderLayout(0, UIHelper.GAP_LARGE));
        contentPanel.setOpaque(false);

        // Summary Cards
        JPanel cardsPanel = new JPanel(new GridLayout(1, 3, UIHelper.GAP_LARGE, 0));
        cardsPanel.setOpaque(false);

        totalRevenueLabel = createSummaryCard(cardsPanel, "Total Revenue", "Rs 0.00");
        totalInvoicesLabel = createSummaryCard(cardsPanel, "Total Invoices", "0");
        topCustomerLabel = createSummaryCard(cardsPanel, "Top Customer", "-");

        contentPanel.add(cardsPanel, BorderLayout.NORTH);

        // Charts Area
        chartsPanel = new JPanel(new GridLayout(1, 2, UIHelper.GAP_LARGE, 0));
        chartsPanel.setOpaque(false);
        contentPanel.add(chartsPanel, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);

        refreshData();
    }

    private JLabel createSummaryCard(JPanel parent, String title, String initialValue) {
        JPanel card = new JPanel(new BorderLayout(0, UIHelper.GAP_SMALL));
        UIHelper.styleCardPanel(card);
        card.setBorder(new EmptyBorder(UIHelper.PADDING_LARGE, UIHelper.PADDING_LARGE,
                UIHelper.PADDING_LARGE, UIHelper.PADDING_LARGE));

        JLabel titleLabel = new JLabel(title);
        UIHelper.styleLabel(titleLabel);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(UIHelper.TEXT_SECONDARY);

        JLabel valueLabel = new JLabel(initialValue);
        UIHelper.styleLabel(valueLabel);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(UIHelper.ACCENT_BLUE);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        parent.add(card);
        return valueLabel;
    }

    private void refreshData() {
        List<Invoice> invoices = invoiceRepo.findAll();
        List<Product> products = productRepo.findAll();

        // 1. Total Revenue
        BigDecimal totalRevenue = invoices.stream()
                .map(Invoice::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        totalRevenueLabel.setText(String.format("Rs %.2f", totalRevenue));

        // 2. Total Invoices
        totalInvoicesLabel.setText(String.valueOf(invoices.size()));

        // 3. Top Customer
        String topCustomer = invoices.stream()
                .collect(Collectors.groupingBy(i -> i.getCustomer().getName(), Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("-");
        topCustomerLabel.setText(topCustomer);

        // 4. Charts
        chartsPanel.removeAll();

        // Top Products Chart
        List<Product> topProducts = products.stream()
                .sorted(Comparator.comparingInt(Product::getSoldQuantity).reversed())
                .limit(5)
                .collect(Collectors.toList());

        chartsPanel.add(new TopProductsChart(topProducts));

        // Sales Trend (Dummy for now, or simple daily aggregation if dates allow)
        // Since we have dates, let's try to group by date (simple string
        // representation)
        // For simplicity, we'll just show a placeholder or "Last 5 Invoices"
        chartsPanel.add(new SalesTrendChart(invoices));

        chartsPanel.revalidate();
        chartsPanel.repaint();
    }

    // Inner class for Top Products Chart
    private static class TopProductsChart extends JPanel {
        private final List<Product> products;

        public TopProductsChart(List<Product> products) {
            this.products = products;
            UIHelper.styleCardPanel(this);
            setBorder(new EmptyBorder(UIHelper.PADDING_LARGE, UIHelper.PADDING_LARGE,
                    UIHelper.PADDING_LARGE, UIHelper.PADDING_LARGE));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int padding = 40;

            // Title
            g2.setColor(UIHelper.TEXT_PRIMARY);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
            g2.drawString("Top Selling Products", padding, padding);

            if (products.isEmpty()) {
                g2.drawString("No data available", padding, padding + 30);
                return;
            }

            int maxQty = products.get(0).getSoldQuantity();
            if (maxQty == 0)
                maxQty = 1;

            int barHeight = 30;
            int gap = 15;
            int startY = padding + 40;
            int maxBarWidth = width - (2 * padding) - 100; // Leave space for text

            for (int i = 0; i < products.size(); i++) {
                Product p = products.get(i);
                int barWidth = (int) ((double) p.getSoldQuantity() / maxQty * maxBarWidth);

                int y = startY + (i * (barHeight + gap));

                // Bar
                g2.setColor(UIHelper.ACCENT_BLUE);
                g2.fillRoundRect(padding, y, barWidth, barHeight, 5, 5);

                // Product Name
                g2.setColor(UIHelper.TEXT_PRIMARY);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                g2.drawString(p.getName(), padding, y - 5);

                // Quantity
                g2.setColor(UIHelper.TEXT_SECONDARY);
                g2.drawString(String.valueOf(p.getSoldQuantity()) + " sold", padding + barWidth + 10, y + 20);
            }
        }
    }

    // Inner class for Sales Trend Chart (Simplified: Last 10 Invoices)
    private static class SalesTrendChart extends JPanel {
        private final List<Invoice> invoices;

        public SalesTrendChart(List<Invoice> invoices) {
            this.invoices = invoices.stream()
                    .sorted(Comparator.comparing(Invoice::getDate)) // Oldest to Newest
                    .skip(Math.max(0, invoices.size() - 10)) // Last 10
                    .collect(Collectors.toList());
            UIHelper.styleCardPanel(this);
            setBorder(new EmptyBorder(UIHelper.PADDING_LARGE, UIHelper.PADDING_LARGE,
                    UIHelper.PADDING_LARGE, UIHelper.PADDING_LARGE));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int padding = 40;

            // Title
            g2.setColor(UIHelper.TEXT_PRIMARY);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
            g2.drawString("Recent Sales (Last 10 Invoices)", padding, padding);

            if (invoices.isEmpty()) {
                g2.drawString("No data available", padding, padding + 30);
                return;
            }

            BigDecimal maxAmount = invoices.stream()
                    .map(Invoice::getTotalAmount)
                    .max(Comparator.naturalOrder())
                    .orElse(BigDecimal.ONE);

            if (maxAmount.compareTo(BigDecimal.ZERO) == 0)
                maxAmount = BigDecimal.ONE;

            int chartHeight = height - (2 * padding) - 40;
            int chartWidth = width - (2 * padding);
            int barWidth = chartWidth / invoices.size() - 10;
            int startX = padding;
            int bottomY = height - padding;

            for (int i = 0; i < invoices.size(); i++) {
                Invoice inv = invoices.get(i);
                double ratio = inv.getTotalAmount().doubleValue() / maxAmount.doubleValue();
                int barHeight = (int) (ratio * chartHeight);

                int x = startX + (i * (barWidth + 10));
                int y = bottomY - barHeight;

                // Bar
                g2.setColor(UIHelper.SUCCESS_GREEN);
                g2.fillRoundRect(x, y, barWidth, barHeight, 5, 5);

                // Amount (if space permits)
                if (barWidth > 30) {
                    g2.setColor(UIHelper.TEXT_SECONDARY);
                    g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                    // Rotate text or just show short amount
                    String amt = String.valueOf(inv.getTotalAmount().intValue());
                    g2.drawString(amt, x, y - 5);
                }
            }
        }
    }
}
