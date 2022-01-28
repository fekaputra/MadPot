package org.semsys;

import com.apicatalog.jsonld.JsonLdError;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws JsonLdError, IOException {
        CommandLine cmd = parseCMD(args);

        String type = cmd.getOptionValue("t");
        String input = cmd.getOptionValue("i");
        String output = cmd.getOptionValue("o");

        log.info("starting semantic services");
        Transformer transformer = new Transformer();
        if (type.equals("json")) {
            transformer.madmpJsonToOnt(input, output);
        } else if (type.equals("ttl")) {
            transformer.madmpOntToJson(input, output);
        } else {
            log.info("type: '" + type + "' is currently not supported");
        }
        log.info("semantic services started!");
    }


    public static CommandLine parseCMD(String[] args) {

        Options options = new Options();

        options.addRequiredOption("t", "type", true, "Input file type ('json' or 'ttl')");
        options.addRequiredOption("i", "input", true, "Input file name");
        options.addRequiredOption("o", "output", true, "Output file name");

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);
            System.exit(1);
        }

        return cmd;
    }
}
