package com.apiprotector.cli;

import com.apiprotector.core.CompositeDiff;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


public class Main {
    private static final String USAGE = "<OLD_FILE> <NEW_FILE> [OPTIONS]";
    public static void main(String[] args) {

        Options options = setOptions();

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        try {
            CommandLine cmd = parser.parse(options, args);

            if (cmd.hasOption("h")) {
                formatter.printHelp(USAGE, options);
                System.exit(0);
            }

            String[] remainingArgs = cmd.getArgs();
            if (remainingArgs.length < 2) {
                System.err.println("Error: missing required arguments <OLD_FILE> <NEW_FILE>");
                formatter.printHelp(USAGE, options);
                System.exit(1);
            }
            else if (remainingArgs.length > 2) {
                System.err.println("Error: too many arguments");
                formatter.printHelp(USAGE, options);
                System.exit(2);
            }

            String previousFile = remainingArgs[0];
            String currentFile = remainingArgs[1];
            String previousFileContent;
            String currentFileContent;
            String result = null;

            ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

            try {
                previousFileContent = getFileContent(previousFile, yamlMapper);
                currentFileContent = getFileContent(currentFile, yamlMapper);
                result = new CompositeDiff(previousFileContent, currentFileContent).toString();
            }
            catch (IOException e) {
                System.err.println("Error while reading file: " + e.getMessage());
                System.exit(4);
            }

            ObjectMapper jsonMapper = new ObjectMapper();
            JsonNode jsonNode = jsonMapper.readTree(result);
            String changes = jsonNode.get("changes").toPrettyString();
            boolean isCompatible = jsonNode.get("changes").get("compatible").asBoolean();
            String compatibilityInfo = isCompatible ? "The API changes are compatible." : "The API changes are not compatible.";

            if (cmd.hasOption("o")) {
                String outputFile = cmd.getOptionValue("o");
                try (FileWriter writer = new FileWriter(outputFile)) {
                    if (cmd.hasOption("v")) {
                        writer.write(changes);
                    }
                    else {
                        writer.write(compatibilityInfo);
                    }
                    if (!cmd.hasOption("s")) {
                        System.out.println("Output written to " + outputFile);
                    }
                }
                catch (IOException e) {
                    System.err.println("Error writing to file: " + e.getMessage());
                    System.exit(5);
                }
            }
            else {
                if (cmd.hasOption("v")) {
                    System.out.println(changes);
                }
                else if (!cmd.hasOption("s")) {
                    System.out.println(compatibilityInfo);
                }
            }

            if (!isCompatible && cmd.hasOption("e")) {
                System.exit(1);
            }

        }
        catch (AlreadySelectedException e) {
            System.err.println("Multiple options selected: " + e.getMessage());
            formatter.printHelp(USAGE, options);
            System.exit(6);
        }
        catch (ParseException e) {
            System.err.println("Parsing failed: " + e.getMessage());
            formatter.printHelp(USAGE, options);
            System.exit(7);
        }
        catch (JsonProcessingException e) {
            System.err.println("Error processing JSON: " + e.getMessage());
            System.exit(8);
        }
        catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            System.exit(9);
        }
    }

    private static Options setOptions() {
        Options options = new Options();
        options.addOption("h", "help"
                , false, "Display help information");
        options.addOption("o", "output"
                , true, "Output file");
        options.addOption("e", "error"
                , false, "Exit with error code if the OpenAPI changes are not compatible");

        OptionGroup optionGroup = new OptionGroup();
        Option verboseOption = new Option("v", "verbose"
                , false, "Verbose output");
        Option silentOption = new Option("s", "silent"
                , false, "Print no output to console");

        optionGroup.addOption(verboseOption);
        optionGroup.addOption(silentOption);
        options.addOptionGroup(optionGroup);
        return options;
    }

    private static String getFileContent(String fileName, ObjectMapper yamlMapper) throws IOException {
        String previousFileContent = null;
        if (fileName.endsWith(".yaml") || fileName.endsWith(".yml")) {
            previousFileContent = yamlMapper.readTree(new File(fileName)).toString();
        }
        else if (fileName.endsWith(".json")) {
            previousFileContent = Files.readString(Path.of(fileName));
        }
        else {
            System.err.println("Unsupported file format. Only YAML and JSON are supported.");
            System.exit(3);
        }
        return previousFileContent;
    }
}
