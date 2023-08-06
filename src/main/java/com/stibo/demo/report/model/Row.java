package com.stibo.demo.report.model;

//Could use lombok builder, but for only this one application I didn't want to import whole library
public record Row(
        String categoryName,
        String attributeName,
        String description,
        String type,
        String groups
) {

}
