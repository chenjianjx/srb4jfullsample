<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<h2>Please login here</h2>

<form action="/bo/portal/login" method="post">
	
	<ul>
		<li>Username: <input name="username" value="${model.username}"/> </li>
		<li>Password: <input name="password" type="password" value="${model.password}"/> </li>
		<li><input type="submit" value="Login"/></li>
	</ul>

</form>