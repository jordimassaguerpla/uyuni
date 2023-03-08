#!/bin/bash

if [ -z "${UYUNI_PROJECT}" ];then
    echo "Set and export UYUNI_PROJECT variable"
    exit 1
fi

if [ -z "${UYUNI_VERSION}" ];then
    echo "Set and export UYUNI_VERSION variable"
    exit 2
fi

echo "Killing old containers"
containers="deblike_minion rhlike_minion sleminion sshminion uyuni-server-all-in-one-test-1 controller-test-1"
for i in ${containers};do
    docker kill ${i}
done

echo "Remove network"
docker network rm uyuni-network-1


