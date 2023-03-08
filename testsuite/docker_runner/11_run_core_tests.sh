#!/bin/bash
docker exec controller-test-1 bash -c "export CUCUMBER_PUBLISH_TOKEN=95eb386d-cb35-4824-a477-d827783c8f0c && export PROVIDER=docker && export SERVER=uyuni-server-all-in-one-test-1 && export HOSTNAME=controller-test-1 && cd /testsuite && rake cucumber:poc_core"

