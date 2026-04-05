/**
 * SearchAlgorithms.java
 * Contains sequential search, binary search, and name-based search implementations.
 *
 * Course: CSC 483.1 – Algorithms Analysis and Design
 * Student: Boniface Chigozie David | U2022/5570060
 * University of Port Harcourt, Faculty of Computing
 */
public class SearchAlgorithms {

    // =======================================================================
    // Part B – Method 1: Sequential Search by ID
    // Time Complexity:
    //   Best Case:    O(1)  – target found at index 0
    //   Average Case: O(n/2) ≈ O(n)  – target at middle on average
    //   Worst Case:   O(n)  – target at last position or not present
    // =======================================================================
    /**
     * Searches the products array sequentially for a product matching targetId.
     *
     * @param products  Unsorted or sorted array of Product objects
     * @param targetId  The product ID to search for
     * @return          The matching Product, or null if not found
     */
    public static Product sequentialSearchById(Product[] products, int targetId) {
        for (Product product : products) {       // iterate every element
            if (product.getProductId() == targetId) {
                return product;                  // found – return immediately
            }
        }
        return null;                             // not found
    }

    // =======================================================================
    // Part B – Method 2: Binary Search by ID
    // PRECONDITION: products[] must be sorted in ascending order by productId.
    // Time Complexity:
    //   Best Case:    O(1)   – target found at first mid calculation
    //   Average Case: O(log n)
    //   Worst Case:   O(log n)
    // =======================================================================
    /**
     * Searches a sorted products array using binary search for targetId.
     *
     * @param products  Array of Product objects sorted by productId (ascending)
     * @param targetId  The product ID to search for
     * @return          The matching Product, or null if not found
     */
    public static Product binarySearchById(Product[] products, int targetId) {
        int left  = 0;
        int right = products.length - 1;

        while (left <= right) {
            // Avoids integer overflow vs (left + right) / 2
            int mid   = left + (right - left) / 2;
            int midId = products[mid].getProductId();

            if (midId == targetId) {
                return products[mid];           // exact match
            } else if (midId < targetId) {
                left  = mid + 1;                // search right half
            } else {
                right = mid - 1;                // search left half
            }
        }
        return null;                            // not found
    }

    // =======================================================================
    // Part B – Method 3: Search by Name (Sequential – names are unsorted)
    // Time Complexity:
    //   Best Case:    O(1)
    //   Average Case: O(n/2) ≈ O(n)
    //   Worst Case:   O(n)
    // =======================================================================
    /**
     * Searches the products array sequentially for a matching product name.
     * Case-insensitive comparison is used for robustness.
     *
     * @param products    Array of Product objects (any order)
     * @param targetName  The product name to search for
     * @return            The matching Product, or null if not found
     */
    public static Product searchByName(Product[] products, String targetName) {
        for (Product product : products) {
            if (product.getProductName().equalsIgnoreCase(targetName)) {
                return product;
            }
        }
        return null;
    }
}
