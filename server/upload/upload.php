<?php
include ('../conn.php');

$description=$_POST["description"];
$data=$_POST["testdata"];
$testtime=$_POST["testtime"];
$model=$_POST["model"];
$brightness=$_POST["brightness"];
$interval = $_POST["interval"];

$dataSplit=split(";",$data);
$dataFirst = $dataSplit[0];
$dataLast = end($dataSplit);
$dataFirstSplit = split(" ",$dataFirst);
$dataLastSplit = split(" ",$dataLast);

$duration = intval($dataLastSplit[0]) - intval($dataFirstSplit[0]);
$startPercentage = intval($dataFirstSplit[1]);
$endPercentage = intval($dataLastSplit[1]);


if($startPercentage-$endPercentage==0){
	$projectedLife=0;
}else{
	if($startPercentage-$endPercentage>=2){
	    //Calculate accurate projected life.
        $accurate_start_percentage = intval($startPercentage)-1;
        $accurate_stop_percentage = intval($endPercentage);
        $accurate_start_time = 0;
        $accurate_stop_time = 1;
        $testData=$dataSplit;
        $findStartFlag = false;
        foreach ($testData as $testDataLine) {
            $testDataLineSplit=split(" ", $testDataLine);
            if(!$findStartFlag && intval($testDataLineSplit[1])==$accurate_start_percentage){
                $findStartFlag = true;
                $accurate_start_time = intval($testDataLineSplit[0]);
            }
            if(intval($testDataLineSplit[1])==$accurate_stop_percentage){
                $accurate_stop_time = intval($testDataLineSplit[0]);
                break;
            }

        }
        $accurate_projected_life = ($accurate_stop_time - $accurate_start_time)*100 / ($accurate_start_percentage - $accurate_stop_percentage);
        $projectedLife = $accurate_projected_life;
	}else{
		$projectedLife = $duration*100/($startPercentage-$endPercentage);
	}
}



$sql="INSERT INTO `testdata`(`id`, `device`, `description`, `brightness`, `interval`, `duration`, `start_percentage`, `stop_percentage`, `projected_life`, `data`, `testtime`) VALUES (NULL,'$model','$description','$brightness',$interval,$duration,$startPercentage,$endPercentage,$projectedLife,'$data','$testtime')";

$result=mysql_query($sql);


echo "Success"

?>
