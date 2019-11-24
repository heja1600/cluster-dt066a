#java -jar Master.jar 100k lines window

echo  "Running tests";

function test() {
    file=$1 
    for lines in 10 20 30 40 50
    do
        for window in 1 5 10
        do
            java -jar Master.jar ${file} ${lines} ${window};
            echo FINISHED: ${file} lines: ${lines} window: ${window};
        done
    done
}

test 100k.csv
test 250k.csv
test 500k.csv
test 1000k.csv

echo tests finished