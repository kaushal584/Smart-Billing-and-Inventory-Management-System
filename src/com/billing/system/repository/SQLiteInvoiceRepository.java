package com.billing.system.repository;

import com.billing.system.model.Customer;
import com.billing.system.model.Invoice;
import com.billing.system.model.InvoiceItem;
import com.billing.system.model.InvoiceStatus;
import com.billing.system.model.Product;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class SQLiteInvoiceRepository extends SQLiteRepository<Invoice> {
    public SQLiteInvoiceRepository() {
        super();
    }

    @Override
    protected String tableName() {
        return "invoices";
    }

    @Override
    protected String createTableSql() {
        return "CREATE TABLE IF NOT EXISTS invoices ("
                + "id TEXT PRIMARY KEY,"
                + "customer_id TEXT,"
                + "customer_name TEXT NOT NULL,"
                + "customer_email TEXT,"
                + "customer_phone TEXT,"
                + "customer_type TEXT,"
                + "date TEXT NOT NULL,"
                + "status TEXT NOT NULL,"
                + "total_amount TEXT NOT NULL,"
                + "tax_amount TEXT NOT NULL,"
                + "discount_amount TEXT NOT NULL"
                + ")";
    }

    private String createInvoiceItemsSql() {
        return "CREATE TABLE IF NOT EXISTS invoice_items ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "invoice_id TEXT NOT NULL,"
                + "item_order INTEGER NOT NULL,"
                + "product_id TEXT,"
                + "product_name TEXT NOT NULL,"
                + "product_description TEXT,"
                + "product_category TEXT,"
                + "unit_price TEXT NOT NULL,"
                + "quantity INTEGER NOT NULL,"
                + "FOREIGN KEY(invoice_id) REFERENCES invoices(id) ON DELETE CASCADE"
                + ")";
    }

    @Override
    protected String upsertSql() {
        return "INSERT INTO invoices (id, customer_id, customer_name, customer_email, customer_phone, customer_type, date, status, total_amount, tax_amount, discount_amount) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) "
                + "ON CONFLICT(id) DO UPDATE SET customer_id=excluded.customer_id, customer_name=excluded.customer_name, customer_email=excluded.customer_email, customer_phone=excluded.customer_phone, customer_type=excluded.customer_type, date=excluded.date, status=excluded.status, total_amount=excluded.total_amount, tax_amount=excluded.tax_amount, discount_amount=excluded.discount_amount";
    }

    @Override
    protected String selectAllSql() {
        return "SELECT id, customer_id, customer_name, customer_email, customer_phone, customer_type, date, status, total_amount, tax_amount, discount_amount FROM invoices ORDER BY date DESC";
    }

    @Override
    protected String selectByIdSql() {
        return "SELECT id, customer_id, customer_name, customer_email, customer_phone, customer_type, date, status, total_amount, tax_amount, discount_amount FROM invoices WHERE id = ?";
    }

    @Override
    protected String deleteSql() {
        return "DELETE FROM invoices WHERE id = ?";
    }

    @Override
    protected void bindUpsert(PreparedStatement statement, Invoice entity) throws SQLException {
        Customer customer = entity.getCustomer();
        statement.setString(1, entity.getId());
        statement.setString(2, customer != null ? customer.getId() : null);
        statement.setString(3, customer != null ? customer.getName() : null);
        statement.setString(4, customer != null ? customer.getEmail() : null);
        statement.setString(5, customer != null ? customer.getPhone() : null);
        statement.setString(6, customer != null && customer.getType() != null ? customer.getType().name() : null);
        statement.setString(7, entity.getDate().toString());
        statement.setString(8, entity.getStatus().name());
        statement.setString(9, entity.getTotalAmount().toPlainString());
        statement.setString(10, entity.getTaxAmount().toPlainString());
        statement.setString(11, entity.getDiscountAmount().toPlainString());
    }

    @Override
    protected Invoice mapRow(ResultSet resultSet) throws SQLException {
        String customerId = resultSet.getString("customer_id");
        String customerName = resultSet.getString("customer_name");
        String customerEmail = resultSet.getString("customer_email");
        String customerPhone = resultSet.getString("customer_phone");
        String customerType = resultSet.getString("customer_type");

        Customer customer = new Customer(
                customerId != null ? customerId : resultSet.getString("id"),
                customerName,
                customerEmail,
                customerPhone,
                customerType != null ? Customer.CustomerType.valueOf(customerType) : Customer.CustomerType.REGULAR);

        Invoice invoice = new Invoice(customer);
        invoice.setId(resultSet.getString("id"));
        invoice.setDate(LocalDateTime.parse(resultSet.getString("date")));
        invoice.setStatus(InvoiceStatus.valueOf(resultSet.getString("status")));
        invoice.setTotalAmount(new BigDecimal(resultSet.getString("total_amount")));
        invoice.setTaxAmount(new BigDecimal(resultSet.getString("tax_amount")));
        invoice.setDiscountAmount(new BigDecimal(resultSet.getString("discount_amount")));
        return invoice;
    }

    @Override
    protected String getId(Invoice entity) {
        return entity.getId();
    }

    @Override
    public void save(Invoice entity) {
        try (Connection connection = getConnection()) {
            ensureInvoiceItemSchema(connection);
            connection.setAutoCommit(false);
            try (PreparedStatement invoiceStatement = connection.prepareStatement(upsertSql());
                    PreparedStatement deleteItemsStatement = connection
                            .prepareStatement("DELETE FROM invoice_items WHERE invoice_id = ?");
                    PreparedStatement insertItemStatement = connection.prepareStatement(
                            "INSERT INTO invoice_items (invoice_id, item_order, product_id, product_name, product_description, product_category, unit_price, quantity) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
                bindUpsert(invoiceStatement, entity);
                invoiceStatement.executeUpdate();

                deleteItemsStatement.setString(1, entity.getId());
                deleteItemsStatement.executeUpdate();

                int index = 0;
                for (InvoiceItem item : entity.getItems()) {
                    Product product = item.getProduct();
                    insertItemStatement.setString(1, entity.getId());
                    insertItemStatement.setInt(2, index++);
                    insertItemStatement.setString(3, product.getId());
                    insertItemStatement.setString(4, product.getName());
                    insertItemStatement.setString(5, product.getDescription());
                    insertItemStatement.setString(6, product.getCategory());
                    insertItemStatement.setString(7, item.getUnitPrice().toPlainString());
                    insertItemStatement.setInt(8, item.getQuantity());
                    insertItemStatement.addBatch();
                }

                insertItemStatement.executeBatch();
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to save invoice", e);
        }
    }

    @Override
    public Optional<Invoice> findById(String id) {
        Optional<Invoice> invoice = super.findById(id);
        invoice.ifPresent(this::loadItemsForInvoice);
        return invoice;
    }

    @Override
    public List<Invoice> findAll() {
        List<Invoice> invoices = super.findAll();
        for (Invoice invoice : invoices) {
            loadItemsForInvoice(invoice);
        }
        invoices.sort(Comparator.comparing(Invoice::getDate).reversed());
        return invoices;
    }

    @Override
    public void delete(String id) {
        try (Connection connection = getConnection()) {
            ensureInvoiceItemSchema(connection);
            connection.setAutoCommit(false);
            try (PreparedStatement deleteItemsStatement = connection
                    .prepareStatement("DELETE FROM invoice_items WHERE invoice_id = ?");
                    PreparedStatement deleteInvoiceStatement = connection.prepareStatement(deleteSql())) {
                deleteItemsStatement.setString(1, id);
                deleteItemsStatement.executeUpdate();

                deleteInvoiceStatement.setString(1, id);
                deleteInvoiceStatement.executeUpdate();
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to delete invoice", e);
        }
    }

    @Override
    protected void importLegacyData() {
        if (!isTableEmpty() || !fileExists("invoices.csv")) {
            return;
        }

        for (String line : readAllLines("invoices.csv")) {
            if (line.trim().isEmpty()) {
                continue;
            }

            String[] parts = line.split(",", -1);
            if (parts.length < 7) {
                continue;
            }

            Customer customer = new Customer(
                    "legacy-" + parts[0],
                    parts[1],
                    "",
                    "",
                    Customer.CustomerType.REGULAR);

            Invoice invoice = new Invoice(customer);
            invoice.setId(parts[0]);
            invoice.setDate(LocalDateTime.parse(parts[2]));
            invoice.setStatus(InvoiceStatus.valueOf(parts[3]));
            invoice.setTotalAmount(new BigDecimal(parts[4]));
            invoice.setTaxAmount(new BigDecimal(parts[5]));
            invoice.setDiscountAmount(new BigDecimal(parts[6]));
            save(invoice);
        }
    }

    private void loadItemsForInvoice(Invoice invoice) {
        try (Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT product_id, product_name, product_description, product_category, unit_price, quantity FROM invoice_items WHERE invoice_id = ? ORDER BY item_order")) {
            statement.setString(1, invoice.getId());
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Product product = new Product(
                            resultSet.getString("product_id"),
                            resultSet.getString("product_name"),
                            resultSet.getString("product_description"),
                            new BigDecimal(resultSet.getString("unit_price")),
                            resultSet.getString("product_category"),
                            0,
                            0);
                    invoice.addItem(new InvoiceItem(product, resultSet.getInt("quantity")));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to load invoice items for " + invoice.getId(), e);
        }
    }

    private void ensureInvoiceItemSchema(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(createInvoiceItemsSql());
        }
    }
}