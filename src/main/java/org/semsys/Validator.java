package org.semsys;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import org.apache.commons.io.IOUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.topbraid.shacl.validation.ValidationUtil;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public enum Validator {

    INSTANCE;

    private String madmpSHACLFile = "madmp-1.1.0-shacl.ttl";
    private String madmpContextFile = "madmp-1.1.0-context.json";
    private Model madmpSHACL = null;

    public Resource validateRDF(Model madmpModel) {
        if (madmpSHACL == null) {
            madmpSHACL = ModelFactory.createDefaultModel();
            InputStream shaclIS = Validator.class.getClassLoader().getResourceAsStream(madmpSHACLFile);
            RDFDataMgr.read(madmpSHACL, shaclIS, Lang.TURTLE);
        }

        return ValidationUtil.validateModel(madmpModel, madmpSHACL, true);
    }

    public Resource validateRDF(String madmpFile) throws FileNotFoundException {
        Model model = ModelFactory.createDefaultModel();
        FileInputStream is = new FileInputStream(madmpFile);
        RDFDataMgr.read(model, is, Lang.TURTLE);
        return validateRDF(model);
    }

    public JsonObject validateJSON(String madmpJSON) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        FileInputStream inputIS = new FileInputStream(madmpJSON);
        String inputString = IOUtils.toString(inputIS, StandardCharsets.UTF_8);
        Set<String> inputKeys = getKeysInJsonUsingMaps(inputString, mapper);
        inputIS.close();

        mapper = new ObjectMapper();
        InputStream contextIS = Validator.class.getClassLoader().getResourceAsStream(madmpContextFile);
        String contextString = IOUtils.toString(contextIS, StandardCharsets.UTF_8);
        Set<String> contextKeys = getKeysInJsonUsingMaps(contextString, mapper);
        contextIS.close();

        inputKeys.removeAll(contextKeys);

        JsonObjectBuilder builder = Json.createObjectBuilder();
        if (inputKeys.isEmpty())
            builder.add("conform", "true");
        else {
            builder.add("unknown_keys", Json.createArrayBuilder(inputKeys));
            builder.add("conform", "false");
        }

        return builder.build();
    }

    private Set<String> getKeysInJsonUsingMaps(String json, ObjectMapper mapper) throws JsonProcessingException {
        Set<String> keys = new HashSet<>();
        Map<String, Object> jsonElements = mapper.readValue(json, new TypeReference<>() {
        });
        getAllKeys(jsonElements, keys);
        return keys;
    }

    private void getAllKeys(Map<String, Object> jsonElements, Set<String> keys) {

        jsonElements.entrySet()
                .forEach(entry -> {
                    keys.add(entry.getKey());
                    if (entry.getValue() instanceof Map) {
                        Map<String, Object> map = (Map<String, Object>) entry.getValue();
                        getAllKeys(map, keys);
                    } else if (entry.getValue() instanceof List) {
                        List<?> list = (List<?>) entry.getValue();
                        list.forEach(listEntry -> {
                            if (listEntry instanceof Map) {
                                Map<String, Object> map = (Map<String, Object>) listEntry;
                                getAllKeys(map, keys);
                            }
                        });
                    }
                });
    }
}
