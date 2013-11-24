# time in ms
START=$(date +%s%N | cut -b1-13)

# execute the client
./client

END=$(date +%s%N | cut -b1-13)
DIFF=$(( $END - $START ))
#echo the time
echo "Total time: $DIFF milliseconds"
