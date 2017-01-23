#!/bin/bash

HTTP_METHOD=$1
URL=$2
ITERATIONS=$3

./run.bash
echo "`date` Starting test for $HTTP_METHOD $URL"

for i in `seq 1 $ITERATIONS`
do
    echo "`date` Iteration $i"
    startTime=`perl -MTime::HiRes -e 'printf("%.0f\n",Time::HiRes::time()*1000)'`
    curl -s -X $HTTP_METHOD "$URL" > /dev/null
    endTime=`perl -MTime::HiRes -e 'printf("%.0f\n",Time::HiRes::time()*1000)'`
    duration=$(expr $endTime - $startTime)
    echo "Response time: $duration"
done

echo "`date` End of test"