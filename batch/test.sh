#!/bin/bash

for filename in ../src/test/resources/ttl/*.ttl;
do
  basename $filename
  f="$(basename -- $filename)"
  java -jar madpot-1.0.0-jar-with-dependencies.jar -t -i "$filename"
done

for filename in ../src/test/resources/json/*.json
do
  basename $filename
  f="$(basename -- $filename)"
  java -jar madpot-1.0.0-jar-with-dependencies.jar -v -t -i "$filename"
done
