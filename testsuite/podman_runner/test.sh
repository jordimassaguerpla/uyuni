#!/bin/bash
set -ex
count=1

while [ 1 == 1 ];do
	echo "DEBUG try ${count}..."
	time ./run
        let count=count+1
done
