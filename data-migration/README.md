# How to run data migration  

````
mvn initialize flyway:migrate 
````

## Why "initialize" ?
````
mvn flyway:migrate  #won't work
````

It's because during "initialize" lifecycle a plugin will be run to load database properties from they system's configuration file, to populate the variables in the pom.xml file, whose values are needed by flyway plugin.