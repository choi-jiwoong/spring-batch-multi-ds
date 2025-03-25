#!/bin/bash

URL="http://localhost:8080/login"

for i in {1..15}
do
  echo "Calling $URL/$i"
  curl -s "$URL/$i"
  echo ""
  sleep 0.2
done