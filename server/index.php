<?php
include ('conn.php');



$sql="select * from testdata";
$result=mysql_query($sql);


?>


<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Battery Benchmark</title>
<link rel="stylesheet" type="text/css" media="screen, projection" href="demo.css" />
		<script type="text/javascript" src="jquery-1.4.2.min.js"></script>
		<script type="text/javascript">
			
			$(document).ready(function() {
				
				$('input[type=checkbox]').click(function() {
					
					thisCheckbox	= $(this);
					
					if(thisCheckbox.attr('checked')) 
						thisCheckbox.attr('checked', '');
					else
						thisCheckbox.attr('checked', 'checked');
				});
				
				$('table tr').click(function() {
					
					checkBox = $(this).children('td').children('input[type=checkbox]');
					
					if(checkBox.attr('checked'))
						checkBox.attr('checked', '');
					else
						checkBox.attr('checked', 'checked');
				});
				
				$('.check-all').click(function() {
					checkBox = $('table tr').children('td').children('input[type=checkbox]');
					
					if($(this).attr('checked')) {
						this.checked=false;
						checkBox.each(function(){
							this.checked=false;
						}); 
					} else {
						this.checked=true;
						checkBox.each(function(){
							this.checked=true;
						}); 
					}
				});
				
			});
			
		</script>
</head>
<body>
	<p class="title">Battery Benchmark</p>
	<form action = "chart/showchart.php" method = "post"> 
	<table>
		<tr>
			<th><input type="checkbox" class="check-all" /></th>
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
			echo "<td><input type=\"checkbox\" name=\"id[]\" value=\"".$id."\"/></td>";
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
	<input type="submit" value="Compare" /> 
	</form>


</body>
</html>
