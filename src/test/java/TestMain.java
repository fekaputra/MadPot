import com.apicatalog.jsonld.JsonLdError;
import org.junit.AfterClass;
import org.junit.Test;
import org.semsys.Main;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class TestMain {

    @AfterClass
    public static void cleanUp() {
        File folder = new File(".");
        Arrays.stream(folder.listFiles())
                .filter(f -> f.getName().endsWith(".ttl"))
                .forEach(File::delete);
        Arrays.stream(folder.listFiles())
                .filter(f -> f.getName().endsWith(".json"))
                .forEach(File::delete);
    }

    @Test
    public void TestValidate() throws IOException, JsonLdError {
        Main.main(new String[]{"-i", "src/test/resources/ttl/gdp_sunshine-maDMP.ttl", "-v"});
        Main.main(new String[]{"-i", "src/test/resources/json/ex8-dmp-minimal-content.json", "-v"});
    }

    @Test
    public void TestValidateWithErrors() throws IOException, JsonLdError {
        Main.main(new String[]{"-i", "src/test/resources/ttl/invalid_many_examples.ttl", "-v"});
        Main.main(new String[]{"-i", "src/test/resources/json/ex9-dmp-long-incorrect.json", "-v"});
    }

    @Test
    public void TestTransform() throws IOException, JsonLdError {
        Main.main(new String[]{"-i", "src/test/resources/ttl/gdp_sunshine-maDMP.ttl", "-v"});
        Main.main(new String[]{"-i", "src/test/resources/json/ex8-dmp-minimal-content.json", "-v"});
    }

    @Test
    public void TestTransformAndValidate() throws IOException, JsonLdError {
        Main.main(new String[]{"-i", "src/test/resources/ttl/gdp_sunshine-maDMP.ttl", "-v", "-t"});
        Main.main(new String[]{"-i", "src/test/resources/json/ex8-dmp-minimal-content.json", "-v", "-t"});
    }
}
