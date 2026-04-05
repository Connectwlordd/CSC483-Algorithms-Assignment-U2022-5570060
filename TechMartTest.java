import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;

/**
 * TechMartTest.java
 * JUnit 5 test suite verifying correctness of all search and insert operations.
 *
 * Test categories:
 *  1. Product class tests
 *  2. Sequential search tests
 *  3. Binary search tests
 *  4. Name search tests
 *  5. HybridSearch tests (insert, searchById, searchByName)
 *
 * Course: CSC 483.1 – Algorithms Analysis and Design
 * Student: Boniface Chigozie David | U2022/5570060
 * University of Port Harcourt, Faculty of Computing
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TechMartTest {

    // ---------------------------------------------------------------
    // Shared fixtures
    // ---------------------------------------------------------------
    private static Product[] sampleProducts;    // unsorted (as inserted)
    private static Product[] sortedProducts;    // sorted copy for binary search
    private static HybridSearch hybrid;

    @BeforeAll
    static void setup() {
        // Small representative dataset
        sampleProducts = new Product[]{
            new Product(50,  "Pro Laptop",    "Electronics",  1200.00, 30),
            new Product(10,  "Smart Watch",   "Wearables",     350.00, 80),
            new Product(80,  "Ultra Camera",  "Imaging",       900.00, 15),
            new Product(30,  "Slim Tablet",   "Electronics",   500.00, 60),
            new Product(70,  "Flex Router",   "Networking",    120.00, 45),
            new Product(20,  "Nano Speaker",  "Audio",          80.00, 120),
            new Product(90,  "Max Headset",   "Audio",         220.00, 55),
            new Product(5,   "Prime Charger", "Peripherals",    25.00, 200),
            new Product(60,  "Swift Drone",   "Gaming",        750.00, 10),
            new Product(40,  "Turbo Keyboard","Peripherals",    95.00, 90),
        };

        // Sorted copy
        sortedProducts = Arrays.copyOf(sampleProducts, sampleProducts.length);
        Arrays.sort(sortedProducts);   // sorts by productId via compareTo

        // Hybrid search structure
        hybrid = new HybridSearch(20);
        for (Product p : sampleProducts) {
            hybrid.addProduct(p);
        }
    }

    // ===============================================================
    // 1. Product Class Tests
    // ===============================================================

    @Test @Order(1)
    @DisplayName("Product: getters return correct values")
    void testProductGetters() {
        Product p = new Product(42, "Test Product", "TestCat", 99.99, 5);
        assertEquals(42,           p.getProductId(),     "ID mismatch");
        assertEquals("Test Product", p.getProductName(), "Name mismatch");
        assertEquals("TestCat",    p.getCategory(),      "Category mismatch");
        assertEquals(99.99,        p.getPrice(), 0.001,  "Price mismatch");
        assertEquals(5,            p.getStockQuantity(), "Stock mismatch");
    }

    @Test @Order(2)
    @DisplayName("Product: compareTo sorts by productId ascending")
    void testProductCompareTo() {
        Product p1 = new Product(1,  "A", "X", 10.0, 1);
        Product p2 = new Product(5,  "B", "Y", 20.0, 2);
        Product p3 = new Product(10, "C", "Z", 30.0, 3);

        assertTrue(p1.compareTo(p2) < 0,  "p1 should be less than p2");
        assertTrue(p3.compareTo(p2) > 0,  "p3 should be greater than p2");
        assertEquals(0, p2.compareTo(p2), "Same product should equal 0");
    }

    @Test @Order(3)
    @DisplayName("Product: toString contains key fields")
    void testProductToString() {
        Product p = new Product(7, "MyPhone", "Electronics", 499.99, 10);
        String s = p.toString();
        assertTrue(s.contains("7"),            "toString missing ID");
        assertTrue(s.contains("MyPhone"),      "toString missing name");
        assertTrue(s.contains("Electronics"), "toString missing category");
    }

    // ===============================================================
    // 2. Sequential Search Tests
    // ===============================================================

    @Test @Order(4)
    @DisplayName("Sequential Search: returns correct product when found")
    void testSequentialSearchFound() {
        Product result = SearchAlgorithms.sequentialSearchById(sampleProducts, 30);
        assertNotNull(result,                      "Should find product with ID 30");
        assertEquals(30,          result.getProductId(), "Wrong product returned");
        assertEquals("Slim Tablet", result.getProductName());
    }

    @Test @Order(5)
    @DisplayName("Sequential Search: returns null for absent ID")
    void testSequentialSearchNotFound() {
        Product result = SearchAlgorithms.sequentialSearchById(sampleProducts, 999);
        assertNull(result, "Should return null for non-existent ID");
    }

    @Test @Order(6)
    @DisplayName("Sequential Search: finds first element (best case)")
    void testSequentialSearchBestCase() {
        // ID 50 is at index 0 of sampleProducts
        Product result = SearchAlgorithms.sequentialSearchById(sampleProducts, 50);
        assertNotNull(result);
        assertEquals("Pro Laptop", result.getProductName());
    }

    @Test @Order(7)
    @DisplayName("Sequential Search: handles single-element array")
    void testSequentialSearchSingleElement() {
        Product[] single = {new Product(1, "Solo", "X", 1.0, 1)};
        assertNotNull(SearchAlgorithms.sequentialSearchById(single, 1));
        assertNull(SearchAlgorithms.sequentialSearchById(single, 2));
    }

    @Test @Order(8)
    @DisplayName("Sequential Search: handles empty array")
    void testSequentialSearchEmpty() {
        Product[] empty = {};
        assertNull(SearchAlgorithms.sequentialSearchById(empty, 5));
    }

    // ===============================================================
    // 3. Binary Search Tests
    // ===============================================================

    @Test @Order(9)
    @DisplayName("Binary Search: returns correct product when found")
    void testBinarySearchFound() {
        Product result = SearchAlgorithms.binarySearchById(sortedProducts, 70);
        assertNotNull(result,                    "Should find product with ID 70");
        assertEquals(70,        result.getProductId());
        assertEquals("Flex Router", result.getProductName());
    }

    @Test @Order(10)
    @DisplayName("Binary Search: returns null for absent ID")
    void testBinarySearchNotFound() {
        Product result = SearchAlgorithms.binarySearchById(sortedProducts, 999);
        assertNull(result, "Should return null for non-existent ID");
    }

    @Test @Order(11)
    @DisplayName("Binary Search: finds element at middle (best case)")
    void testBinarySearchMiddle() {
        // Middle index of 10-element sorted array is index 5 → ID 50
        int midId = sortedProducts[sortedProducts.length / 2].getProductId();
        Product result = SearchAlgorithms.binarySearchById(sortedProducts, midId);
        assertNotNull(result);
        assertEquals(midId, result.getProductId());
    }

    @Test @Order(12)
    @DisplayName("Binary Search: finds first element (left boundary)")
    void testBinarySearchFirst() {
        Product result = SearchAlgorithms.binarySearchById(sortedProducts, 5);
        assertNotNull(result);
        assertEquals("Prime Charger", result.getProductName());
    }

    @Test @Order(13)
    @DisplayName("Binary Search: finds last element (right boundary)")
    void testBinarySearchLast() {
        Product result = SearchAlgorithms.binarySearchById(sortedProducts, 90);
        assertNotNull(result);
        assertEquals("Max Headset", result.getProductName());
    }

    @Test @Order(14)
    @DisplayName("Binary Search: handles single-element array")
    void testBinarySearchSingleElement() {
        Product[] single = {new Product(42, "Unique", "X", 5.0, 3)};
        assertNotNull(SearchAlgorithms.binarySearchById(single, 42));
        assertNull(SearchAlgorithms.binarySearchById(single, 1));
    }

    // ===============================================================
    // 4. Name Search Tests
    // ===============================================================

    @Test @Order(15)
    @DisplayName("Name Search: finds product by exact name")
    void testSearchByNameFound() {
        Product result = SearchAlgorithms.searchByName(sampleProducts, "Nano Speaker");
        assertNotNull(result);
        assertEquals(20, result.getProductId());
    }

    @Test @Order(16)
    @DisplayName("Name Search: case-insensitive matching")
    void testSearchByNameCaseInsensitive() {
        Product result = SearchAlgorithms.searchByName(sampleProducts, "turbo keyboard");
        assertNotNull(result, "Case-insensitive search should succeed");
        assertEquals(40, result.getProductId());
    }

    @Test @Order(17)
    @DisplayName("Name Search: returns null when name not found")
    void testSearchByNameNotFound() {
        Product result = SearchAlgorithms.searchByName(sampleProducts, "NonExistentProduct XYZ");
        assertNull(result);
    }

    // ===============================================================
    // 5. HybridSearch Tests
    // ===============================================================

    @Test @Order(18)
    @DisplayName("Hybrid: sorted order maintained after addProduct")
    void testHybridSortedOrder() {
        HybridSearch hs = new HybridSearch(10);
        hs.addProduct(new Product(30, "C", "X", 1.0, 1));
        hs.addProduct(new Product(10, "A", "X", 2.0, 2));
        hs.addProduct(new Product(20, "B", "X", 3.0, 3));
        hs.addProduct(new Product(5,  "Z", "X", 4.0, 4));

        Product[] sorted = hs.getProducts();
        assertEquals(5,  sorted[0].getProductId(), "First should be ID 5");
        assertEquals(10, sorted[1].getProductId(), "Second should be ID 10");
        assertEquals(20, sorted[2].getProductId(), "Third should be ID 20");
        assertEquals(30, sorted[3].getProductId(), "Fourth should be ID 30");
    }

    @Test @Order(19)
    @DisplayName("Hybrid: searchById returns correct product")
    void testHybridSearchById() {
        Product result = hybrid.searchById(60);
        assertNotNull(result);
        assertEquals("Swift Drone", result.getProductName());
    }

    @Test @Order(20)
    @DisplayName("Hybrid: searchById returns null for absent ID")
    void testHybridSearchByIdNotFound() {
        assertNull(hybrid.searchById(9999));
    }

    @Test @Order(21)
    @DisplayName("Hybrid: searchByName uses TreeMap index correctly")
    void testHybridSearchByName() {
        Product result = hybrid.searchByName("Ultra Camera");
        assertNotNull(result);
        assertEquals(80, result.getProductId());
    }

    @Test @Order(22)
    @DisplayName("Hybrid: searchByName is case-insensitive")
    void testHybridSearchByNameCaseInsensitive() {
        Product result = hybrid.searchByName("ULTRA CAMERA");
        assertNotNull(result, "Hybrid name search should be case-insensitive");
        assertEquals(80, result.getProductId());
    }

    @Test @Order(23)
    @DisplayName("Hybrid: searchByName returns null for absent name")
    void testHybridSearchByNameNotFound() {
        assertNull(hybrid.searchByName("Ghost Product 9999"));
    }

    @Test @Order(24)
    @DisplayName("Hybrid: size is correct after insertions")
    void testHybridSize() {
        assertEquals(sampleProducts.length, hybrid.getSize(),
                     "Hybrid size should match number of products added");
    }

    @Test @Order(25)
    @DisplayName("Hybrid: capacity grows automatically beyond initial capacity")
    void testHybridAutoGrow() {
        HybridSearch hs = new HybridSearch(2);   // very small initial capacity
        for (int i = 1; i <= 10; i++) {
            hs.addProduct(new Product(i, "Prod " + i, "Cat", i * 10.0, i));
        }
        assertEquals(10, hs.getSize(), "Should store all 10 products after auto-grow");
        Product result = hs.searchById(7);
        assertNotNull(result);
        assertEquals("Prod 7", result.getProductName());
    }
}
