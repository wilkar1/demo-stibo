package com.stibo.demo.report.service;

import com.stibo.demo.report.logging.LogTime;
import com.stibo.demo.report.model.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Stream;

import static com.stibo.demo.report.utils.Constants.*;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.joining;

@Service
public class ReportService {

    @LogTime
    public Stream<Stream<String>> report(Datastandard datastandard, String categoryId) {
        List<Category> allNestedCategories = getAllNestedCategories(datastandard, categoryId);
        Collections.reverse(allNestedCategories);
        List<Row> rows = new ArrayList<>();
        prepareHeaders(rows);
        allNestedCategories.forEach(category -> {
            List<Attribute> attributes = getAttributesFromCategory(datastandard, category);
            createRowsFromAttributes(datastandard, rows, category, attributes);
        });
        return rows.stream()
                .map(row -> Stream.of(row.categoryName(), row.attributeName(), row.description(), row.type(), row.groups()))
                .toList()
                .stream();
    }

    private void prepareHeaders(List<Row> rows) {
        rows.add(new Row(
                        TABLE_CATEGORY_NAME,
                        TABLE_ATTRIBUTE_NAME,
                        TABLE_DESCRIPTION,
                        TABLE_TYPE,
                        TABLE_GROUPS
                )
        );
    }

    private void createRowsFromAttributes(Datastandard datastandard, List<Row> rows, Category category, List<Attribute> attributes) {
        attributes.forEach(attribute -> {
                    String attributeName = prepareAttributeName(category, attribute);
                    String attributeTypeId = prepareAttributeTypeId(datastandard, attribute);
                    String groupsNames = prepareGroupsString(datastandard, attribute);
                    rows.add(new Row(category.name(),
                                    attributeName,
                                    attribute.description(),
                                    attributeTypeId,
                                    groupsNames
                            )
                    );
                }
        );
    }

    private static String prepareGroupsString(Datastandard datastandard, Attribute attribute) {
        List<String> groupIds = attribute.groupIds();
        List<AttributeGroup> attributeGroups = datastandard.attributeGroups();
        if (groupIds != null && attributeGroups != null){
            return attributeGroups.stream()
                    .filter(attributeGroup -> groupIds.contains(attributeGroup.id()))
                    .map(AttributeGroup::name)
                    .collect(joining("\n"));
        }
        return "";
    }

    private static String prepareAttributeTypeId(Datastandard datastandard, Attribute attribute) {
        List<AttributeLink> attributeLinks = attribute.attributeLinks();
        if (attributeLinks != null && !attributeLinks.isEmpty()) {
            List<String> attributesOptional = attributeLinks.stream()
                    .filter(AttributeLink::optional)
                    .flatMap(attributeLink -> getAttributeStream(datastandard, attributeLink))
                    .map(nestedAttribute -> String.format("%s: %s", nestedAttribute.name(), nestedAttribute.type()))
                    .toList();
            List<String> attributesNonOptional = attributeLinks.stream()
                    .filter(not(AttributeLink::optional))
                    .flatMap(attributeLink -> getAttributeStream(datastandard, attributeLink))
                    .map(nestedAttribute -> String.format("%s*: %s", nestedAttribute.name(), nestedAttribute.type().id()))
                    .toList();
            List<String> combinedList = Stream.of(attributesOptional, attributesNonOptional)
                    .flatMap(Collection::stream)
                    .toList();
            String joinedString = String.join("\n", combinedList);
            return String.format("%s{\n%s\n}[]", attribute.type().id(), joinedString);
        }
        return attribute.type().multiValue() ? attribute.type().id().concat("[]") : attribute.type().id();
    }

    private static String prepareAttributeName(Category category, Attribute attribute) {
        AttributeLink link = category.attributeLinks().stream()
                .filter(attributeLink -> attributeLink.id().equals(attribute.id()))
                .findFirst()
                .orElse(null);
        String attributeName = attribute.name();
        if (link != null) {
            attributeName = checkOptional(link, attributeName);
        }
        return attributeName;
    }

    private static String checkOptional(AttributeLink link, String attributeName) {
        if (link.optional() == null || link.optional()) return attributeName;
        return attributeName.concat("*");
    }

    private static List<Attribute> getAttributesFromCategory(Datastandard datastandard, Category category) {
        return category.attributeLinks()
                .stream()
                .flatMap(attributeLink -> getAttributeStream(datastandard, attributeLink))
                .toList();
    }

    private static Stream<Attribute> getAttributeStream(Datastandard datastandard, AttributeLink attributeLink) {
        return datastandard.attributes().stream()
                .filter(attribute -> Objects.equals(attributeLink.id(), attribute.id()));
    }

    private List<Category> getAllNestedCategories(Datastandard datastandard, String categoryId) {
        List<Category> processedCategories = new ArrayList<>();
        Category category = getParentById(datastandard, categoryId);
        processedCategories.add(category);
        while (category.parentId() != null) {
            category = getParentById(datastandard, category.parentId());
            if (category == null) break;
            processedCategories.add(category);
        }
        return processedCategories;
    }

    private Category getParentById(Datastandard datastandard, String categoryId) {
        return datastandard.categories().stream()
                .filter(cat -> cat.id().equals(categoryId))
                .findFirst()
                .orElse(null);
    }
}
