# Interfuze

This repository contains the code for the Interfuze coding challenge (completed by James Coulson). This has been written in Java (+ Maven) as I do not have much experience programming in .NET.

**NOTE:** As part of the assignment it is required that the customer be supplied a README detailing any assumptions. This can be found at the bottom of this document.

## Initial Setup and CSV Package

This repository was initialised prior to starting the challenge with the only actions being to initialise the Java project and setup/test the CSV package (as per email instructions).

The [Apache Commons CSV](https://commons.apache.org/proper/commons-csv/) library has been used and the below command was used to generate the Java project.

```
mvn archetype:generate -DgroupId=interfuze -DartifactId=interfuze -DarchetypeArtifactId=maven-archetype-quickstart -DarchetypeVersion=1.4 -DinteractiveMode=false
```

## Running the project

The `run.sh` bash script can be used to run the project without the need to compile the project into a .jar (this may require `chmod +x run.sh` to allow for the script to be run). However, if this were to be given to an actual client the application would be bundled into a .jar file and be run directly.

The tool also incorporates a number of options, including a help menu, to configure the execution of the app. The help menu can be broughh up by running the below command.

	./run.sh -h

# Customer README

Welcome to the *Fuzion, inc CLI* tool READNE file. In this document you will find details of how to get started using this command line tool, as well as some general assumptions around its usage.

## Getting Started

Before you begin using the tool you are able to see the options which can be used to configure how the tool runs. This can be seen using the below command.

	./run.sh -h

This will print the help menu in your terminal which can be used as a reference for future use.

Now, to get started using the tool ensure that the data file that are to be processed are stored in a folder titled `data` within the directory the app will be running. This includes the Devices CSV which should be named `Devices.csv`. Then by running the following command, the tool will process these data files and output the summarised data in a tabular format.

	./run.sh

## Assumptions

- By default, the tool assumes that the data that need to be processed is stored in a local `./data` directory (containing the Devices CSV file). This can be overwritten using the `-o` or `--observations` option.
- It is assumed that the Devices CSV file is contained within the `./data` directory and is named `Devices.csv`. However, while the file can be overwritten using the `-d` or `--devices` CLI options, it will always be assumed to be contained within the `./data` folder.
- All data and device files will be in the format of a CSV file, and have the `.csv` file extension. If this is not the case the tool will skip processing them.
- The Devices CSV file has the following headers: `Device ID`, `Device Name`, and `Location`.
- The Data/Observations CSV files have the following headers: `Device ID`, `Time`, `Rainfall`. Additionally that the `Time` values are in form `d/MM/yyyy h:mm`.