package com.research.assistant;

import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/citation")
@CrossOrigin("*")
public class CitationController {

    private final CitationService citationService;

    public CitationController(CitationService citationService) {
        this.citationService = citationService;
    }

    @PostMapping
    public String generateCitation(
            @RequestParam String url,
            @RequestParam String format
    ) throws IOException {

        Map<String, String> meta = citationService.extractMeta(url);

        String citation;

        switch (format.trim().toUpperCase()) {
            case "APA":
                citation = citationService.generateAPA(meta);
                break;
            case "MLA":
                citation = citationService.generateMLA(meta);
                break;
            case "IEEE":
                citation = citationService.generateIEEE(meta);
                break;
            default:
                citation = "Invalid citation format";
        }

        return citation;
    }
}
