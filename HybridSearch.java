import java.util.Arrays;
import java.util.TreeMap;

/**
 * HybridSearch.java
 * Part C – Hybrid Search Strategy for TechMart.
 *
 * Design Goals:
 *  1. Maintains a sorted Product[] (by productId) for O(log n) binary search by ID.
 *  2. Maintains a TreeMap<String, Product> name index for O(log n) search by name
 *     – avoids rebuilding the entire structure on every insertion.
 *  3. addProduct() inserts into both structures efficiently.
 *
 * Time Complexity Analysis:
 *  ┌─────────────────────┬──────────────┬──────────────────────────────────────┐
 *  │ Operation           │ Complexity   │ Reason                               │
 *  ├─────────────────────┼──────────────┼──────────────────────────────────────┤
 *  │ searchById()        │ O(log n)     │ Binary search on sorted array        │
 *  │ searchByName()      │ O(log n)     │ TreeMap get() – balanced BST         │
 *  │ addProduct()        │ O(n)         │ Array shift to maintain sorted order │
 *  │   - findInsertPos   │ O(log n)     │ Binary search for insert position    │
 *  │   - array shift     │ O(n)         │ System.arraycopy dominates           │
 *  │   - nameIndex put   │ O(log n)     │ TreeMap insertion                    │
 *  └─────────────────────┴──────────────┴──────────────────────────────────────┘
 *
 * Course: CSC 483.1 – Algorithms Analysis and Design
 * Student: Boniface Chigozie David | U2022/5570060
 * University of Port Harcourt, Faculty of Computing
 */
public class HybridSearch {

    private Product[] products;                       // sorted array by productId
    private int       size;                           // number of products currently stored
    private final TreeMap<String, Product> nameIndex; // O(log n) name lookup

    // ---------------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------------
    public HybridSearch(int initialCapacity) {
        this.products  = new Product[initialCapacity];
        this.size      = 0;
        this.nameIndex = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    }

    // ---------------------------------------------------------------
    // addProduct – insert maintaining sorted order by productId
    // Time Complexity: O(n) due to array shift
    // ---------------------------------------------------------------
    /**
     * Adds a new product while:
     *  (a) keeping the internal sorted array sorted by productId, and
     *  (b) updating the name index in O(log n).
     *
     * @param newProduct  The product to add
     */
    public void addProduct(Product newProduct) {
        // Grow the backing array if needed (amortised O(1))
        if (size >= products.length) {
            products = Arrays.copyOf(products, products.length * 2);
        }

        // Step 1 – Binary search for the correct sorted insertion position: O(log n)
        int insertPos = findInsertPosition(newProduct.getProductId());

        // Step 2 – Shift existing elements right by one: O(n)
        System.arraycopy(products, insertPos, products, insertPos + 1, size - insertPos);

        // Step 3 – Place the new product
        products[insertPos] = newProduct;
        size++;

        // Step 4 – Update the name index: O(log n)
        nameIndex.put(newProduct.getProductName(), newProduct);
    }

    /**
     * Finds the index at which a product with the given id should be inserted
     * to keep the array sorted (binary search for insert position).
     */
    private int findInsertPosition(int targetId) {
        int left  = 0;
        int right = size;       // right = size (one past last valid index)
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (products[mid].getProductId() < targetId) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }
        return left;
    }

    // ---------------------------------------------------------------
    // searchById – binary search on sorted products[]
    // Time Complexity: O(log n)
    // ---------------------------------------------------------------
    /**
     * Searches for a product by ID using binary search.
     *
     * @param targetId  Product ID to find
     * @return          The matching Product, or null if not found
     */
    public Product searchById(int targetId) {
        int left  = 0;
        int right = size - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            int mid_id = products[mid].getProductId();
            if (mid_id == targetId)  return products[mid];
            else if (mid_id < targetId) left  = mid + 1;
            else                        right = mid - 1;
        }
        return null;
    }

    // ---------------------------------------------------------------
    // searchByName – TreeMap lookup
    // Time Complexity: O(log n)
    // ---------------------------------------------------------------
    /**
     * Searches for a product by name using the TreeMap index.
     * Case-insensitive (TreeMap was built with CASE_INSENSITIVE_ORDER).
     *
     * @param name  Product name to look up
     * @return      The matching Product, or null if not found
     */
    public Product searchByName(String name) {
        return nameIndex.get(name);
    }

    // ---------------------------------------------------------------
    // Utility accessors
    // ---------------------------------------------------------------
    /** @return Number of products stored */
    public int getSize() { return size; }

    /** @return A copy of the active portion of the sorted products array */
    public Product[] getProducts() {
        return Arrays.copyOf(products, size);
    }

    /** @return The internal name index (read-only view for testing) */
    public TreeMap<String, Product> getNameIndex() {
        return nameIndex;
    }
}
