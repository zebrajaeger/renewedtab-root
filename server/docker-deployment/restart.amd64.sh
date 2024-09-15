#!/bin/sh

echo "##### Stop stack #####"
docker compose stop
echo "##### Remove Container #####"
docker container inspect renewedtab > /dev/null 2>&1 && echo "exists: remove container" &&  docker container rm renewedtab
echo "##### Remove image #####"
docker image inspect zebrajaeger/renewedtab:amd64-0.0.1-SNAPSHOT > /dev/null 2>&1 && echo "exists: remove image" && docker image rm zebrajaeger/renewedtab:amd64-0.0.1-SNAPSHOT
echo "#####Load image #####"
docker image load -i  ph5-amd64-0.0.1-SNAPSHOT.tar
echo "##### Start stack #####"
docker compose up -d
