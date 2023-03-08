#!/bin/bash

 docker exec controller-test-1 bash -c "export CUCUMBER_PUBLISH_TOKEN=95eb386d-cb35-4824-a477-d827783c8f0c && export PROVIDER=docker && export SERVER=uyuni-server-all-in-one-test-1 && export HOSTNAME=controller-test-1 && export SSH_MINION=sshminion && export MINION=sleminion && export RHLIKE_MINION=rhlike_minion && export DEBLIKE_MINION=deblike_minion && cd /testsuite && rake cucumber:poc_init_clients"
