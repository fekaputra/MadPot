package org.semsys;

import com.apicatalog.jsonld.JsonLdError;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonWriter;
import org.apache.commons.cli.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws JsonLdError, IOException {
        CommandLine cmd = parseCMD(args);

        String input = cmd.getOptionValue("i").trim();
        Boolean transform = cmd.hasOption("t");
        Boolean validate = cmd.hasOption("v");

        String inputType = FilenameUtils.getExtension(input);
        String output = FilenameUtils.getBaseName(input);

        log.info("starting maDMP ontology toolkit");
        Transformer transformer = new Transformer();

        if (validate) {
            if (inputType.equals("ttl")) {
                Resource report = Validator.INSTANCE.validateRDF(input);
                RDFDataMgr.write(new FileOutputStream(output + "-report.ttl"), report.getModel(), Lang.TURTLE);
            } else if (inputType.equals("json")) {
                JsonWriter writer = Json.createWriter(new FileWriter(output + "-report.json"));
                JsonObject report = Validator.INSTANCE.validateJSON(input);
                writer.writeObject(report);
                writer.close();
            } else {
                log.info("validation for input with type '" + inputType + "' is currently not supported for non-TTL files");
            }
        }
        if (transform) {
            if (inputType.equals("json")) {
                transformer.madmpJsonToOnt(input, output + "-transformed.ttl");
            } else if (inputType.equals("ttl")) {
                transformer.madmpOntToJson(input, output + "-transformed.json");
            } else {
                log.info("transformation for input with type '" + inputType + "' is currently not supported");
            }
        }
        log.info("finished maDMP ontology toolkit");
    }


    public static CommandLine parseCMD(String[] args) {

        Options options = new Options();

        options.addRequiredOption("i", "input", true, "Input file name");

        options.addOption("t", "transform", false, "Transform input file based on its extension (JSON -> TTL or TTL -> JSON)");
        options.addOption("v", "validate", false, "Validate input file based on SHACL (for ttl) or JSON-LD context (for json)");

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            formatter.printHelp("utility-name", options);
            System.exit(1);
        }

        return cmd;
    }
}
