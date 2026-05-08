package com.multisearch.service;

import com.multisearch.dto.EquipmentDTO;
import com.multisearch.dto.MaterialDTO;
import com.multisearch.dto.PurchaseOrderDTO;
import com.multisearch.dto.SalesOrderDTO;
import com.multisearch.dto.SearchResultDTO;
import com.multisearch.dto.WorkforceDTO;
import com.multisearch.exception.DataLoadException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DataService {

    private static final Logger log = LoggerFactory.getLogger(DataService.class);
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${multisearch.data.dir:}")
    private String configuredDataDir;

    private static final Map<String, TypeReference<?>> FILE_TYPE_MAP = Map.of(
            "sales_orders.json", new TypeReference<List<SalesOrderDTO>>() {
            },
            "purchase_orders.json", new TypeReference<List<PurchaseOrderDTO>>() {
            },
            "equipments.json", new TypeReference<List<EquipmentDTO>>() {
            },
            "materials.json", new TypeReference<List<MaterialDTO>>() {
            },
            "workforce.json", new TypeReference<List<WorkforceDTO>>() {
            });

    private String getCategoryName(String filename) {
        return switch (filename) {
            case "sales_orders.json" -> "Pedidos de Venda";
            case "purchase_orders.json" -> "Pedidos de Compra";
            case "equipments.json" -> "Equipamentos";
            case "materials.json" -> "Materiais";
            case "workforce.json" -> "Mão de Obra";
            default -> filename.replace(".json", "");
        };
    }

    private File resolveDataDir() {
        if (configuredDataDir != null && !configuredDataDir.isEmpty()) {
            File dir = new File(configuredDataDir);
            if (dir.exists() && dir.isDirectory()) {
                return dir;
            }
            log.warn("Configured data directory '{}' not found, falling back to defaults", configuredDataDir);
        }

        String[] paths = { "data", "src/backend/data", "../data", "../../data" };
        for (String path : paths) {
            File dir = new File(path);
            if (dir.exists() && dir.isDirectory()) {
                return dir;
            }
        }

        throw new DataLoadException(
                "Data directory not found. Configure 'multisearch.data.dir' in application.properties.");
    }

    public List<SearchResultDTO> getAllData() {
        return search("");
    }

    public List<SearchResultDTO> search(String query) {
        List<SearchResultDTO> results = new ArrayList<>();
        File dataDir = resolveDataDir();
        File[] files = dataDir.listFiles((d, name) -> name.endsWith(".json"));

        String lowerQuery = (query == null) ? "" : query.trim().toLowerCase();

        if (files == null || files.length == 0) {
            log.warn("No JSON files found in data directory: {}", dataDir.getAbsolutePath());
            return results;
        }

        for (File file : files) {
            String catName = getCategoryName(file.getName());
            TypeReference<?> typeRef = FILE_TYPE_MAP.getOrDefault(
                    file.getName(),
                    new TypeReference<List<Map<String, Object>>>() {
                    });

            try (FileInputStream inputStream = new FileInputStream(file)) {

                List<?> items = (List<?>) mapper.readValue(inputStream, typeRef);

                List<?> matchedItems;
                if (lowerQuery.isEmpty()) {
                    matchedItems = items;
                } else {
                    matchedItems = items.stream()
                            .filter(item -> itemMatchesQuery(item, lowerQuery))
                            .toList();
                }

                results.add(new SearchResultDTO(catName, matchedItems));
            } catch (Exception e) {
                log.error("Failed to read data file '{}': {}", file.getName(), e.getMessage(), e);
            }
        }
        return results;
    }

    @SuppressWarnings("unchecked")
    private boolean itemMatchesQuery(Object item, String lowerQuery) {
        Map<String, Object> map = mapper.convertValue(item, Map.class);
        return map.values().stream()
                .anyMatch(val -> val != null
                        && String.valueOf(val).toLowerCase().contains(lowerQuery));
    }
}
