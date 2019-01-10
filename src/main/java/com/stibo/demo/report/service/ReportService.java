package com.stibo.demo.report.service;

import com.stibo.demo.report.model.*;
import org.springframework.stereotype.Service;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.stream.Stream;

@Service
public class ReportService {
    public Stream<Stream<String>> report(Datastandard datastandard, String categoryId) {
        throw new NotImplementedException();
    }
}
