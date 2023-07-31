package com.stibo.demo.report.model;

import java.util.List;

public record Attribute(
    String id,
    String name,
    String description,
    AttributeType type,
    List<AttributeLink> attributeLinks,
    List<String> groupIds
) {}

