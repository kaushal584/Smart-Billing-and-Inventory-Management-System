package com.billing.system.ui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class UIHelper {
    // Modern Dark Theme - HIGH CONTRAST
    public static final Color BACKGROUND_DARK = new Color(17, 24, 39);
    public static final Color SURFACE_DARK = new Color(31, 41, 55);
    public static final Color SURFACE_LIGHTER = new Color(55, 65, 81);
    public static final Color ACCENT_BLUE = new Color(59, 130, 246);
    public static final Color ACCENT_HOVER = new Color(96, 165, 250);
    public static final Color TEXT_PRIMARY = new Color(243, 244, 246);
    public static final Color TEXT_SECONDARY = new Color(156, 163, 175);
    public static final Color BORDER_COLOR = new Color(75, 85, 99);
    public static final Color SUCCESS_GREEN = new Color(16, 185, 129);
    public static final Color DANGER_RED = new Color(239, 68, 68);
    public static final Color WARNING_YELLOW = new Color(245, 158, 11);

    public static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font SUBHEADER_FONT = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font REGULAR_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font BOLD_FONT = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font SMALL_FONT = new Font("Segoe UI", Font.PLAIN, 12);

    public static final int PADDING_SMALL = 8;
    public static final int PADDING_MEDIUM = 12;
    public static final int PADDING_LARGE = 16;
    public static final int PADDING_XL = 20;
    public static final int GAP_SMALL = 8;
    public static final int GAP_MEDIUM = 12;
    public static final int GAP_LARGE = 16;

    public static void styleButton(JButton button) {
        button.setFont(BOLD_FONT);
        button.setBackground(ACCENT_BLUE);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setBorder(new EmptyBorder(PADDING_MEDIUM, PADDING_XL, PADDING_MEDIUM, PADDING_XL));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setPreferredSize(new Dimension(button.getPreferredSize().width, 40));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(ACCENT_HOVER);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(ACCENT_BLUE);
            }
        });
    }

    public static void styleSuccessButton(JButton button) {
        button.setFont(BOLD_FONT);
        button.setBackground(SUCCESS_GREEN);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setBorder(new EmptyBorder(PADDING_MEDIUM, PADDING_XL, PADDING_MEDIUM, PADDING_XL));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setPreferredSize(new Dimension(button.getPreferredSize().width, 40));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(20, 205, 145));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(SUCCESS_GREEN);
            }
        });
    }

    public static void styleDangerButton(JButton button) {
        button.setFont(BOLD_FONT);
        button.setBackground(DANGER_RED);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setBorder(new EmptyBorder(PADDING_MEDIUM, PADDING_XL, PADDING_MEDIUM, PADDING_XL));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setPreferredSize(new Dimension(button.getPreferredSize().width, 40));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(248, 113, 113));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(DANGER_RED);
            }
        });
    }

    public static void styleLabel(JLabel label) {
        label.setFont(REGULAR_FONT);
        label.setForeground(TEXT_PRIMARY);
        label.setOpaque(false);
    }

    public static void styleSecondaryLabel(JLabel label) {
        label.setFont(SMALL_FONT);
        label.setForeground(TEXT_SECONDARY);
        label.setOpaque(false);
    }

    public static void styleHeader(JLabel label) {
        label.setFont(HEADER_FONT);
        label.setForeground(TEXT_PRIMARY);
        label.setOpaque(false);
    }

    public static void styleSubheader(JLabel label) {
        label.setFont(SUBHEADER_FONT);
        label.setForeground(TEXT_PRIMARY);
        label.setOpaque(false);
    }

    public static void styleTextField(JTextField field) {
        field.setFont(REGULAR_FONT);
        field.setBackground(SURFACE_LIGHTER);
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(ACCENT_BLUE);
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(PADDING_SMALL, PADDING_MEDIUM, PADDING_SMALL, PADDING_MEDIUM)));
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, 40));
    }

    public static void styleTextArea(JTextArea area) {
        area.setFont(new Font("Consolas", Font.PLAIN, 13));
        area.setBackground(SURFACE_LIGHTER);
        area.setForeground(TEXT_PRIMARY);
        area.setCaretColor(ACCENT_BLUE);
        area.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(PADDING_MEDIUM, PADDING_MEDIUM, PADDING_MEDIUM, PADDING_MEDIUM)));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
    }

    public static void styleComboBox(JComboBox<?> combo) {
        combo.setFont(REGULAR_FONT);
        combo.setBackground(SURFACE_LIGHTER);
        combo.setForeground(TEXT_PRIMARY);
        combo.setBorder(new LineBorder(BORDER_COLOR, 1, true));
        combo.setPreferredSize(new Dimension(combo.getPreferredSize().width, 40));
        combo.setOpaque(true);

        // Fix: Force MetalComboBoxUI to respect background colors on Windows
        try {
            combo.setUI(new javax.swing.plaf.metal.MetalComboBoxUI() {
                @Override
                public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
                    g.setColor(SURFACE_LIGHTER);
                    g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
                }
            });
        } catch (Exception e) {
            // Fallback
        }

        // Fix the editor component colors (the displayed selected value)
        try {
            Component editorComp = combo.getEditor().getEditorComponent();
            if (editorComp instanceof JTextField) {
                JTextField tf = (JTextField) editorComp;
                tf.setBackground(SURFACE_LIGHTER);
                tf.setForeground(TEXT_PRIMARY);
                tf.setCaretColor(ACCENT_BLUE);
            }
        } catch (Exception e) {
            // Ignore if not editable
        }

        // Style dropdown list
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected,
                        cellHasFocus);
                if (isSelected) {
                    label.setBackground(ACCENT_BLUE);
                    label.setForeground(Color.WHITE);
                } else {
                    label.setBackground(SURFACE_LIGHTER);
                    label.setForeground(TEXT_PRIMARY);
                }
                label.setBorder(new EmptyBorder(PADDING_SMALL, PADDING_MEDIUM, PADDING_SMALL, PADDING_MEDIUM));
                label.setOpaque(true);
                return label;
            }
        });
    }

    public static void styleTable(JTable table) {
        table.setBackground(SURFACE_DARK);
        table.setForeground(TEXT_PRIMARY);
        table.setSelectionBackground(ACCENT_BLUE);
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(BORDER_COLOR);
        table.setFont(REGULAR_FONT);
        table.setRowHeight(40);
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));

        // Cell renderer
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (isSelected) {
                    c.setBackground(ACCENT_BLUE);
                    c.setForeground(Color.WHITE);
                } else {
                    c.setBackground(SURFACE_DARK);
                    c.setForeground(TEXT_PRIMARY);
                }
                return c;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        // Header
        JTableHeader header = table.getTableHeader();
        header.setBackground(ACCENT_BLUE);
        header.setForeground(Color.WHITE);
        header.setFont(BOLD_FONT);
        header.setBorder(new MatteBorder(0, 0, 2, 0, ACCENT_HOVER));
        header.setPreferredSize(new Dimension(header.getWidth(), 45));
        header.setReorderingAllowed(false);
        header.setOpaque(true);

        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
                        column);
                label.setBackground(ACCENT_BLUE);
                label.setForeground(Color.WHITE);
                label.setFont(BOLD_FONT);
                label.setBorder(new EmptyBorder(PADDING_MEDIUM, PADDING_MEDIUM, PADDING_MEDIUM, PADDING_MEDIUM));
                label.setHorizontalAlignment(JLabel.LEFT);
                label.setOpaque(true);
                return label;
            }
        });
    }

    public static void stylePanel(JPanel panel) {
        panel.setBackground(BACKGROUND_DARK);
    }

    public static void styleCardPanel(JPanel panel) {
        panel.setBackground(SURFACE_DARK);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(PADDING_XL, PADDING_XL, PADDING_XL, PADDING_XL)));
    }

    public static JScrollPane createStyledScrollPane(Component component) {
        JScrollPane scrollPane = new JScrollPane(component);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1, true));
        scrollPane.getViewport().setBackground(SURFACE_DARK);
        return scrollPane;
    }

    public static void styleHeaderPanel(JPanel panel) {
        panel.setBackground(SURFACE_DARK);
        panel.setBorder(new EmptyBorder(0, 0, PADDING_LARGE, 0));
    }

    /**
     * Configure global UI Manager properties for the Dark Theme.
     * Call this AFTER setting the LookAndFeel.
     */
    public static void setupDarkTheme() {
        // Panel
        UIManager.put("Panel.background", BACKGROUND_DARK);
        UIManager.put("Panel.foreground", TEXT_PRIMARY);

        // OptionPane
        UIManager.put("OptionPane.background", BACKGROUND_DARK);
        UIManager.put("OptionPane.foreground", TEXT_PRIMARY);
        UIManager.put("OptionPane.messageForeground", TEXT_PRIMARY);
        UIManager.put("OptionPane.messageBackground", BACKGROUND_DARK);
        UIManager.put("OptionPane.border", BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(PADDING_LARGE, PADDING_LARGE, PADDING_LARGE, PADDING_LARGE)));
        UIManager.put("OptionPane.buttonFont", BOLD_FONT);

        // Label
        UIManager.put("Label.foreground", TEXT_PRIMARY);
        UIManager.put("Label.background", BACKGROUND_DARK);
        UIManager.put("Label.font", REGULAR_FONT);

        // TextField
        UIManager.put("TextField.background", SURFACE_LIGHTER);
        UIManager.put("TextField.foreground", TEXT_PRIMARY);
        UIManager.put("TextField.caretForeground", ACCENT_BLUE);
        UIManager.put("TextField.border", BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(PADDING_SMALL, PADDING_MEDIUM, PADDING_SMALL, PADDING_MEDIUM)));
        UIManager.put("TextField.font", REGULAR_FONT);

        // TextArea
        UIManager.put("TextArea.background", SURFACE_LIGHTER);
        UIManager.put("TextArea.foreground", TEXT_PRIMARY);
        UIManager.put("TextArea.caretForeground", ACCENT_BLUE);
        UIManager.put("TextArea.font", new Font("Consolas", Font.PLAIN, 13));

        // ComboBox
        UIManager.put("ComboBox.background", SURFACE_LIGHTER);
        UIManager.put("ComboBox.foreground", TEXT_PRIMARY);
        UIManager.put("ComboBox.selectionBackground", ACCENT_BLUE);
        UIManager.put("ComboBox.selectionForeground", Color.WHITE);
        UIManager.put("ComboBox.buttonBackground", SURFACE_LIGHTER);
        UIManager.put("Table.background", SURFACE_DARK);
        UIManager.put("Table.foreground", TEXT_PRIMARY);
        UIManager.put("Table.selectionBackground", ACCENT_BLUE);
        UIManager.put("Table.selectionForeground", Color.WHITE);
        UIManager.put("Table.gridColor", BORDER_COLOR);
        UIManager.put("TableHeader.background", ACCENT_BLUE);
        UIManager.put("TableHeader.foreground", Color.WHITE);
        UIManager.put("TableHeader.font", BOLD_FONT);

        // ScrollPane
        UIManager.put("ScrollPane.background", SURFACE_DARK);
        UIManager.put("Viewport.background", SURFACE_DARK);
    }
}