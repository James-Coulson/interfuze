# Interfuze

This repository contains the code for the Interfuze coding challenge (completed by James Coulson).

This has been written in Java (+ Maven) as I do not have much experience programming in .NET.

## Initial Setup and CSV Package

This repository was initialised prior to starting the challenge with the only actions being to initialise the Java project and setup/test the CSV package (as per email instructions).

The [Apache Commons CSV](https://commons.apache.org/proper/commons-csv/) library has been used and the below command was used to generate the Java project.

```
mvn archetype:generate -DgroupId=interfuze -DartifactId=interfuze -DarchetypeArtifactId=maven-archetype-quickstart -DarchetypeVersion=1.4 -DinteractiveMode=false
```

## Running the project

The `run.sh` bash script can be used to run the project without the need to compile the project into a .jar (this may require `chmod +x run.sh` to allow for the script to be run).

## Assumptions

- In the Data CSV files the units of the `Rainfall` field is `mm/hour` (the amount of rainfall, millimeters, which has fallen in the past hour).