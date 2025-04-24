package com.apiprotector.cli;

import com.apiprotector.core.CompositeDiff;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.cli.*;

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
                System.exit(1);
            }

            String previousFile = remainingArgs[0];
            String currentFile = remainingArgs[1];
            String previousFileContent;
            String currentFileContent;
            String result = "";

            try {
                previousFileContent = Files.readString(Path.of(previousFile));
                currentFileContent = Files.readString(Path.of(currentFile));
                result = new CompositeDiff(previousFileContent, currentFileContent).toString();


            } catch (IOException e) {
                System.err.println("Error while reading file: " + e.getMessage());
                System.exit(2);
            } catch (Exception e) {
                System.err.println("Error while processing files:; " + e.getMessage());
                System.exit(3);
            }

            if (cmd.hasOption("o")) {
                String outputFile = cmd.getOptionValue("o");
                try (FileWriter writer = new FileWriter(outputFile)) {
                    writer.write(result);
                    System.out.println("Output written to " + outputFile);
                } catch (IOException e) {
                    System.err.println("Error writing to file: " + e.getMessage());
                    System.exit(4);
                }
            } else {
                System.out.println(result);
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(result);

            if (jsonNode.get("changes").get("compatible").asBoolean()) {
                System.out.println("The API changes are compatible.");
            } else {
                System.out.println("The API changes are not compatible.");
            }

        } catch (ParseException e) {
            System.err.println("Parsing failed: " + e.getMessage());
            formatter.printHelp(USAGE, options);
            System.exit(5);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }
}
