# 🛒 Supermarket Checkout System (`supermarket_checkout`)

---

## 📌 About the Project

Back in 1999, before high-speed internet reached every neighborhood in Manaus, local businesses relied on standalone software built for a single workstation or a local network. These programs processed sales quickly and stored data locally in text or binary files.

**Mercadinho São Francisco**, located in the Coroado neighborhood, previously recorded sales on paper notebooks and manual calculators, leading to frequent cash drawer mismatches at closing time.

This project delivers a **Minimum Viable Product (MVP)** for a desktop Point-of-Sale (POS) system built in **Java Swing**. It runs completely **offline**, calculates item subtotals, handles multiple payment methods (Cash, Card, and Credit Tab/"Fiado"), calculates change, enforces credit limit policies, and generates an end-of-day total summary grouped by payment method.

---

## ✨ Features (MVP Scope)

- 🛍️ **Multi-Item Cart Registration**: Register multiple items per sale using product code and quantity.
- ❌ **Item Cancellation**: Remove selected items from the cart before completing the sale.
- 💵 **Cash Payments & Change Calculation**: Enter the amount paid in cash and automatically compute the change due.
- 💳 **Card Payments**: Register card sales instantly.
- 📝 **Customer Store Credit ("Fiado")**: Process credit sales only for registered customers, ensuring the total sale does not exceed the customer's available credit limit.
- 📊 **Daily Closing Summary**: Display total daily sales broken down by Cash, Card, and Store Credit ("Fiado").

---

## 🛠️ System Architecture

The project follows a clean **Model-View-Controller (MVC)** design pattern, keeping UI rendering separated from business logic and data structures.

```
src/
 ├── model/
 │   ├── Product.java         # Product entity (price stored in cents)
 │   ├── Customer.java        # Registered store credit customer & limits
 │   └── CartItem.java        # Quantity and subtotal calculations
 └── view/
     └── CheckoutFrame.java   # Swing graphical user interface & listeners
```

> **Note on Financial Accuracy**: To avoid floating-point rounding errors (e.g., `0.1 + 0.2 = 0.30000000000000004`), all monetary calculations are performed and stored internally as **integer cents** (`long`). Formatted currency displays values in Brazilian Real (`R$`).

---

## 🚀 Getting Started & Setup

### Prerequisites
- **Java SE Development Kit (JDK) 17 or 21+**
- **Eclipse IDE** (or IntelliJ IDEA / VS Code)

### Running in Eclipse IDE

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/Jos4f4/supermarket_checkout.git
   ```
2. **Import into Eclipse**:
   - Open Eclipse IDE.
   - Go to `File` ➔ `Import...` ➔ `Existing Projects into Workspace`.
   - Select the root folder of the cloned repository `supermarket_checkout` and click **Finish**.

3. **Configure Module/Build Path (If needed)**:
   - Ensure the JDK system library is attached under **Java Build Path**.
   - If using Java 9+ module configuration, ensure `module-info.java` includes:
     ```java
     module supermarket_checkout {
         requires java.desktop;
     }
     ```

4. **Run the Application**:
   - Right-click `CheckoutFrame.java` in the Package Explorer.
   - Select **Run As** ➔ **Java Application**.

---

## 🧪 Acceptance Test Suite (Formulário F-03)

Use these exact step-by-step test scenarios during live demo presentations:

| Test Code | Scenario | Test Execution Steps | Expected Outcome |
| :--- | :--- | :--- | :--- |
| **CT-01** | **Cash Change Calculation** | Add product `78910` (R$ 25.00). Select **DINHEIRO**, enter `30.00` in the payment field, and click **Finalizar Venda**. | Transaction completes; change displays **R$ 5,00**. |
| **CT-02** | **Item Cancellation** | Add products `78910` (R$ 25.00) and `78911` (R$ 8.50). Select the `78911` row in the table and click **Cancelar Item Selecionado**. | Item `78911` is removed; total recalculates to **R$ 25,00**. |
| **CT-03** | **Approved Credit Tab ("Fiado")** | Add product `78910` (R$ 25.00). Select **FIADO**, enter CPF `123456` (Limit: R$ 500.00), and click **Finalizar Venda**. | Sale authorized; remaining customer credit updates to **R$ 475,00**. |
| **CT-04** | **Exceeded Credit Tab Limit** | Set Quantity to `25` for item `78910` (Total: R$ 625.00). Select **FIADO**, enter CPF `123456`, and click **Finalizar Venda**. | Sale blocked with error: *"Venda excede o limite fiado do cliente"*. |
| **CT-05** | **Card Payment** | Add product `78911` (R$ 8.50). Select **CARTAO** and click **Finalizar Venda**. | Transaction completes without change calculation (`Troco: R$ 0,00`). |
| **CT-06** | **Daily Closing Summary** | Click **Ver Fechamento do Dia** after performing sales. | Summary modal displays totals separated by Cash, Card, and Credit Tab, plus grand total. |

---

## 📝 Pre-loaded Mock Data

For testing convenience, the application loads the following mock dataset into local memory on startup:

### Products Catalog
- `78910` — Arroz 5kg (R$ 25,00)
- `78911` — Feijão 1kg (R$ 8,50)
- `78912` — Leite 1L (R$ 5,20)

### Registered Customers ("Fiado")
- **CPF:** `123456` | **Name:** João Silva | **Initial Credit Limit:** R$ 500,00

---

## 📄 Out of Scope for MVP
The following features are intentionally deferred for future releases:
- Real-time Inventory Management (Stock deduction)
- Electronic Fiscal Note Printing (NFC-e / SAT)
- Barcode Scanner Hardware Integration
- Persistent Database Integration (Database Server / SQL)
