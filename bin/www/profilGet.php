<?php
    $nom="Joharisoa";
    $annee_naissance="2001";
    $age = 2021 - $annee_naissance;
?>
<!DOCTYPE html>
<html>
    <head>
        <title>Exemple PHP</title>
        <meta charset="UTF-8"> 
		<link rel="stylesheet" type="text/css" href="style.css">
	</head>
	<body>
        <h1>Exemple GET</h1>
        <p>Bonjour <?php echo $nom; ?>, vous avez  <?php echo $age; ?> ans.</p>
        <p><a href="formGet.php"><< Retour</a></p>
	</body>
</html>
