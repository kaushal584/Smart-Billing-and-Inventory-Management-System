package com.billing.system.ui;

import com.billing.system.repository.Repository;
import com.billing.system.model.Product;
import com.billing.system.model.Customer;
import com.billing.system.model.Invoice;
import com.billing.system.service.InvoiceService;
import javax.swing.*;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame(Repository<Product> productRepo,
            Repository<Customer> customerRepo,
            Repository<Invoice> invoiceRepo,
            InvoiceService invoiceService) {
        setTitle("Online Billing And Inventory Management System");
        setSize(1400, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Set Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            // Configure all JOptionPane dialogs to match dark theme
            // Configure global dark theme properties
            UIHelper.setupDarkTheme();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create main container
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(UIHelper.BACKGROUND_DARK);

        // Create custom tabbed pane with enhanced styling
        JTabbedPane tabbedPane = new JTabbedPane();
        customizeTabbedPane(tabbedPane);

        // Add tabs with consistent icons (if you have icons, uncomment these lines)
        tabbedPane.addTab("  Products  ", new ProductPanel(productRepo));
        tabbedPane.addTab("  Customers  ", new CustomerPanel(customerRepo));
        tabbedPane.addTab("  New Invoice  ", new InvoicePanel(productRepo, customerRepo, invoiceService));
        tabbedPane.addTab("  History  ", new InvoiceHistoryPanel(invoiceService));
        tabbedPane.addTab("  Analytics  ", new AnalyticsPanel(invoiceRepo, productRepo));

        mainContainer.add(tabbedPane, BorderLayout.CENTER);
        add(mainContainer);

        getContentPane().setBackground(UIHelper.BACKGROUND_DARK);
    }

    private void customizeTabbedPane(JTabbedPane tabbedPane) {
        tabbedPane.setFont(UIHelper.BOLD_FONT);
        tabbedPane.setBackground(UIHelper.BACKGROUND_DARK);
        tabbedPane.setForeground(UIHelper.TEXT_PRIMARY);

        // Enhanced UIManager settings for consistent tabbed pane styling
        UIManager.put("TabbedPane.selected", UIHelper.ACCENT_BLUE);
        UIManager.put("TabbedPane.background", UIHelper.BACKGROUND_DARK);
        UIManager.put("TabbedPane.foreground", UIHelper.TEXT_PRIMARY);
        UIManager.put("TabbedPane.contentAreaColor", UIHelper.BACKGROUND_DARK);
        UIManager.put("TabbedPane.selectedForeground", Color.WHITE);
        UIManager.put("TabbedPane.tabInsets", new Insets(10, 20, 10, 20));
        UIManager.put("TabbedPane.contentBorderInsets", new Insets(0, 0, 0, 0));
        UIManager.put("TabbedPane.tabAreaBackground", UIHelper.SURFACE_DARK);

        // Apply custom UI for better control
        tabbedPane.setUI(new BasicTabbedPaneUI() {
            @Override
            protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex,
                    int x, int y, int w, int h, boolean isSelected) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (isSelected) {
                    g2d.setColor(UIHelper.ACCENT_BLUE);
                } else {
                    g2d.setColor(UIHelper.SURFACE_DARK);
                }
                g2d.fillRoundRect(x, y, w, h, 8, 8);
            }

            @Override
            protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex,
                    int x, int y, int w, int h, boolean isSelected) {
                // No border for cleaner look
            }

            @Override
            protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
                // No content border for cleaner look
            }
        });
    }
}