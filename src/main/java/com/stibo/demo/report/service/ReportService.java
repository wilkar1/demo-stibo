package com.stibo.demo.report.service;

import com.stibo.demo.report.model.*;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Service
public class ReportService {
    public Stream<Stream<String>> report(Datastandard datastandard, String categoryId) {
        // TODO: implement
        return Stream.of();
    }
}
