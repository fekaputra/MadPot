#!/bin/bash

for filename in ../src/test/resources/ttl/*.ttl;
do
  basename $filename
  f="$(basename -- $filename)"
  java -jar ../target/madmp-transformer-0.9-SNAPSHOT-jar-with-dependencies.jar -t ttl -i "$filename" -o "$f".json
done

for filename in ../src/test/resources/json/*.json
do
  basename $filename
  f="$(basename -- $filename)"
  java -jar ../target/madmp-transformer-0.9-SNAPSHOT-jar-with-dependencies.jar -t json -i "$filename" -o "$f".ttl
done