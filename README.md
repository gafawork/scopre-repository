# Scopre Repository for Gitlab <br />Java Library for search in the GitLab repository

Java library for searching Gitlab Server repositories.


---

## GitLab Server Version Support

gitlab4j-api which supports versions 11.0+ of Gitlab Community Edition and Gitlab Enterprise Edition

---

### Generate Jar
```
mvn clean install
```

---
### Using Scopre Repository

| parameter                   | functionality                                                | sample                                      |
|-----------------------------|--------------------------------------------------------------|---------------------------------------------|
| -d, --debug                 | debug                                                        | -d                                          |
| -t, --token  <arg>          | token [REQUIRED]                                             | -t glpat-x3Udy1PSS5_AgRwjUDu                |
| -f, --filter <args>         | filter files                                                 | -f "pom.xml" "config.properties"            |
| -s, --search <args>         | one text or list of many text as input for search [REQUIRED] | -s "context" "Spring-boot"                  |
| -b, --searchBranches <args> | branch(es)                                                   | -b "master" "develop"                       |
| -h, hostUrl <arg>           | host gitlab  [REQUIRED]                                      | -h "https://gitlab.com"                     |
| -n, --projectName <args>    | project name                                                 | -n "gitlab-search" "gafawork"               |
| -p, --parallel <arg>        | total threads                                                | -p 4                                        |
| -c, --classPlugin <arg>     | plugin for execute                                           | -c plugin.gafawork.scopre.repository.SystemOutPlugin |
| -i, --inputFile <arg>       | input file filter projects                                   | -i inputFilterFile.csv                      |

<br> 

### **Java 21 Requirement**

### **Project Set Up**

To run Gitlab-Search, you must run it via the command line using the parameters below:

```
java -jar ./target/scopre-repository-1.0-jar-with-dependencies.jar
-d
-t glpat-x2UHy2PSS7_AGRwKqUDu
-p 4
-f "pom.xml"
-s "jasper"
-b "master"
-h "https://gitlab.com"
```

<br> 

```
java -jar ./target/scopre-repository-1.0-jar-with-dependencies.jar
-d
-t glpat-x2UHy2PSS7_AGRwKqUDu
-p 4
-f "^.*.java$"
-s "String" "Integer"
-b "master"
-h "https://gitlab.com"
```

<br> 

**Output:**

![img.png](img.png)

**Note: **
- report-yyyy-MM-dd hh-mm-ss.csv file generated with the search result.

![img_1.png](img_1.png)

- report-yyyy-MM-dd hh-mm-ss.txt file generated with the search result.

![img_2.png](img_2.png)

