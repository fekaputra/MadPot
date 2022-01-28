maDMP Transformer
--

This CLI tool focus on the transformation between maDMP serializations.
The tool especially focus on the transformation between JSON and TTL serialization of the maDMP. 
The JSON-LD context used for the transformation is available on this repository (`src/main/resources/madmp-1.1.0.jsonld`), as well as online: [maDMP JSON-LD Context](http://semantics.id/madmp/context.jsonld)

## How to run

Prerequisites for running maDMP-transformer
* Java 11 or higher
* Apache Maven (only for compilation)

To run the transformation, please run the following script:
* (optional) maven compile: `mvn clean package`
* run the jar file: `java -jar target/madmp-transformer-0.9-SNAPSHOT-jar-with-dependencies.jar -t <input-type> -i <input-file> -o <designated-output-file>`
* example single executions
  * JSON to TTL: `java -jar target/madmp-transformer-0.9-SNAPSHOT-jar-with-dependencies.jar -t json -i src/test/resources/json/ex1-header-fundedProject.json -o output.ttl`
  * TTL to JSON: `java -jar target/madmp-transformer-0.9-SNAPSHOT-jar-with-dependencies.jar -t ttl -i src/test/resources/ttl/gdp_sunshine-maDMP.ttl -o output.json`

To run batch transformation, please take a look on the `batch` folder
