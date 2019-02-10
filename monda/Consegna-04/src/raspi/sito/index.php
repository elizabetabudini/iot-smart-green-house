<!DOCTYPE html>
<html lang="it">
<head>
  <title>Smart Door</title>
  <link rel="stylesheet" type="text/css" href="assets\bootstrap-3.3.7-dist\css\bootstrap.min.css" media="screen" />
  <link rel="stylesheet" type="text/css" href="assets/css/main.css" media="screen" />
  <meta http-equiv="refresh" content="1;URL='index.php'">
</head>
<body>
  <?php require "assets/filepart/header.php"; ?>
  <div class="container">
    <div class="row">
      <div class="col-sm-12 text-center jumbotron header">
        <h1>Benvenuto nella tua</h1>
        <h1>Smart Door Log</h1>
      </div>
    </div>
    <div class="row">
      <div class="col-sm-12">
        <p>
          Temperatura:
          <?php
            $open=fopen("temp.txt","r");
            $testo=fread($open,filesize("temp.txt"));
            fclose($open);

            echo(nl2br($testo));
  	      ?>
        </p>
        <p>
          Intensità:
          <?php
            $open=fopen("int.txt","r");
            $testo=fread($open,filesize("int.txt"));
            fclose($open);

            echo(nl2br($testo));
  	      ?>
	    %
        </p>
      </div>
    <div class="row">
      <div class="col-sm-12">
	<p>
        <?php
          $open=fopen("log.txt","r");
          $testo=fread($open,filesize("log.txt"));
          fclose($open);

          echo(nl2br($testo));
	      ?>
	</p>
      </div>
    </div>
  </div>
</body>
