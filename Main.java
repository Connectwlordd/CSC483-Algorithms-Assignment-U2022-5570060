import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Main.java
 * TechMart Search Performance Analysis – Main driver program.
 *
 * Generates 100,000 Product objects with random IDs in [1, 200,000],
 * then benchmarks sequential search, binary search, and hybrid name search,
 * printing a formatted performance table as required.
 *
 * Course: CSC 483.1 – Algorithms Analysis and Design
 * Student: Boniface Chigozie David | U2022/5570060
 * University of Port Harcourt, Faculty of Computing
 */
public class Main {

    // ---------------------------------------------------------------
    // Constants
    // ---------------------------------------------------------------
    private static final int     DATASET_SIZE  = 100_000;
    private static final int     MAX_ID        = 200_000;
    private static final int     WARM_UP_RUNS  = 5;      // JVM warm-up iterations
    private static final int     BENCH_RUNS    = 20;     // timed iterations for averaging
    private static final long    NS_PER_MS     = 1_000_000L;

    // Sample product name pool (used to generate realistic names)
    private static final String[] ADJECTIVES = {
        "Smart", "Ultra", "Pro", "Lite", "Max", "Nano", "Flex", "Swift",
        "Quantum", "Prime", "Elite", "Turbo", "Mega", "Eco", "Slim"
    };
    private static final String[] NOUNS = {
        "Laptop", "Phone", "Tablet", "Watch", "Speaker", "Camera",
        "Keyboard", "Mouse", "Headset", "Monitor", "Router", "Charger",
        "Drone", "Printer", "Scanner", "Projector", "Console", "Hub"
    };
    private static final String[] CATEGORIES = {
        "Electronics", "Wearables", "Peripherals", "Networking",
        "Storage", "Audio", "Gaming", "Imaging"
    };

    // ---------------------------------------------------------------
    // Entry point
    // ---------------------------------------------------------------
    public static void main(String[] args) {

        sep();
        System.out.println("TECHMART SEARCH PERFORMANCE ANALYSIS (n = 100,000 products)");
        sep();
        System.out.println();

        // 1. Generate unsorted dataset
        System.out.println("Generating dataset of " + DATASET_SIZE + " products...");
        Product[] unsorted = generateDataset(DATASET_SIZE);

        // 2. Create a sorted copy (by productId) for binary search
        Product[] sorted = Arrays.copyOf(unsorted, unsorted.length);
        Arrays.sort(sorted);   // uses Product.compareTo() → sorts by productId

        System.out.println("Dataset ready.  Sorted copy created for binary search.");
        System.out.println();

        // 3. Choose target IDs for each scenario
        int bestCaseId   = unsorted[0].getProductId();                // first element in unsorted
        int middleId     = sorted[sorted.length / 2].getProductId();  // middle of sorted array
        int randomId     = sorted[new Random().nextInt(DATASET_SIZE)].getProductId();
        int notFoundId   = MAX_ID + 1;                                // guaranteed absent

        // ---------------------------------------------------------------
        // SEQUENTIAL SEARCH
        // ---------------------------------------------------------------
        System.out.println("SEQUENTIAL SEARCH:");
        double seqBest    = benchmarkSequential(unsorted, bestCaseId,  true);
        double seqAverage = benchmarkSequential(unsorted, randomId,    false);
        double seqWorst   = benchmarkSequential(unsorted, notFoundId,  false);
        System.out.printf("  Best Case  (ID found at position 0)  : %.3f ms%n", seqBest);
        System.out.printf("  Average Case (random ID)             : %.3f ms%n", seqAverage);
        System.out.printf("  Worst Case (ID not found)            : %.3f ms%n", seqWorst);
        System.out.println();

        // ---------------------------------------------------------------
        // BINARY SEARCH
        // ---------------------------------------------------------------
        System.out.println("BINARY SEARCH:");
        double binBest    = benchmarkBinary(sorted, middleId,   true);
        double binAverage = benchmarkBinary(sorted, randomId,   false);
        double binWorst   = benchmarkBinary(sorted, notFoundId, false);
        System.out.printf("  Best Case  (ID at middle)            : %.3f ms%n", binBest);
        System.out.printf("  Average Case (random ID)             : %.3f ms%n", binAverage);
        System.out.printf("  Worst Case (ID not found)            : %.3f ms%n", binWorst);
        System.out.println();

        // ---------------------------------------------------------------
        // Performance ratio
        // ---------------------------------------------------------------
        double speedup = seqAverage / binAverage;
        System.out.printf("PERFORMANCE IMPROVEMENT: Binary search is ~%.0fx faster on average%n%n", speedup);

        // ---------------------------------------------------------------
        // HYBRID NAME SEARCH
        // ---------------------------------------------------------------
        System.out.println("HYBRID NAME SEARCH (TreeMap index):");
        HybridSearch hybrid = buildHybrid(unsorted);

        // Pick a valid name from the dataset
        String targetName = unsorted[DATASET_SIZE / 2].getProductName();
        double hybridSearch = benchmarkHybridSearch(hybrid, targetName);
        double hybridInsert = benchmarkHybridInsert(hybrid);
        System.out.printf("  Average search time (by name)        : %.3f ms%n", hybridSearch);
        System.out.printf("  Average insert time (addProduct)     : %.3f ms%n", hybridInsert);
        System.out.println();

        // ---------------------------------------------------------------
        // Summary table
        // ---------------------------------------------------------------
        printSummaryTable(seqBest, seqAverage, seqWorst,
                          binBest, binAverage, binWorst,
                          speedup, hybridSearch, hybridInsert);
        sep();
    }

    // ---------------------------------------------------------------
    // Dataset generation
    // ---------------------------------------------------------------
    private static Product[] generateDataset(int size) {
        Random     rng      = new Random(42L);   // fixed seed for reproducibility
        Set<Integer> usedIds = new HashSet<>();
        Product[]  data     = new Product[size];

        for (int i = 0; i < size; i++) {
            // Unique random ID in [1, MAX_ID]
            int id;
            do { id = rng.nextInt(MAX_ID) + 1; } while (!usedIds.add(id));

            String name     = ADJECTIVES[rng.nextInt(ADJECTIVES.length)]
                            + " " + NOUNS[rng.nextInt(NOUNS.length)]
                            + " " + (i + 1);          // make name unique by appending index
            String category = CATEGORIES[rng.nextInt(CATEGORIES.length)];
            double price    = 10.0 + rng.nextDouble() * 2490.0;  // $10 – $2500
            int    stock    = rng.nextInt(501);                   // 0 – 500 units

            data[i] = new Product(id, name, category, price, stock);
        }
        return data;
    }

    // ---------------------------------------------------------------
    // Benchmarking helpers
    // ---------------------------------------------------------------
    private static double benchmarkSequential(Product[] arr, int targetId, boolean warmUp) {
        // JVM warm-up (results discarded)
        int warmRuns = warmUp ? WARM_UP_RUNS : 2;
        for (int i = 0; i < warmRuns; i++) {
            SearchAlgorithms.sequentialSearchById(arr, targetId);
        }
        // Timed runs
        long total = 0;
        for (int i = 0; i < BENCH_RUNS; i++) {
            long start = System.nanoTime();
            SearchAlgorithms.sequentialSearchById(arr, targetId);
            total += System.nanoTime() - start;
        }
        return (double) total / BENCH_RUNS / NS_PER_MS;
    }

    private static double benchmarkBinary(Product[] arr, int targetId, boolean warmUp) {
        int warmRuns = warmUp ? WARM_UP_RUNS : 2;
        for (int i = 0; i < warmRuns; i++) {
            SearchAlgorithms.binarySearchById(arr, targetId);
        }
        long total = 0;
        for (int i = 0; i < BENCH_RUNS; i++) {
            long start = System.nanoTime();
            SearchAlgorithms.binarySearchById(arr, targetId);
            total += System.nanoTime() - start;
        }
        return (double) total / BENCH_RUNS / NS_PER_MS;
    }

    private static HybridSearch buildHybrid(Product[] source) {
        HybridSearch hs = new HybridSearch(source.length * 2);
        for (Product p : source) {
            hs.addProduct(p);
        }
        return hs;
    }

    private static double benchmarkHybridSearch(HybridSearch hs, String name) {
        for (int i = 0; i < WARM_UP_RUNS; i++) hs.searchByName(name);
        long total = 0;
        for (int i = 0; i < BENCH_RUNS; i++) {
            long start = System.nanoTime();
            hs.searchByName(name);
            total += System.nanoTime() - start;
        }
        return (double) total / BENCH_RUNS / NS_PER_MS;
    }

    private static double benchmarkHybridInsert(HybridSearch hs) {
        // Insert 20 products into the hybrid structure and average the time
        long total = 0;
        for (int i = 0; i < BENCH_RUNS; i++) {
            Product p = new Product(
                MAX_ID + 100 + i,
                "BenchmarkProduct " + i,
                "Test",
                99.99,
                10
            );
            long start = System.nanoTime();
            hs.addProduct(p);
            total += System.nanoTime() - start;
        }
        return (double) total / BENCH_RUNS / NS_PER_MS;
    }

    // ---------------------------------------------------------------
    // Formatted output
    // ---------------------------------------------------------------
    private static void printSummaryTable(
            double sB, double sA, double sW,
            double bB, double bA, double bW,
            double speedup, double hSearch, double hInsert) {

        System.out.println();
        System.out.println("PERFORMANCE SUMMARY TABLE");
        System.out.println("-".repeat(70));
        System.out.printf("%-30s %12s %12s %12s%n",
                "Search Method", "Best (ms)", "Avg (ms)", "Worst (ms)");
        System.out.println("-".repeat(70));
        System.out.printf("%-30s %12.3f %12.3f %12.3f%n",
                "Sequential Search (by ID)", sB, sA, sW);
        System.out.printf("%-30s %12.3f %12.3f %12.3f%n",
                "Binary Search (by ID)", bB, bA, bW);
        System.out.printf("%-30s %12.3f %12s %12s%n",
                "Hybrid Name Search", hSearch, "N/A", "N/A");
        System.out.printf("%-30s %12.3f %12s %12s%n",
                "Hybrid Insert", hInsert, "N/A", "N/A");
        System.out.println("-".repeat(70));
        System.out.printf("Speed improvement (avg): Binary search is ~%.0fx faster than sequential%n", speedup);
        System.out.println();
    }

    private static void sep() {
        System.out.println("=".repeat(70));
    }
}
