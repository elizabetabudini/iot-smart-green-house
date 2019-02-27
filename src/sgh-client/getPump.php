<?php

	$format = "Y-m-d\TH:i:s.u\Z";
	$result = "";
	
	if (file_exists('log.txt')){
		if(!isset($_POST['last'])){
			$file_lines = file('log.txt');
			
			foreach ($file_lines as $line) {		
				$split = explode(' ',$line, 2);		
				$date = DateTime::createFromFormat($format, $split[0]);
				$result .= "<tr><td>" . $date->format('d-m-Y H:i:s') . "</td><td>" . $split[1] . "</td></tr>";
			}

			if(isset($result)){
				echo json_encode(array("a" => $result, "b" => $date->format('d-m-Y H:i:s')));
			} else {
				echo json_encode(array("a" => "0", "b" => "0"));
			}
		} else{	
			$last = DateTime::createFromFormat('d-m-Y H:i:s', $_POST['last']);
			
			$file_lines = file('log.txt');
			
			foreach ($file_lines as $line) {	
				$split = explode(' ',$line, 2);		
				$date = DateTime::createFromFormat($format, $split[0]);
				if($date->format('d-m-Y H:i:s') > $last->format('d-m-Y H:i:s')){
					$result .= "<tr><td>" . $date->format('d-m-Y H:i:s') . "</td><td>" . $split[1] . "</td></tr>";
				}
			}
			
			if(isset($result)){
				echo json_encode(array("a" => $result, "b" => $date->format('d-m-Y H:i:s')));
			} else {
				echo json_encode(array("a" => "0", "b" => "0"));
			}
		}
	} else {
			echo json_encode(array("a" => "0", "b" => "0"));
	}
?>
