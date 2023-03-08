#!/bin/bash

docker run --rm -d --network uyuni-network-1 -v /tmp/test-all-in-one:/tmp --name sshminion -h sshminion ghcr.io/$UYUNI_PROJECT/uyuni/opensuse-minion:$UYUNI_VERSION

