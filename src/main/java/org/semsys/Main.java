package org.semsys;

import com.apicatalog.jsonld.JsonLdError;
import org.apache.commons.cli.*;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws JsonLdError, IOException {
        CommandLine cmd = parseCMD(args);

        String command = cmd.getOptionValue("c").toLowerCase().trim();
        String type = cmd.getOptionValue("t").toLowerCase().trim();
        String input = cmd.getOptionValue("i").trim();
        String output = cmd.getOptionValue("o").trim();
        Boolean validate = cmd.hasOption("v");

        log.info("starting transformer and validator services");
        Transformer transformer = new Transformer();

        if (command.equals("transform")) {
            if (type.equals("json")) {
                transformer.madmpJsonToOnt(input, output, validate);
            } else if (type.equals("ttl")) {
                transformer.madmpOntToJson(input, output, validate);
            } else {
                log.info("command: '" + command + "' for filetype '" + type + "' is currently not supported");
            }
        } else if (command.equals("validate")) {
            if (type.equals("ttl")) {
                Resource report = Validator.INSTANCE.validate(input);
                RDFDataMgr.write(new FileOutputStream(input + ".shacl"), report.getModel(), Lang.TURTLE);
            } else {
                log.info("command: '" + command + "' is currently not supported for non-TTL files");
            }
        }
        log.info("semantic services started!");
    }


    public static CommandLine parseCMD(String[] args) {

        Options options = new Options();

        options.addRequiredOption("c", "command", true, "Command - 'transform' or 'validate' ");
        options.addRequiredOption("t", "type", true, "Input file type - 'json' or 'ttl' ");
        options.addRequiredOption("i", "input", true, "Input file name");
        options.addRequiredOption("o", "output", true, "Output file name");
        options.addOption("v", "validate", false, "(optional) validate ttl result from JSON transformation");

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
