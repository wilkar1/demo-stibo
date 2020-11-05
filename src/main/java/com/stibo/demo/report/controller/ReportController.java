package com.stibo.demo.report.controller;

import com.stibo.demo.report.model.Datastandard;
import com.stibo.demo.report.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.RequestEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.joining;
import static org.apache.logging.log4j.util.Strings.dquote;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
public class ReportController {
    private final ReportService reportService;
    private final RestTemplate restTemplate;

    @Autowired
    public ReportController(ReportService reportService, RestTemplateBuilder restTemplateBuilder) {
        this.reportService = reportService;
        this.restTemplate = restTemplateBuilder.build();
    }

    @RequestMapping(value = "/report/{datastandardId}/{categoryId}", produces = "text/csv")
    public String report(@PathVariable String datastandardId, @PathVariable String categoryId, @RequestHeader(required=false) String authorization)  {
        var uri = URI.create("https://pds-dev.stibosystems.com/api/datastandards/v1/datastandards/" + datastandardId);
        var request = RequestEntity.get(uri).header(AUTHORIZATION, authorization).build();
        var datastandard = restTemplate.exchange(request, Datastandard.class).getBody();
        return reportService.report(datastandard, categoryId)
            .map(row -> row.map(c -> escape(c)).collect(joining(","))).collect(joining("\n"));
    }

    private static String escape(String cell) {
        if (isNull(cell)) {
            return "";
        } else if (cell.contains("\"")) {
            return dquote(cell.replace("\"", "\"\""));
        } else if (cell.contains(",") || cell.contains("\n")) {
            return dquote(cell);
        } else {
            return cell;
        }
    }
}
