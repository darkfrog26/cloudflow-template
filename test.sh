#!/usr/bin/env bash

for str in $(cat data.json)
do
  echo "Using $str"
  curl -i -X POST http://localhost:3000 -H "Content-Type: application/json" --data "$str"
done