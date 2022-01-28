maDMP-Ontology-Toolkit (MadPot)
--

**The MadPot toolkit** focus on supporting the usage and adoption of [maDMP ontology](http://semantics.id/madmp/terms/index-en.html).
Currently, there are two main functions of the toolkit: 
* **(i) transformation** 
* **(ii) validation**

The transformation function allow transformation between the JSON and TTL serializations of the maDMP. 
To this end, we published JSON-LD context used for the transformation is available on this repository (`src/main/resources/madmp-1.1.0.jsonld`), as well as online: [maDMP JSON-LD Context](http://semantics.id/madmp/context.jsonld).

The validation function rely on the SHACL constraint definitions that are originally contributed by TU Wien students as part of their work in the university, committed in the following [GitHub repository](https://github.com/Dzeri96/DCSO-SHACL). 
Some adjustments are implemented following the updates on the JSON-LD context definition.

## _How to run_

Prerequisites for running **MadPot**
* Java 11 or higher
* Apache Maven (only for compilation)
* (optional) maven compile: `mvn clean package`

After the complication finished (or after you downloaded the JAR file), you can execute the following script to do the transformation of maDMP files.
Note that you can add an optional argument `-v` in the end to run validation on the input file (for TTL file) or the transformation result (for JSON file).
* JSON to TTL: `java -jar madpot-[version]-jar-with-dependencies.jar -c transform -t json -i [input file] -o [output file]`
* TTL to JSON: `java -jar madpot-[version]-jar-with-dependencies.jar -c transform -t ttl -i [input file] -o [output file]`
* To run batch transformation, please take a look on the `batch` folder and look into the `test.sh` script as an example.

To conduct validation, you can run the following script. 
Note that the validation report will be stored as a turtle file with the following name `[filename][.shacl.ttl]` 
* `java -jar madpot-[version]-jar-with-dependencies.jar -c validate -t ttl -i [filename]`
