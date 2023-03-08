#!/bin/bash
src_dir=$(cd $(dirname "$0")/../.. && pwd -P)

 docker run --rm -d --network uyuni-network-1 -v /tmp/test-all-in-one:/tmp -v ${src_dir}/testsuite/docker_runner/salt-minion-entry-point.sh:/salt-minion-entry-point.sh --name sleminion -h sleminion ghcr.io/$UYUNI_PROJECT/uyuni/opensuse-minion:$UYUNI_VERSION bash -c "/salt-minion-entry-point.sh uyuni-server-all-in-one-test-1 1-SUSE-KEY-x86_64" && \
 docker exec sleminion bash -c "ssh-keygen -A && /usr/sbin/sshd -e" && \
 docker exec sleminion bash -c "if [ ! -d /root/.ssh ];then mkdir /root/.ssh/;chmod 700 /root/.ssh;fi;cp /tmp/authorized_keys /root/.ssh/"
