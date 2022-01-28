package org.semsys;

import com.apicatalog.jsonld.JsonLd;
import com.apicatalog.jsonld.JsonLdError;
import jakarta.json.*;
import jakarta.json.stream.JsonGenerator;
import org.apache.commons.io.FileUtils;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Transformer {

    private Property propertyDMP = ResourceFactory.createProperty("https://w3id.org/madmp/terms#", "dmp");
    private Resource clsDMP = ResourceFactory.createResource("https://w3id.org/madmp/terms#DMP");
    private Resource namedIndividual = ResourceFactory.createResource(OWL.NS + "NamedIndividual");

    private String madmpOntology = "madmp-1.1.0.ttl";
    private String madmpContext = "madmp-1.1.0.jsonld";
    private File madmpContextFile;

    public Transformer() throws IOException {
        InputStream contextIS = Transformer.class.getClassLoader().getResourceAsStream(madmpContext);
        madmpContextFile = File.createTempFile(madmpContext, ".jsonld");
        madmpContextFile.deleteOnExit();
        FileUtils.copyInputStreamToFile(contextIS, madmpContextFile);
    }

    public void madmpJsonToOnt(String madmpJsonFile, String outputOntFile) throws JsonLdError, FileNotFoundException {
        Model model = madmpJsonToModel(new File(madmpJsonFile));
        RDFDataMgr.write(new FileOutputStream(outputOntFile), model, Lang.TURTLE);
    }

    public void madmpOntToJson(String madmpOntFile, String outputJsonFile) throws JsonLdError, IOException {

        JsonObject madmpJsonObject = madmpModelToJsonObject(RDFDataMgr.loadModel(madmpOntFile));
        File tempJsonOutput = new File(outputJsonFile);
        madmpJsonToFile(madmpJsonObject, tempJsonOutput);
    }

    /**
     * A function to write json data into output file
     **/
    private void madmpJsonToFile(JsonValue jsonValue, File outputFile) throws IOException {
        Map<String, Object> properties = new HashMap<>(1);
        properties.put(JsonGenerator.PRETTY_PRINTING, true);

        JsonWriterFactory writerFactory = Json.createWriterFactory(properties);
        FileWriter fileWriter = new FileWriter(outputFile);
        JsonWriter jsonWriter = writerFactory.createWriter(fileWriter);
        if (jsonValue.getValueType().equals(JsonValue.ValueType.ARRAY)) {
            jsonWriter.writeArray(jsonValue.asJsonArray());
        } else if (jsonValue.getValueType().equals(JsonValue.ValueType.OBJECT)) {
            jsonWriter.writeObject(jsonValue.asJsonObject());
        }

        fileWriter.flush();
        fileWriter.close();
    }

    /**
     * A function to transform an maDMP JSON instance into an maDMP ontology instance.
     *
     * @param dmpJsonFile input maDMP in JSON
     * @return JenaModel
     * @throws JsonLdError
     */
    private Model madmpJsonToModel(File dmpJsonFile) throws JsonLdError {
        Model madmpModel = ModelFactory.createDefaultModel();
        InputStream madmpIS = Transformer.class.getClassLoader().getResourceAsStream(madmpOntology);
        RDFDataMgr.read(madmpModel, madmpIS, Lang.TURTLE);

        Model model = ModelFactory.createDefaultModel();
        model.setNsPrefixes(madmpModel.getNsPrefixMap());
        JsonArray jsonArray = JsonLd.expand(dmpJsonFile.toURI())
                .context(madmpContextFile.toURI()).get();
        InputStream is = new ByteArrayInputStream(jsonArray.toString().getBytes());
        RDFDataMgr.read(model, is, Lang.JSONLD);

        // hack to ensure correct DMP class representation
        // ... since the top-level JSON object always represent DMP class
        Statement hasDMPStmt = model.getProperty(null, propertyDMP);
        model.add((Resource) hasDMPStmt.getObject(), RDF.type, clsDMP);
        model.remove(hasDMPStmt);

        return model;
    }

    private JsonObject madmpModelToJsonObject(Model model) throws IOException, JsonLdError {

        // ** hack to prepare the transformation from ontology instances to JSON object, which consist of
        // (i) transform rdf:type "madmp:DMP" to property "dmp", and
        // (ii) remove all named individual statements from the triples
        Statement hasDMPStmt = model.listStatements(null, RDF.type, clsDMP).next();
        model.add(model.createResource(), propertyDMP, hasDMPStmt.getSubject());
        model.removeAll(null, RDF.type, namedIndividual);

        // serializing the Jena model as nquads, since the JSON serializer only accept nquads
        File tempFile = File.createTempFile("temp", ".nq");
        tempFile.deleteOnExit();
        RDFDataMgr.write(new FileOutputStream(tempFile), model, Lang.NQUADS);

        // read the nquads file and save it as expanded JSON-LD into a temporary file,
        JsonArray expandedJsonLd = JsonLd.fromRdf(tempFile.toURI()).get();

        File tempJson = File.createTempFile("temp", ".json");
        tempJson.deleteOnExit();
        madmpJsonToFile(expandedJsonLd, tempJson);

        // create a JSON Object from the temporary file.
        JsonObject framedJsonLd = JsonLd.frame(tempJson.toURI(), madmpContextFile.toURI()).get();

        // getting only the data part ("@graph") from the JSON-LD
        JsonArray graph = framedJsonLd.getJsonArray("@graph");

        // find the top-level DMP from the data
        // ... at the moment we assume that only a single DMP is contained within a file.
        Iterator<JsonValue> values = graph.iterator();
        JsonObject dmpJson = null;
        while (dmpJson == null && values.hasNext()) {
            JsonValue value = values.next();
            if (value instanceof JsonObject && value.asJsonObject().containsKey("dmp")) {
                dmpJson = value.asJsonObject();
            }
        }

        // adjust some values (e.g., removeing @id, @type) in the JSON data to ensure compatibility with JSON formats
        if (dmpJson != null)
            dmpJson = adjustMadmpJsonObjectValues(dmpJson);

        return dmpJson;
    }

    /**
     * Adjustment of madmp JSON-LD object values to normal JSON object
     *
     * @param madmpJsonObject
     * @return JsonObject
     */
    private JsonObject adjustMadmpJsonObjectValues(JsonObject madmpJsonObject) {
        String jsonString = madmpJsonObject.toString();

        // convert integer/decimal without value and type
        jsonString = jsonString.replaceAll("\\{\\s*\\\"@value\\\"\\s*:\\s*\\\"(\\d+)\\\"\\s*,\\s*\\\"@type\\\"\\s*:\\s*\\\"xsd:integer\\\"\\s*}", "$1");
        jsonString = jsonString.replaceAll("\\{\\s*\\\"@value\\\"\\s*:\\s*\\\"(\\d+.\\d+)\\\"\\s*,\\s*\\\"@type\\\"\\s*:\\s*\\\"xsd:decimal\\\"\\s*}", "$1");

        // remove all ids
        jsonString = jsonString.replaceAll("\\s*\\\"@id\\\"\\s*:\\s*\\\"[A-Za-z\\/.\\s0-9:_-]*,?\\\"\\s*,", "");
        jsonString = jsonString.replaceAll("\\s*\\\"@id\\\"\\s*:\\s*\\\"[A-Za-z\\/.\\s0-9:_-]*\\\",?\\s*", "");
        jsonString = jsonString.replaceAll("\\s*\\\"@id\\\"\\s*:\\s*\\[(\\s*\\\"[A-Za-z\\/.\\s0-9:_-]*\\\",?)*\\s*]?,?", "");

        // remove all types
        jsonString = jsonString.replaceAll("\\s*\\\"@type\\\"\\s*:\\s*\\\"[A-Za-z\\/.\\s0-9:_-]*,?\\\"\\s*,", "");
        jsonString = jsonString.replaceAll("\\s*\\\"@type\\\"\\s*:\\s*\\\"[A-Za-z\\/.\\s0-9:_-]*\\\",?\\s*", "");
        jsonString = jsonString.replaceAll("\\s*\\\"@type\\\"\\s*:\\s*\\[(\\s*\\\"[A-Za-z\\/.\\s0-9:_-]*\\\",?)*\\s*]?,?", "");

        return Json.createReader(new StringReader(jsonString)).readObject();
    }
}
