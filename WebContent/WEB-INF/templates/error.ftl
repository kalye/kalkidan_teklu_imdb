<!DOCTYPE html>
<!--Author Kalkidan Teklu -->
<html>
<head>
<meta charset="UTF-8">
<title>Registeration</title>
</head>
<body>
<!--Display error message if error exists -->
<#if error??>
 <div style="display:inline-block; float:left; color: red;">${message}</div><br>
</#if>
<form method="get">
<!--Display back button for user to go back to movie.html page -->
  <button type="submit" formaction="/kalkidan_teklu_imdb/movie.html" name="loginButton">Back</button>
</form>
</body>
</html>