#java -jar Master.jar 100k lines window


echo  "Running tests";

function test() {
    file=$1 
    for i in 1 25 50
    do
        java -jar Master.jar file $i 1;
        echo "FINISHED: file lines: ${i} window: 1";
        java -jar Master.jar file i 5;
        echo "FINISHED: file lines: ${i} window: 5";
        java -jar Master.jar file i 10;
        echo "FINISHED: file lines: ${i} window: 10";
    done
}´

test "100k.csv"
test "250k.csv"
test "500k.csv"
test "1000k.csv"

echo "Tests finished";