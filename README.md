maDMP-Ontology-Toolkit (MadPot)
--

**The MadPot toolkit** focus on supporting the usage and adoption of [maDMP ontology](http://semantics.id/madmp/terms/index-en.html).
Currently, there are two main functions of the toolkit: 
* **(i) transformation** 
* **(ii) validation**

The transformation function allow transformation between the JSON and TTL serializations of the maDMP. 
To this end, we published JSON-LD context used for the transformation is available on this repository (`src/main/resources/madmp-1.1.0.jsonld`), as well as online: [maDMP JSON-LD Context](http://semantics.id/madmp/context.jsonld).

The validation function for RDF (turtle) file rely on the SHACL constraint definitions that are originally contributed by TU Wien students as part of their work in the university, committed in the following [GitHub repository](https://github.com/Dzeri96/DCSO-SHACL). 
Some adjustments are implemented following the updates on the JSON-LD context definition.
For JSON-LD file, we check the keys contained within the input JSON file against all keys defined in the JSON-LD context, where any mismatch would mean incompatibility with the current JSON schema definition.

## _How to run_

Prerequisites for running **MadPot**
* Java 11 or higher
* Apache Maven (only for compilation)
* (optional) maven compile: `mvn clean package`

After the complication finished (or after you downloaded the JAR file), 
you can execute the following script to do the transformation of maDMP files, which triggerred by the command `-t`.
Note that the application will automatically detect the extension of file, and won't process any file other than JSON and TTL at the moment.
It will produce the output file in the form of `[input file]-transformed.[json/ttl]`.

* JSON to TTL: `java -jar madpot-[version]-jar-with-dependencies.jar -t -i [input file].json`
* TTL to JSON: `java -jar madpot-[version]-jar-with-dependencies.jar -t -i [input file].ttl`

For validation, you can run similar script with `-v` instead of `-t`.
It will produce the validation result in the form of `[input file]-report.[json/ttl]`.
* For JSON file: `java -jar madpot-[version]-jar-with-dependencies.jar -v -i [input file].json`
* For TTL file: `java -jar madpot-[version]-jar-with-dependencies.jar -v -i [input file].ttl`

Furthermore, you can combine both commands (validate and transform) in one go, which will produce two output files (report and transformed).
To run batch transformation, please take a look on the `batch` folder and look into the `test.sh` script as an example.

* For JSON file: `java -jar madpot-[version]-jar-with-dependencies.jar -v -t -i [input file].json`
* For TTL file: `java -jar madpot-[version]-jar-with-dependencies.jar -v -t -i [input file].ttl`

