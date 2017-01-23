#!/bin/bash

mvn clean install && scp target/adtAnalysis*.jar sparkmaster@10.156.207.26:~/adtAnalysis/ && ssh -f sparkmaster@10.156.207.26 "bash -c 'cd ~/adtAnalysis && nohup ./run.bash > app.log 2>&1 &'"