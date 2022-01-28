import com.apicatalog.jsonld.JsonLdError;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.semsys.Transformer;

import java.io.File;
import java.io.IOException;

public class TestTransformer {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void TestTransformer() throws IOException, JsonLdError {
        Transformer transformer = new Transformer();

        String jsonInput = "src/test/resources/json/ex9-dmp-long.json";
        String rdfInput = "src/test/resources/ttl/ex1-header-fundedProject.ttl";

        File file = folder.newFolder();

        String rdfOutputOfJsonInput = file + "1_rdfOutputOfJsonInput.ttl";
        String regenerateJsonInput = file + "1_regeneratedJsonInput.json";
        String jsonOutputOfRdfInput = file + "2_jsonOutputOfRdfInput.json";
        String regeneratedRdfInput = file + "2_regeneratedRdfInput.ttl";

        transformer.madmpJsonToOnt(jsonInput, rdfOutputOfJsonInput, false);
        transformer.madmpOntToJson(rdfOutputOfJsonInput, regenerateJsonInput, false);

        transformer.madmpOntToJson(rdfInput, jsonOutputOfRdfInput, false);
        transformer.madmpJsonToOnt(jsonOutputOfRdfInput, regeneratedRdfInput, false);
    }
}
