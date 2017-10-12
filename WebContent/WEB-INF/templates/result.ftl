<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Registeration</title>
<style>
table {
    border-collapse: collapse;
}

table, th, td {
    border: 1px solid black;
}

</style>
</head>
<body>
<!--Display movies -->
<#if movies??>
 <h1>Display movies</h1><br>
 <table>
 <tr><td>Name<td><td>Year</td> <td>Rank</td></tr>
 <#list results as movie>
    <tr>
    <td>${movie.name}<td>
    <td>${movie.year}</td> 
    <td>
    <!--Display rank if it exists -->
    	<#if movie.rank??>
		  ${movie.rank}
		<#else>
		  
		</#if>
	</td>
    </tr>
  </#list>
  </table>
</#if>
<#if actors??>
 <h1>Display actors</h1><br>
 <table>
 <td>First Name<td><td>Last Name</td> <td>Gender</td><td>Film Count</td>
 <#list results as actor>
     <tr><td>${actor.first_name}<td><td>${actor.last_name}</td> <td>${actor.gender}</td><td>${actor.film_count}</td></tr>
  </#list>
  </table>
</#if>
<#if directors??>
 <h1>Display directors</h1><br>
 <table>
 <tr><td>First Name<td><td>Last Name</td></tr>
 <#list results as director>
     <tr><td>${director.first_name}<td><td>${director.last_name}</td></tr>
  </#list>
  </table>
</#if>
<br>
 <form method="get">
  <button type="submit" formaction="/kalkidan_teklu_imdb/movie.html" name="search">Back</button>
</form>
</body>
</html>