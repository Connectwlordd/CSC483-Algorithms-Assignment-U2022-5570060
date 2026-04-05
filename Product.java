/**
 * Product.java
 * Represents a product in TechMart's inventory catalog.
 *
 * Course: CSC 483.1 – Algorithms Analysis and Design
 * Student: Boniface Chigozie David | U2022/5570060
 * University of Port Harcourt, Faculty of Computing
 */
public class Product implements Comparable<Product> {

    private int    productId;
    private String productName;
    private String category;
    private double price;
    private int    stockQuantity;

    // ---------------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------------
    public Product(int productId, String productName,
                   String category, double price, int stockQuantity) {
        this.productId     = productId;
        this.productName   = productName;
        this.category      = category;
        this.price         = price;
        this.stockQuantity = stockQuantity;
    }

    // ---------------------------------------------------------------
    // Getters
    // ---------------------------------------------------------------
    public int    getProductId()     { return productId;     }
    public String getProductName()   { return productName;   }
    public String getCategory()      { return category;      }
    public double getPrice()         { return price;         }
    public int    getStockQuantity() { return stockQuantity; }

    // ---------------------------------------------------------------
    // Comparable – sorts products by productId (required for binary search)
    // ---------------------------------------------------------------
    @Override
    public int compareTo(Product other) {
        return Integer.compare(this.productId, other.productId);
    }

    // ---------------------------------------------------------------
    // Utility
    // ---------------------------------------------------------------
    @Override
    public String toString() {
        return String.format(
            "Product{id=%-6d  name='%-30s'  category='%-12s'  price=%8.2f  stock=%d}",
            productId, productName, category, price, stockQuantity
        );
    }
}
