#!/bin/bash

HTTP_METHOD=$1
URL=$2
ITERATIONS=$3

nohup ./run.bash > app.log 2>&1 &
echo "`date` Waiting for application to start..."
sleep 5

grep_result=
while [ -n "$grep_result" ]
do
    sleep 5
    grep_result=`grep "Started Application in" app.log | grep -v grep`
done

echo "`date` Starting test for $HTTP_METHOD $URL"

duration_sum=0
for i in `seq 1 $ITERATIONS`
do
    echo "`date` Iteration $i"
    startTime=`perl -MTime::HiRes -e 'printf("%.0f\n",Time::HiRes::time()*1000)'`
    curl -s -X $HTTP_METHOD "$URL"
    endTime=`perl -MTime::HiRes -e 'printf("%.0f\n",Time::HiRes::time()*1000)'`
    duration=$(expr $endTime - $startTime)
    echo "Response time: $duration [ms]"
    duration_sum=$(expr $duration_sum + $duration)
done
avg_duration=$(expr $duration_sum / $ITERATIONS)
echo "Avg Response Time: $avg_duration [ms]"
echo "`date` End of test"