package com.stibo.demo.report.model;

import java.util.List;

public record Datastandard(
    String id,
    String name,
    List<Category> categories,
    List<Attribute> attributes,
    List<AttributeGroup> attributeGroups
) {}

