<!DOCTYPE html>
<html lang="it">
<head>
  <title>Smart Door</title>
  <link rel="stylesheet" type="text/css" href="assets\bootstrap-3.3.7-dist\css\bootstrap.min.css" media="screen" />
  <link rel="stylesheet" type="text/css" href="assets/css/main.css" media="screen" />
  <meta http-equiv="refresh" content="1;URL='index.php'">
</head>
<body>
  <header>
	<nav class="navbar navbar-inverse bg-inverse navbar-fixed-top">
		<div class="container">
		<div class="navbar-header">
			<a class="navbar-brand" href="index.php">GreenHouse</a>
		</div>
		</div>
	</nav>
  </header>

  <div class="container">
    <div class="row">
      <div class="col-sm-12 text-center jumbotron header">
        <h1>GreenHouse Log</h1>
      </div>
    </div>
    <div class="row">
      <div class="col-lg">    		     
        <p>
          <?php
            $open=fopen("log.txt","r");
            $testo=fread($open,filesize("log.txt"));
            fclose($open);

            echo(nl2br($testo));
  	      ?>	    
        </p>
        <p>
			<?php
            $open=fopen("umid.txt","r");
            $testo=fread($open,filesize("umid.txt"));
            fclose($open);

            echo(nl2br($testo));
  	      ?>
        </p>
      </div>
  </div>
</body>
