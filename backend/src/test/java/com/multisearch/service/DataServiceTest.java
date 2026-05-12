package com.multisearch.service;

import java.io.File;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.multisearch.dto.SearchResultDTO;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("integration")
@DisplayName("DataService - Integration Tests with Isolated Test Data")
@SuppressWarnings("null") // Hamcrest matchers lack @NonNull — false positives from Eclipse null-analysis
class DataServiceTest {

    private DataService dataService;

    @BeforeEach
    void setUp() {
        dataService = new DataService();
        String testDataPath = new File("src/test/resources/testdata").getAbsolutePath();
        ReflectionTestUtils.setField(dataService, "configuredDataDir", testDataPath);
    }

    // -------------------------------------------------------------------------
    // Search with special queries (empty, null)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("search('') → returns all 5 categories (equivalent to getAllData)")
    void search_withEmptyQuery_returnsAllCategories() {
        List<SearchResultDTO> results = dataService.search("");
        assertEquals(5, results.size(), "Should return all 5 categories");
    }

    @Test
    @DisplayName("search(null) → behaves like empty query, returns all 5 categories")
    void search_withNullQuery_returnsAllCategories() {
        List<SearchResultDTO> results = dataService.search(null);
        assertEquals(5, results.size(), "Null query should behave like empty query");
    }

    // -------------------------------------------------------------------------
    // Search by match
    // -------------------------------------------------------------------------

    @Test
    // Fixture dependency: expects "Construtora Silva" in testdata/sales_orders.json
    @DisplayName("search('Construtora Silva') → finds results in Sales Orders")
    void search_withExactCustomerMatch_returnsCorrectResults() {
        List<SearchResultDTO> results = dataService.search("Construtora Silva");
        boolean hasMatch = results.stream()
                .anyMatch(r -> r.getCategory().equals("Pedidos de Venda")
                        && r.getItems() != null
                        && !r.getItems().isEmpty());
        assertTrue(hasMatch, "Should find 'Construtora Silva' in sales orders");
    }

    @Test
    // Fixture dependency: expects "Furadeira" in testdata/sales_orders.json and
    // testdata/equipment.json
    @DisplayName("search('Furadeira') → finds matches in exactly 2 categories (Sales Orders and Equipment)")
    void search_withPartialMatch_returnsFilteredResults() {
        List<SearchResultDTO> results = dataService.search("Furadeira");
        long matchingCategories = results.stream()
                .filter(r -> r.getItems() != null && !r.getItems().isEmpty())
                .count();
        assertEquals(2, matchingCategories,
                "'Furadeira' should appear in exactly Pedidos de Venda and Equipamentos");
    }

    @Test
    @DisplayName("search('XYZNONEXISTENT123') → returns all 5 categories, each with empty items")
    void search_withNoMatch_returnsEmptyItems() {
        List<SearchResultDTO> results = dataService.search("XYZNONEXISTENT123");
        assertEquals(5, results.size(), "Should always return all 5 categories even with no matches");
        for (SearchResultDTO result : results) {
            assertTrue(result.getItems().isEmpty(),
                    "Category '" + result.getCategory() + "' should have no matches");
        }
    }

    // -------------------------------------------------------------------------
    // Case-insensitivity
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("search('CONSTRUTORA') and search('construtora') → return the same items per category")
    void search_isCaseInsensitive() {
        List<SearchResultDTO> upperResults = dataService.search("CONSTRUTORA");
        List<SearchResultDTO> lowerResults = dataService.search("construtora");

        // Compare per-category, not just totals — prevents false positives from
        // different category distributions that happen to sum to the same count
        for (SearchResultDTO upper : upperResults) {
            SearchResultDTO lower = lowerResults.stream()
                    .filter(r -> r.getCategory().equals(upper.getCategory()))
                    .findFirst()
                    .orElseThrow(() -> new AssertionError(
                            "Category '" + upper.getCategory() + "' missing from lowercase results"));
            assertEquals(upper.getItems().size(), lower.getItems().size(),
                    "Item count mismatch for category '" + upper.getCategory() + "'");
        }
        long totalItems = upperResults.stream().mapToLong(r -> r.getItems().size()).sum();
        assertTrue(totalItems > 0, "Should find at least one result");
    }

    // -------------------------------------------------------------------------
    // getAllData()
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("getAllData() → returns the same data as search('') with matching item counts per category")
    void getAllData_returnsSameAsEmptySearch() {
        List<SearchResultDTO> allData = dataService.getAllData();
        List<SearchResultDTO> emptySearch = dataService.search("");
        assertEquals(allData.size(), emptySearch.size());

        // Compare by category name, not by index (order-independent)
        for (SearchResultDTO all : allData) {
            SearchResultDTO match = emptySearch.stream()
                    .filter(r -> r.getCategory().equals(all.getCategory()))
                    .findFirst()
                    .orElseThrow(() -> new AssertionError(
                            "Category '" + all.getCategory() + "' missing from search('') results"));
            assertEquals(all.getItems().size(), match.getItems().size(),
                    "Item count mismatch for category '" + all.getCategory() + "'");
        }
    }

    // -------------------------------------------------------------------------
    // UTF-8 encoding and accented characters
    // -------------------------------------------------------------------------

    @Test
    // Fixture dependency: expects "Turno" field in testdata/workforce.json
    @DisplayName("search('Turno') → finds results in 'Mão de Obra' preserving accents")
    void search_withAccentedCharacters_works() {
        List<SearchResultDTO> results = dataService.search("Turno");
        boolean hasWorkforce = results.stream()
                .anyMatch(r -> r.getCategory().equals("Mão de Obra")
                        && r.getItems() != null
                        && !r.getItems().isEmpty());
        assertTrue(hasWorkforce, "Should find 'Turno' in workforce data");
    }

    // -------------------------------------------------------------------------
    // Search by numeric values (IDs)
    // -------------------------------------------------------------------------

    @Test
    // Fixture dependency: expects SalesOrderID 90001 in testdata/sales_orders.json
    // (line 1)
    @DisplayName("search('90001') → finds the numeric ID in Sales Orders")
    void search_withNumericQuery_matchesIds() {
        List<SearchResultDTO> results = dataService.search("90001");
        boolean hasSalesOrder = results.stream()
                .anyMatch(r -> r.getCategory().equals("Pedidos de Venda")
                        && r.getItems() != null
                        && !r.getItems().isEmpty());
        assertTrue(hasSalesOrder, "Should find numeric ID in sales orders");
    }

    // -------------------------------------------------------------------------
    // Resilience: invalid directory
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("search() with invalid directory → does not throw (safe fallback)")
    void search_invalidDataDir_doesNotThrow() {
        DataService badService = new DataService();
        ReflectionTestUtils.setField(badService, "configuredDataDir", "/nonexistent/path/xyz123");
        assertDoesNotThrow(() -> badService.search("test"));
    }

    @Test
    @DisplayName("getAllData() with invalid directory → does not throw (safe fallback)")
    void getAllData_invalidDataDir_doesNotThrow() {
        DataService badService = new DataService();
        ReflectionTestUtils.setField(badService, "configuredDataDir", "/nonexistent/path/xyz123");
        assertDoesNotThrow(() -> badService.getAllData());
    }

    // -------------------------------------------------------------------------
    // Category names
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("getAllData() → contains exactly the 5 categories with correct translated names")
    void search_categoriesHaveCorrectNames() {
        List<SearchResultDTO> results = dataService.search("");
        List<String> categories = results.stream()
                .map(SearchResultDTO::getCategory)
                .toList();

        // Strict assertion: exactly these 5 categories, no more, no less
        assertThat(categories, containsInAnyOrder(
                "Pedidos de Venda",
                "Pedidos de Compra",
                "Equipamentos",
                "Materiais",
                "Mão de Obra"));
    }
}