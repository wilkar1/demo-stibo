package com.stibo.demo.report.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stibo.demo.report.model.Datastandard;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.stibo.demo.report.utils.Constants.*;
import static java.util.stream.Collectors.toList;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ReportService.class, ObjectMapper.class})
public class ReportServiceTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ReportService reportService;

    private Datastandard datastandard;

    @BeforeEach
    public void before() throws IOException {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("datastandard.json");
        this.datastandard = objectMapper.readValue(stream, Datastandard.class);
    }

    @Test
    public void testReport() {
        //when
        List<List<String>> report = reportService.report(datastandard, "leaf").map(row -> row.collect(toList())).collect(toList());

        //then
        Assertions.assertEquals(report, getTestList());
    }

    private List<List<String>> getTestList() {
        List<List<String>> testList = new ArrayList<>();
        testList.add(Lists.newArrayList(
                TABLE_CATEGORY_NAME,
                TABLE_ATTRIBUTE_NAME,
                TABLE_DESCRIPTION,
                TABLE_TYPE,
                TABLE_GROUPS
        ));
        testList.add(Lists.newArrayList(
                "Root",
                "String Value*",
                null,
                "string",
                "All"
        ));
        testList.add(Lists.newArrayList(
                "Leaf",
                "Composite Value",
                "Composite Value Description",
                "composite{\nNested Value*: integer\n}[]",
                "All\nComplex"
        ));
        return testList;
    }
}
