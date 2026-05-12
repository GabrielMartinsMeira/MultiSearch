package com.multisearch.controller;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.multisearch.dto.SalesOrderDTO;
import com.multisearch.dto.SearchResultDTO;
import com.multisearch.dto.WorkforceDTO;
import com.multisearch.service.DataService;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SearchController.class)
@DisplayName("SearchController - HTTP Controller Unit Tests")
@SuppressWarnings("null") // Hamcrest matchers lack @NonNull — false positives from Eclipse null-analysis
class SearchControllerTest {
        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private DataService dataService;

        // --- Mock data with realistic content ---

        private SalesOrderDTO salesOrder;
        private WorkforceDTO workforceEntry;

        /** Default result for getAllData() — 5 categories with real items */
        private List<SearchResultDTO> allDataResults;

        @BeforeEach
        void setUp() {
                // SalesOrder with real data (items have actual content, not empty List.of())
                salesOrder = new SalesOrderDTO();
                salesOrder.setSalesOrderId(90001);
                salesOrder.setCustomer("Construtora Silva");
                salesOrder.setMaterialId("MAT-001");
                salesOrder.setMaterialName("Furadeira Industrial");
                salesOrder.setQuantity(10);
                salesOrder.setTotalValue(5000.0);
                salesOrder.setDeliveryDate("2024-06-01");

                // WorkforceDTO with accented characters (encoding verification)
                workforceEntry = new WorkforceDTO();
                workforceEntry.setWorkforceId(1);
                workforceEntry.setName("Carlos Mão de Obra");
                workforceEntry.setShift("Manhã");

                // Default result for getAllData() — all 5 categories with items
                allDataResults = List.of(
                                new SearchResultDTO("Pedidos de Venda", List.of(salesOrder)),
                                new SearchResultDTO("Pedidos de Compra", List.of()),
                                new SearchResultDTO("Equipamentos", List.of()),
                                new SearchResultDTO("Materiais", List.of()),
                                new SearchResultDTO("Mão de Obra", List.of(workforceEntry)));

                when(dataService.getAllData()).thenReturn(allDataResults);

                // anyString() as fallback — specific stubs override by query
                when(dataService.search(anyString())).thenReturn(List.of());
        }

        // =========================================================================
        // GET /api/all
        // =========================================================================

        // In @WebMvcTest context without full GlobalExceptionHandler, Spring Boot 3.2
        // may return 4xx or 5xx for unsupported methods. is4xxClientError() is
        // sufficient to guarantee POST is rejected per the endpoint contract.

        @Test
        @DisplayName("GET /api/all → HTTP 200, Content-Type JSON, 5 categories with items")
        void allEndpoint_returns200WithJsonAndItems() throws Exception {
                mockMvc.perform(get("/api/all"))
                                .andExpect(status().isOk())
                                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$", hasSize(5)))
                                .andExpect(jsonPath("$[0].category").value("Pedidos de Venda"))
                                .andExpect(jsonPath("$[4].category").value("Mão de Obra"))
                                // Verifies items have real content, not just that they exist
                                .andExpect(jsonPath("$[4].items[0].Name").value("Carlos Mão de Obra"))
                                .andExpect(jsonPath("$[0].items", hasSize(1)))
                                .andExpect(jsonPath("$[0].items[0].SalesOrderID").value(90001))
                                .andExpect(jsonPath("$[0].items[0].Customer").value("Construtora Silva"));
        }

        @Test
        @DisplayName("GET /api/all → response contains all 5 categories with correct names")
        void allEndpoint_returnsAllCategoryNames() throws Exception {
                mockMvc.perform(get("/api/all"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[*].category", containsInAnyOrder(
                                                "Pedidos de Venda",
                                                "Pedidos de Compra",
                                                "Equipamentos",
                                                "Materiais",
                                                "Mão de Obra")));
        }

        @Test
        @DisplayName("GET /api/all → calls dataService.getAllData() exactly once, never search()")
        void allEndpoint_callsGetAllDataOnce() throws Exception {
                mockMvc.perform(get("/api/all"))
                                .andExpect(status().isOk());

                verify(dataService, times(1)).getAllData();
                verify(dataService, never()).search(anyString());
        }

        @Test
        @DisplayName("GET /api/all → HTTP 500 when service throws RuntimeException")
        void allEndpoint_whenServiceThrows_returns500() throws Exception {
                when(dataService.getAllData()).thenThrow(new RuntimeException("Simulated service failure"));

                mockMvc.perform(get("/api/all"))
                                .andExpect(status().isInternalServerError());
        }

        @Test
        @DisplayName("POST /api/all → must reject with client error (only GET is allowed)")
        void allEndpoint_postMethod_isRejected() throws Exception {
                mockMvc.perform(post("/api/all"))
                                .andExpect(status().is4xxClientError());
        }

        // =========================================================================
        // GET /api/search — with specific queries
        // =========================================================================

        @Test
        @DisplayName("GET /api/search?query=Construtora → returns only Sales Orders with the correct item")
        void searchEndpoint_withQuery_returnsSpecificResults() throws Exception {
                // Specific stub for the "Construtora" query
                when(dataService.search("Construtora")).thenReturn(List.of(
                                new SearchResultDTO("Pedidos de Venda", List.of(salesOrder))));

                mockMvc.perform(get("/api/search").param("query", "Construtora"))
                                .andExpect(status().isOk())
                                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                                // Verifies the real content of the returned item
                                .andExpect(jsonPath("$", hasSize(1)))
                                .andExpect(jsonPath("$[0].category").value("Pedidos de Venda"))
                                .andExpect(jsonPath("$[0].items[0].Customer").value("Construtora Silva"))
                                .andExpect(jsonPath("$[0].items[0].SalesOrderID").value(90001));
        }

        @Test
        @DisplayName("GET /api/search?query=Manhã → delegates the correct query to the service (verify)")
        void searchEndpoint_delegatesQueryToService() throws Exception {
                when(dataService.search("Manhã")).thenReturn(List.of(
                                new SearchResultDTO("Mão de Obra", List.of(workforceEntry))));

                mockMvc.perform(get("/api/search").param("query", "Manhã"))
                                .andExpect(status().isOk());

                verify(dataService, times(1)).search("Manhã");
                verify(dataService, never()).getAllData();
        }

        @Test
        @DisplayName("GET /api/search?query= (empty query) → HTTP 200 with no errors")
        void searchEndpoint_withEmptyQuery_returns200() throws Exception {
                when(dataService.search("")).thenReturn(allDataResults);

                mockMvc.perform(get("/api/search").param("query", ""))
                                .andExpect(status().isOk())
                                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        }

        @Test
        @DisplayName("GET /api/search (no query param) → HTTP 200 using empty default value")
        void searchEndpoint_withoutQueryParam_returns200() throws Exception {
                when(dataService.search("")).thenReturn(allDataResults);

                mockMvc.perform(get("/api/search"))
                                .andExpect(status().isOk());

                verify(dataService, times(1)).search("");
        }

        @Test
        @DisplayName("GET /api/search?query=Mão → HTTP 200, verifies UTF-8 and that the service receives the correct accent")
        void searchEndpoint_withAccentedChars_preservesEncoding() throws Exception {
                // Specific stub + verify for accented characters
                when(dataService.search("Mão")).thenReturn(List.of(
                                new SearchResultDTO("Mão de Obra", List.of(workforceEntry))));

                mockMvc.perform(get("/api/search").param("query", "Mão"))
                                .andExpect(status().isOk())
                                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                                // Confirms the accented category is in the response
                                .andExpect(jsonPath("$[0].category").value("Mão de Obra"));

                // Verifies "Mão" arrived intact at the service (correct encoding)
                verify(dataService, times(1)).search("Mão");
        }

        @Test
        @DisplayName("GET /api/search?query=%20%20%20 (whitespace only) → HTTP 200 with no errors")
        void searchEndpoint_withWhitespaceOnlyQuery_returns200() throws Exception {
                // Whitespace-only query
                when(dataService.search("   ")).thenReturn(List.of());

                mockMvc.perform(get("/api/search").param("query", "   "))
                                .andExpect(status().isOk());

                verify(dataService, times(1)).search("   ");
        }

        @Test
        @DisplayName("GET /api/search?query=<script> (XSS special chars) → HTTP 200, treated as literal text")
        void searchEndpoint_withSpecialCharsQuery_returns200() throws Exception {
                // Query with special characters — API must treat as literal text
                String xssQuery = "<script>alert('xss')</script>";
                when(dataService.search(xssQuery)).thenReturn(List.of());

                mockMvc.perform(get("/api/search").param("query", xssQuery))
                                .andExpect(status().isOk())
                                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

                verify(dataService, times(1)).search(xssQuery);
        }

        @Test
        @DisplayName("GET /api/search → HTTP 500 when service throws RuntimeException")
        void searchEndpoint_whenServiceThrows_returns500() throws Exception {
                when(dataService.search(anyString())).thenThrow(new RuntimeException("Simulated service failure"));

                mockMvc.perform(get("/api/search").param("query", "teste"))
                                .andExpect(status().isInternalServerError());
        }

        @Test
        @DisplayName("POST /api/search → must reject with client error (only GET is allowed)")
        void searchEndpoint_postMethod_isRejected() throws Exception {
                mockMvc.perform(post("/api/search").param("query", "teste"))
                                .andExpect(status().is4xxClientError());
        }

        @Test
        @DisplayName("GET /api/search?query={1000 chars} → HTTP 200 ou 400, nunca 500")
        void searchEndpoint_withVeryLongQuery_doesNotCrash() throws Exception {
                String longQuery = "a".repeat(1000);
                when(dataService.search(longQuery)).thenReturn(List.of());

                int status = mockMvc.perform(get("/api/search").param("query", longQuery))
                                .andReturn().getResponse().getStatus();
                // Should return 200 or 400, but never 500 (internal error)
                org.junit.jupiter.api.Assertions.assertNotEquals(500, status,
                                "Long query must not cause internal server error (HTTP 500)");
        }
}