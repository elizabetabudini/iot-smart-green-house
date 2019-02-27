<!DOCTYPE html>
<html lang="it">
<head>
  <title>GreenHouse</title>
  <link rel="stylesheet" type="text/css" href="assets\bootstrap-3.3.7-dist\css\bootstrap.min.css" media="screen" />
  <link rel="stylesheet" type="text/css" href="assets/css/main.css" media="screen" />
  <script src="https://code.jquery.com/jquery-3.3.1.min.js" type="text/javascript"></script>
  <!-- Bootstrap CSS -->
  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css" integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm" crossorigin="anonymous">

	<!-- Notification script -->
    <script type="text/javascript">
    	$(document).ready(function(){
			var lastpump;
			var lastum;
			
		   $.ajax({
              type:'POST',
              url:'getPump.php',
              dataType: 'json',
              data: {
				last : lastpump
			  }
            })
              .done(function(response){
                if(response.b != "0"){
					lastpump = response.b;
					$('#Pump').append($(response.a));
                } else {
                	alert("Errore");
                }
              });
              
            $.ajax({
              type:'POST',
              url:'getUm.php',
              dataType: 'json',
              data: {
				last : lastum
			  }
		  })
            .done(function(response){
				if(response.b != "0"){
					lastum = response.b;
					$('#Um').append($(response.a));
                } else {
                	alert("Errore");
                }
            });
		   
		   function refresh(){
				 $.ajax({
				  type:'POST',
				  url:'getPump.php',
				  dataType: 'json',
				  data: {
					last : lastpump
				  }
				})
				.done(function(response){
					if(response.b != "0"){			
						lastpump = response.b;
						$('#Pump').append($(response.a));
					} else {
						alert("Errore");
					}
					});
				
				$.ajax({
				  type:'POST',
				  url:'getUm.php',
				  dataType: 'json',         
				  data: {
					last : lastum
				  }
				})
				.done(function(response){
					if(response.b != "0"){
						lastum = response.b;
						$('#Um').append($(response.a));
					} else {
						alert("Errore");
					}
				});
			}  
            window.setInterval(refresh, 5000);
		});
	</script>
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
        <table id="Pump" class="table table-striped">
			<tr>
				<th scope="col">Data</th>
				<th scope="col">Azione</th>
			</tr>
        </table>
      </div>
      <div class="col-lg">    		     
        <table id="Um" class="table table-striped">
			<tr>
				<th scope="col">Data</th>
				<th scope="col">Valore %</th>
			</tr>
        </table>
      </div>
  </div>
 
</body>
</html>
