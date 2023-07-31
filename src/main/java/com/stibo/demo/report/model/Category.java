package com.stibo.demo.report.model;

import java.util.List;

public record Category(
    String id,
    String name,
    String description,
    String parentId,
    List<AttributeLink> attributeLinks
) {}

