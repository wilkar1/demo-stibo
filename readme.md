# Demo Project

This is a demo project, which shows some of the data model interactions we do in the PDX project.

The project objective is to generate a tabular report in CSV format from our data standard specification.

Please clone the project, implement your solution, and send us a zip-file with your answer.

### IDE 

The assignment is also given to applicants without Java knowledge, so here is a small guide on how to get started. 
Download and install [OpenJDK 11](https://adoptium.net/releases.html?variant=openjdk11) and 
[IntelliJ Community Edition](https://www.jetbrains.com/idea/download/), or another IDE of your preference. 
In IntelliJ, simply open the build file, [build.gradle](build.gradle), as a project. You might have to setup the 
[SDK](https://www.jetbrains.com/help/idea/sdk.html#change-project-sdk). 

The project can also be built, tested, and
[run](https://spring.io/guides/gs/spring-boot/#_run_the_application) using gradle in the shell. Run the gradle
[tasks](https://docs.gradle.org/current/userguide/command_line_interface.html#sec:listing_tasks) command to see
the list of available tasks.

## Data Standard

We have a data standard, which describes the context for our products.

### Categories
The data standard has a list of *categories*, which disjointly categorize the products.
The categories are linked together in a hierarchy, where a category points to it's parent.
E.g., for a *Clothing* category, we could have sub-categories *T-Shirts* and *Jeans* so the resulting category 
hierarchies would be *Clothing/T-Shirts* and *Clothing/Jeans*. There is always a single root, where the parent is not 
set.

### Attributes
The data standard also has a set of *attributes*, which describe the data on the products.
The attribute has a type, which specifies the type of the value. E.g., an attribute could be a *Description* of type
*string* and a *Price* of type *decimal*. The attributes are linked into the categories using *attribute links*.

Attributes available for a given category are determined by including all attributes linked to any category in the 
corresponding category hierarchy, all the way to the root category. E.g., attributes defined in the *Clothing* category 
are also available for products in the *T-Shirts* and *Jeans* categories.

Some attributes also link in other attributes, which means that this attribute is a composite definition. An attribute
in a composite definition can also be a composite.

### Attribute Groups
The data standard also defines a set of *attribute groups*, which is an ability to scope the attributes by their 
purpose. One attribute can be linked in to multiple attribute groups. E.g., you could have a group with all assets,
and other groups also including some assets. It is a tool which helps filtering the attributes.

Some data standards have more than 10,000 categories and attributes. The number of available attributes in a category 
can exceed 500.

There is a trivial test data standard in the [datastandard.json](src/test/resources/datastandard.json) test resource.
The object model for the data standard is defined in the [model](src/main/java/com/stibo/demo/report/model) package.

## Deliveries

The assignment is to implement the *report* method in the 
[Reportservice](src/main/java/com/stibo/demo/report/service/ReportService.java) service. 
The method takes a data standard object and a category id as argument, and should return a stream of stream of strings. 
The outer stream is the tabular rows, and the inner streams are the cells in a row. The controller
[endpoint](src/main/java/com/stibo/demo/report/controller/ReportController.java) makes a naïve conversion of this to 
csv.

There is a [test](src/test/java/com/stibo/demo/report/service/ReportServiceTest.java) setup, which can be used to 
exercise the service call.

The deliveries are split into 3 levels of increasing complexity. **You do not have to support all levels at the same time, nor do you have to implement all levels.** Only implement the level of complexity you feel comfortable with in the given timeframe. There are no penalties for not implementing higher levels of complexity.

Fields that differ from the previous level are marked with **bold** in the level description.

The tables below act as a graphical representation of what the output may look like when viewed as a table. The expectation is **not** to produce a graphical console-viewable table.
### Level 1

The report should contain all attributes visible in the defined category.
Produce a report like this on the test data standard:

    +---------------+-----------------+-----------------------------+-----------+
    | Category Name | Attribute Name  | Description                 | Type      |
    +---------------+-----------------+-----------------------------+-----------+
    | Root          | String Value    |                             | string    |
    +---------------+-----------------+-----------------------------+-----------+
    | Leaf          | Composite Value | Composite Value Description | composite |
    +---------------+-----------------+-----------------------------+-----------+

The fields are:

- **Category Name:** the name of the category, the attribute is linked in to.
- **Attribute Name:** the name of the attribute.
- **Description:** the description of the attribute.
- **Type:** the type id of the attribute.

### Level 2

Produce a report like this on the test data standard:

    +---------------+-----------------+-----------------------------+-------------+---------+
    | Category Name | Attribute Name  | Description                 | Type        | Groups  |
    +---------------+-----------------+-----------------------------+-------------+---------+
    | Root          | String Value*   |                             | string      | All     |
    +---------------+-----------------+-----------------------------+-------------+---------+
    | Leaf          | Composite Value | Composite Value Description | composite[] | All     |
    |               |                 |                             |             | Complex |
    +---------------+-----------------+-----------------------------+-------------+---------+

The fields are:

- **Category Name:** the name of the category, the attribute is linked in to.
- **Attribute Name:** the name of the attribute, **with an asterisk (*) appended, if the link to the attribute 
has the *optional* field set to false**.
- **Description:** the description of the attribute.
- **Type:** the type id of the attribute, **with brackets ([]) appended, if the *multiValue* field is set to true**.
- **Groups:** **the names of the attribute groups listed in the *groudIds* field, separated by new lines**.

### Level 3

Produce a report like this on the test data standard:

    +---------------+-----------------+-----------------------------+--------------------------+---------+
    | Category Name | Attribute Name  | Description                 | Type                     | Groups  |
    +---------------+-----------------+-----------------------------+--------------------------+---------+
    | Root          | String Value*   |                             | string                   | All     |
    +---------------+-----------------+-----------------------------+--------------------------+---------+
    | Leaf          | Composite Value | Composite Value Description | composite{               | All     |
    |               |                 |                             |   Nested Value*: integer | Complex |
    |               |                 |                             | }[]                      |         |
    +---------------+-----------------+-----------------------------+--------------------------+---------+

The fields are:

- **Category Name:** the name of the category, the attribute is linked in to.
- **Attribute Name:** the name of the attribute, with an asterisk (*) appended, if the link to the attribute has the 
*optional* field set to false.
- **Description:** the description of the attribute.
- **Type:** the type id of the attribute, with brackets ([]) appended, if the the *multiValue* field is set to true. 
**If the *attributeLinks* field is set, then the links are _recursively expanded in braces ({}) as: two spaces, 
attribute name with optional marker, colon (:), and expanded type, separated by new lines.**
- **Groups:** the name of the attribute groups listed in the *groudIds* field, separated by new lines.

# Real Data

If the application is started, it can be tested on real data as

    curl -s -u key:secret http://localhost:8080/report/acme/T_SHIRTS

The expected content of this call is exported to the [t-shirts.csv](doc/t-shirts.csv) file, and imported into the 
corresponding [T-Shirts.xslx](doc/T-Shirts.xslx) file.