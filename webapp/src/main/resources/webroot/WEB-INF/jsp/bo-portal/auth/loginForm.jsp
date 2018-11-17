<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<h2>Please login here</h2>

<p><font color="red"><c:out value="${model.err.errorCode}"/></font><p>
<p><font color="red"><c:out value="${model.err.userErrMsg}"/></font><p>

<form action="/bo/portal/login" method="post">
	
	<ul>
		<li>Email: <input name="email" value="${model.email}"/> </li>
		<li>Password: <input name="password" type="password" value="${model.password}"/> </li>
		<li><input type="submit" value="Login"/></li>
	</ul>

</form>