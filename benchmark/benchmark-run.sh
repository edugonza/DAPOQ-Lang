#!/bin/bash

MM=$1
QUERIES_PATH=$2
OUTPUT_CSV="$PWD/output.csv"
CLI="java -jar $PWD/../build/libs/*-cli.jar"

for i in $QUERIES_PATH/*.dapoql; do
  `$CLI -db $MM -dpf $i -o $OUTPUT_CSV`
done

for i in $QUERIES_PATH/*.sql; do
  `$CLI -db $MM -sqlf $i -o $OUTPUT_CSV`
done

for i in $QUERIES_PATH/*.dapoql; do
  `$CLI -db $MM -dpf $i -o $OUTPUT_CSV -mc`
done

