package com.multisearch.service;

import com.multisearch.dto.SearchResultDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class DataService {

    private final ObjectMapper mapper = new ObjectMapper();

    private String getCategoryName(String filename) {
        switch (filename) {
            case "sales_orders.json": return "Pedidos de Venda";
            case "purchase_orders.json": return "Pedidos de Compra";
            case "equipments.json": return "Equipamentos";
            case "materials.json": return "Materiais";
            case "workforce.json": return "Mão de Obra";
            default: return filename.replace(".json", "");
        }
    }

    private File resolveDataDir() {
        String[] paths = {"data", "src/backend/data", "../data", "../../data"};
        for (String path : paths) {
            File dir = new File(path);
            if (dir.exists() && dir.isDirectory()) return dir;
        }
        return new File("data");
    }

    public List<SearchResultDTO> getAllData() {
        return search("");
    }

    public List<SearchResultDTO> search(String query) {
        List<SearchResultDTO> results = new ArrayList<>();
        File dataDir = resolveDataDir();
        File[] files = dataDir.listFiles((d, name) -> name.endsWith(".json"));

        String lowerQuery = (query == null) ? "" : query.trim().toLowerCase();

        if (files != null) {
            for (File file : files) {
                String catName = getCategoryName(file.getName());
                try (InputStreamReader reader = new InputStreamReader(new FileInputStream(file), Charset.forName("ISO-8859-1"))) {
                    List<Map<String, Object>> items = mapper.readValue(reader, new TypeReference<List<Map<String, Object>>>() {});
                    
                    List<Map<String, Object>> matchedItems;
                    if (lowerQuery.isEmpty()) {
                        matchedItems = items;
                    } else {
                        matchedItems = items.stream()
                            .filter(item -> item.values().stream()
                                .anyMatch(val -> val != null && String.valueOf(val).toLowerCase().contains(lowerQuery)))
                            .collect(Collectors.toList());
                    }
                    results.add(new SearchResultDTO(catName, matchedItems));
                } catch (Exception e) { e.printStackTrace(); }
            }
        }
        return results;
    }
}
