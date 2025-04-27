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
    private static final String USAGE = "API Protector <OLD_FILE> <NEW_FILE> [-o outputfile]";
    public static void main(String[] args) {

        Options options = new Options();
        Option output = Option.builder("o")
                .longOpt("output")
                .hasArg()
                .desc("Output file")
                .build();

        options.addOption(output);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        try {
            CommandLine cmd = parser.parse(options, args);

            String[] remainingArgs = cmd.getArgs();
            if (remainingArgs.length < 2) {
                System.out.println("Error: Missing required arguments <OLD_FILE> <NEW_FILE>");
                formatter.printHelp(USAGE, options);
                System.exit(2);
            }

            String previousFile = remainingArgs[0];
            String currentFile = remainingArgs[1];
            String previousFileContent = null;
            String currentFileContent = null;
            String result = null;

            ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

            try {
                if (previousFile.endsWith("yaml")) {
                    previousFileContent = yamlMapper.readTree(new File(previousFile)).toString();
                }
                else if (previousFile.endsWith("json")) {
                    previousFileContent = Files.readString(Path.of(previousFile));
                }
                else {
                    System.err.println("Unsupported file format. Only YAML and JSON are supported.");
                    System.exit(3);
                }

                if (currentFile.endsWith("yaml")) {
                    currentFileContent = yamlMapper.readTree(new File(currentFile)).toString();
                }
                else if (currentFile.endsWith("json")) {
                    currentFileContent = Files.readString(Path.of(currentFile));
                }
                else {
                    System.err.println("Unsupported file format. Only YAML and JSON are supported.");
                    System.exit(3);
                }
                result = new CompositeDiff(previousFileContent, currentFileContent).toString();
            } catch (IOException e) {
                System.err.println("Error while reading file: " + e.getMessage());
                System.exit(4);
            } catch (Exception e) {
                System.err.println("Error while processing files:; " + e.getMessage());
                System.exit(5);
            }

            ObjectMapper jsonMapper = new ObjectMapper();
            JsonNode jsonNode = jsonMapper.readTree(result);
            String changes = jsonNode.get("changes").toPrettyString();
            boolean isCompatible = jsonNode.get("changes").get("compatible").asBoolean();
            String compatibilityInfo = isCompatible ? "The API changes are compatible." : "The API changes are not compatible.";

            if (cmd.hasOption("o")) {
                String outputFile = cmd.getOptionValue("o");
                try (FileWriter writer = new FileWriter(outputFile)) {
                    writer.write(changes);
                    writer.write(compatibilityInfo);
                    System.out.println("Output written to " + outputFile);
                } catch (IOException e) {
                    System.err.println("Error writing to file: " + e.getMessage());
                    System.exit(6);
                }
            } else {
                System.out.println(changes);
                System.out.println(compatibilityInfo);
            }

            if (!isCompatible) {
                System.exit(1);
            }
        } catch (ParseException e) {
            System.err.println("Parsing failed: " + e.getMessage());
            formatter.printHelp(USAGE, options);
            System.exit(6);
        } catch (JsonProcessingException e) {
            System.err.println("Error processing JSON: " + e.getMessage());
            System.exit(7);
        }

    }
}
