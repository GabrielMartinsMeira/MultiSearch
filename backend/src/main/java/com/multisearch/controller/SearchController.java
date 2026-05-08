package com.multisearch.controller;

import com.multisearch.dto.SearchResultDTO;
import com.multisearch.service.DataService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class SearchController {

    private final DataService dataService;

    public SearchController(DataService dataService) {
        this.dataService = dataService;
    }

    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SearchResultDTO> search(
            @RequestParam(required = false, defaultValue = "") String query) {
        return dataService.search(query);
    }

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SearchResultDTO> getAll() {
        return dataService.getAllData();
    }
}
