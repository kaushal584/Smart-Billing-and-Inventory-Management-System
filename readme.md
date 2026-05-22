# Online Billing & Inventory Management System

A modern, Java-based desktop application for managing products, customers, and invoices with a clean, high-contrast dark UI.  
Supports invoice generation, PDF export, and flexible discount/tax strategies.

---

## Features

- **Product Management:** Add, view, and delete products.
- **Customer Management:** Add, view, and delete customers.
- **Invoice Generation:** Create invoices, add items, apply discounts and taxes.
- **Invoice History:** View and export past invoices (PDF/text).
- **Analytics:** (Optional panel for future analytics features)
- **Modern UI:** High-contrast dark theme for comfortable use.
- **Extensible:** Easily add new discount or tax strategies.

---

## Getting Started

### Prerequisites

- Java 8 or higher
- [itextpdf-5.5.13.3.jar](https://github.com/itext/itextpdf/releases/tag/5.5.13.3) (already included in `lib/`)

### Build & Run

1. **Clone or Download** this repository.
2. **Open a terminal** in the project root.
3. **Compile & Run:**
    ```bat
    run.bat
    ```
    Or manually:
    ```bat
    javac -cp "lib/itextpdf-5.5.13.3.jar;src" -d bin src\com\billing\system\**\*.java
    java -cp "bin;lib/itextpdf-5.5.13.3.jar" com.billing.system.Main
    ```

---

## Project Structure

```
src/
  com/billing/system/
    model/         # Data models (Product, Customer, Invoice, etc.)
    repository/    # Data storage (CSV, in-memory)
    service/       # Business logic (InvoiceService, Calculator)
    strategy/      # Discount & Tax strategies
    ui/            # Swing UI panels and helpers
    Main.java      # Application entry point
lib/
  itextpdf-5.5.13.3.jar
bin/
  ...             # Compiled classes
run.bat           # Windows build & run script
data/             # SQLite database file created at runtime
```

---

## Storage

The app now stores data in a local SQLite database at `data/billing.db`.

On first run, the app imports the existing `customers.csv`, `products.csv`, and `invoices.csv` files into SQLite so your current data is preserved.

---

## Customization

- **UI Colors:** Edit `UIHelper.java` for theme changes.
- **Add Discount/Tax Strategies:** Implement `DiscountStrategy` or `TaxStrategy` in `strategy/` and register in the UI.

---

