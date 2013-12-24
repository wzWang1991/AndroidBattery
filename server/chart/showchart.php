<?php

$ids = $_POST['id'];



include ('../conn.php');

$sql="select * from testdata where ";
$counter=0;
foreach ($ids as $tmpid) {
	if($counter==0) $sql=$sql."id="."'".$tmpid."'";
	else $sql=$sql." or id="."'".$tmpid."'";
	$counter=$counter+1;
}

$result=mysql_query($sql);

?>


<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Battery Benchmark</title>
<link rel="stylesheet" type="text/css" media="screen, projection" href="demo.css" />

		<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.8.2/jquery.min.js"></script>
		<script type="text/javascript">
$(function () {
        $('#container').highcharts({
            chart: {
                type: 'spline'
            },
            title: {
                text: 'The Battery Consumption'
            },
            subtitle: {
                text: ' '
            },
            credits: {
                enabled:false
            },
            plotOptions: {
                spline: {
                    lineWidth: 5,
                    turboThreshold: 2000,
                    marker: {
                        enabled:false
                    }
                }

            },
            xAxis: {
                title:{
                    text: 'Time (s)'
                },
                type: 'linear'
                
            },
            yAxis: {
                title: {
                    text: 'Battery (%)'
                },
                max:100,
                min: 0
            },
            tooltip: {
                formatter: function() {
                        return '<b>'+ this.series.name +'</b><br/>'+
                        this.x+'s '+this.y+'%';
                }
            },
            
            series: [
                <?php
                    while($row=mysql_fetch_array($result))
                    {
                        echo "{";
                        echo "name: ";
                        echo "'".$row['description'].", ".$row['brightness'].", Interval: ".$row['interval']."s',";

                        $testData=split(";",$row['data']);
                        echo "data: [";
                        foreach ($testData as $testDataLine) {
                            $testDataLineSplit=split(" ", $testDataLine);
                            if($testDataLine==end($testData))
                                echo "[".$testDataLineSplit[0].",".$testDataLineSplit[1]."]";
                            else 
                                echo "[".$testDataLineSplit[0].",".$testDataLineSplit[1]."],";

                        }
                        echo "]";
                        echo "},";
                    }
                ?>
                
            ]
        });
    });
    

</script>
</head>
<body>
	<p class="title">Battery Benchmark</p>
	<form action = "chart/showchart.php" method = "post"> 
	<table>
		<tr>
			<th class="col-name">ID</th>
			<th>Device</th>
			<th class="col-comment">Description</th>
			<th>Brightness</th>
			<th>Interval</th>
			<th>Duration</th>
			<th>Start %</th>
			<th>End %</th>
			<th>Projected Life</th>
			<th class="col-response">Test time</th>
		</tr>

		<?php
	$result=mysql_query($sql);
		//http://www.w3school.com.cn/php/func_mysql_fetch_array.asp
		while($row=mysql_fetch_array($result)){
			$id=$row['id'];
			$device=$row['device'];
			$description=$row['description'];
			$brightness=$row['brightness'];
			$interval=$row['interval'];
			$duration=$row['duration'];
			$start_percentage=$row['start_percentage'];
			$stop_percentage=$row['stop_percentage'];
			$projected_life=$row['projected_life'];



			$testtime=$row['testtime'];

			echo "<tr>";
			echo "<td class=\"col-name\">".$id."</td>";
			echo "<td>".$device."</td>";
			echo "<td class=\"col-comment\">".$description."</td>";
			echo "<td>".$brightness."</td>";
			echo "<td>".$interval." s</td>";
			echo "<td>".$duration." s</td>";
			echo "<td>".$start_percentage."%</td>";
			echo "<td>".$stop_percentage."%</td>";
			echo "<td>".$projected_life." s</td>";
			echo "<td class=\"col-response\">".$testtime."</td>";
			echo "</tr>";
		}
		?>
	</table>
	<p></p>
	</form>
<script src="js/highcharts.js"></script>
<script src="js/modules/exporting.js"></script>
<div id="container" style="min-width: 310px; height: 600px; margin: 0 auto"></div>
</body>
</html>

