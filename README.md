# Terminal
Test :<br>
<br>
main(){<br>
x=$1<br>
i=0<br>
while [ 1 ]<br>
do<br>
echo $x<br>
export x=`expr $x + $3`<br>
export i=`expr $i + 1`<br>
if [ "$i" == "$2" ]<br>
then<br>
break<br>
fi<br>
done<br>
}<br>
<br>
main 0 100 1<br>
<br>
