# CSC483-Algorithms-Assignment-[U2022/5570060]

**Course:** CSC 483.1 – Algorithms Analysis and Design  
**Student:** Boniface Chigozie David | U2022/5570060  
**University:** University of Port Harcourt, Faculty of Computing  
**Session:** 2025/2026, First Semester  

---

## Project Structure

```
CSC483/
├── src/
│   ├── Product.java          # Product data model
│   ├── SearchAlgorithms.java # Sequential & Binary search implementations
│   ├── HybridSearch.java     # Part C – Hybrid search with sorted array + TreeMap index
│   └── Main.java             # Driver program: generates 100,000 products & benchmarks
├── test/
│   └── TechMartTest.java     # JUnit 5 test suite (25 test cases)
├── lib/
│   └── junit-platform-console-standalone-1.10.0.jar  # (download separately – see below)
└── README.md
```

---

## Prerequisites

| Tool | Minimum Version |
|------|----------------|
| Java JDK | 11 or higher |
| JUnit 5 | 5.x (console standalone JAR) |

---

## Compilation

### Step 1 – Compile main source files
```bash
cd CSC483
javac -d out/production src/Product.java src/SearchAlgorithms.java src/HybridSearch.java src/Main.java
```

### Step 2 – Download JUnit 5 standalone JAR (if not present)
```bash
# Download from Maven Central:
# https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.10.0/junit-platform-console-standalone-1.10.0.jar
# Place it in lib/
```

### Step 3 – Compile tests
```bash
javac -d out/test -cp out/production:lib/junit-platform-console-standalone-1.10.0.jar \
      test/TechMartTest.java
```
> **Windows:** Replace `:` with `;` in classpath separators.

---

## Running the Main Program

```bash
java -cp out/production Main
```

### Expected Output Format
```
======================================================================
TECHMART SEARCH PERFORMANCE ANALYSIS (n = 100,000 products)
======================================================================

SEQUENTIAL SEARCH:
  Best Case  (ID found at position 0)  : 0.023 ms
  Average Case (random ID)             : 45.678 ms
  Worst Case (ID not found)            : 89.345 ms

BINARY SEARCH:
  Best Case  (ID at middle)            : 0.001 ms
  Average Case (random ID)             : 0.089 ms
  Worst Case (ID not found)            : 0.092 ms

PERFORMANCE IMPROVEMENT: Binary search is ~513x faster on average

HYBRID NAME SEARCH (TreeMap index):
  Average search time (by name)        : 0.002 ms
  Average insert time (addProduct)     : 0.567 ms
```

---

## Running JUnit Tests

```bash
java -jar lib/junit-platform-console-standalone-1.10.0.jar \
     --class-path out/production:out/test \
     --select-class TechMartTest
```

### Expected: 25 tests, 0 failures

---

## Algorithm Complexity Summary

| Method | Best | Average | Worst |
|--------|------|---------|-------|
| `sequentialSearchById` | O(1) | O(n) | O(n) |
| `binarySearchById` | O(1) | O(log n) | O(log n) |
| `searchByName` (sequential) | O(1) | O(n) | O(n) |
| `HybridSearch.searchById` | O(1) | O(log n) | O(log n) |
| `HybridSearch.searchByName` | O(log n) | O(log n) | O(log n) |
| `HybridSearch.addProduct` | O(log n)* | O(n) | O(n) |

> *addProduct best case is O(log n) for position lookup but O(n) for the shift, so overall O(n).

---

## Notes
- All source files are in the `src/` directory, test files in `test/`.
- The main program uses a fixed random seed (`42L`) so results are reproducible.
- JUnit 5.x is required; JUnit 4 is not compatible with this test file.
