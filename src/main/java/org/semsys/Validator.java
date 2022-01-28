package org.semsys;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.topbraid.shacl.validation.ValidationUtil;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public enum Validator {

    INSTANCE;

    private String madmpSHACLFile = "madmp-1.1.0-shacl.ttl";
    private Model madmpSHACL = null;

    public Resource validate(Model madmpModel) {
        if (madmpSHACL == null) {
            madmpSHACL = ModelFactory.createDefaultModel();
            InputStream shaclIS = Validator.class.getClassLoader().getResourceAsStream(madmpSHACLFile);
            RDFDataMgr.read(madmpSHACL, shaclIS, Lang.TURTLE);
        }

        return ValidationUtil.validateModel(madmpModel, madmpSHACL, true);
    }

    public Resource validate(String madmpFile) throws FileNotFoundException {
        Model model = ModelFactory.createDefaultModel();
        FileInputStream is = new FileInputStream(madmpFile);
        RDFDataMgr.read(model, is, Lang.TURTLE);
        return validate(model);
    }
}
