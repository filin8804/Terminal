# Terminal
Test :

main(){
x=$1
i=0
while [ 1 ]
do
echo $x
export x=`expr $x + $3`
export i=`expr $i + 1`
if [ "$i" == "$2" ]
then
break
fi
done
}

main 0 100 1
