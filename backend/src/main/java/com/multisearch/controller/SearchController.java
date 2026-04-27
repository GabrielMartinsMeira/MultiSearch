package com.multisearch.controller;

import com.multisearch.dto.SearchResultDTO;
import com.multisearch.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class SearchController {

    @Autowired
    private DataService dataService;

    @GetMapping("/search")
    public List<SearchResultDTO> search(@RequestParam String query) {
        return dataService.search(query);
    }

    @GetMapping("/all")
    public List<SearchResultDTO> getAll() {
        return dataService.getAllData();
    }
}
