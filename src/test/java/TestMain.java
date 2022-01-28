import com.apicatalog.jsonld.JsonLdError;
import org.junit.Test;
import org.semsys.Main;

import java.io.IOException;

public class TestMain {

    @Test
    public void TestMain() throws IOException, JsonLdError {

        Main.main(new String[]{"-c", "transform", "-t", "ttl", "-i", "src/test/resources/ttl/gdp_sunshine-maDMP.ttl", "-o", "output.json", "-v"});

    }
}
