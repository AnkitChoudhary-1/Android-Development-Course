# 📦 Project 3: E-Commerce Product Catalog & Analytics Engine

## 🎯 Overview
Collection transformations form the backbone of modern Android data processing (e.g. searching, filtering, and sorting items before rendering them in a `RecyclerView` or `LazyColumn`). In this project, you will build an in-memory **E-Commerce Analytics & Catalog Engine** in Kotlin using idiomatic functional collection operators.

---

## 🛠️ Concepts Practiced
- Immutable (`List`, `Set`, `Map`) vs Mutable collections
- Filtering & searching (`filter`, `filterNot`, `take`, `drop`)
- Mapping & Transformations (`map`, `flatMap`, `associate`, `associateBy`)
- Grouping & Partitioning (`groupBy`, `partition`)
- Aggregations & Math (`sumOf`, `average`, `maxByOrNull`, `minByOrNull`, `fold`, `reduce`)
- Deduplication & Set operations (`distinct`, `distinctBy`, `intersect`, `union`)

---

## 📋 Requirements & Features

### 1. Data Models
- `Product`: `id`, `name`, `category`, `price`, `rating`, `inStockQuantity`, `tags: Set<String>`
- `OrderItem`: `productId`, `quantity`, `unitPrice`
- `Order`: `orderId`, `customerId`, `items: List<OrderItem>`, `status: String`

### 2. Analytics Pipeline Tasks
Implement the following analytical queries without using traditional `for` loops (use Kotlin collection functional operators):
1. **Category Summary:** Group products by category and calculate total inventory value (`price * inStockQuantity`) for each.
2. **Top Selling Products:** Given a list of orders, find the top 3 best-selling products by quantity sold.
3. **Discount Recommendations:** Find all out-of-stock or low-stock items (`inStockQuantity < 5`) with rating >= 4.0.
4. **Customer Purchase History:** Map each `customerId` to the total monetary amount they have spent across all completed orders.
5. **Unique Popular Tags:** Extract all unique product tags sorted alphabetically, and find tags shared across multiple categories.

---

## 💻 Sample Output

```text
============================================================
       📦 E-COMMERCE PRODUCT CATALOG & ANALYTICS ENGINE      
============================================================
📊 1. INVENTORY VALUE PER CATEGORY:
  - Electronics    : $34,890.00 (Total Items: 42)
  - Footwear       : $12,450.00 (Total Items: 155)
  - Accessories    : $5,230.00 (Total Items: 210)

⭐ 2. TOP 3 BEST SELLING PRODUCTS:
  1. Sony WH-1000XM5       : 18 units sold ($7,182.00 revenue)
  2. Nike Air Max 270      : 14 units sold ($2,099.86 revenue)
  3. Apple Watch Series 9  : 11 units sold ($4,389.00 revenue)

⚠️ 3. LOW STOCK / RESTOCK ALERTS (Rating >= 4.0 & Stock < 5):
  - Sony WH-1000XM5 [Electronics] -> Stock: 3 | Rating: ⭐4.8
  - Minimalist Leather Wallet [Accessories] -> Stock: 2 | Rating: ⭐4.5

💰 4. CUSTOMER LIFETIME VALUE (LTV):
  - Customer #CUST-101 (Rohit)  : $7,462.00 (3 orders)
  - Customer #CUST-102 (Priya)  : $4,389.00 (1 order)
  - Customer #CUST-103 (Amit)   : $1,250.00 (2 orders)

🏷️ 5. ALL UNIQUE PRODUCT TAGS (Alphabetical):
  [audio, bluetooth, casual, leather, noise-cancelling, running, smart, wireless]
============================================================
```

---

## 🚀 How to Run
```bash
kotlinc Solution.kt -include-runtime -d Solution.jar
java -jar Solution.jar
```
