<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<h2>Change Password</h2>

<form action="/bo/portal/user/change-password" method="post">
	
	<ul>
		<li>Current Password: <input name="password" type="password" value="${model.oldPassword}"/> </li>
		<li>New Password: <input name="password" type="password" value="${model.newPassword}"/> </li>
		<li>Repeat New Password: <input name="password" type="password" value="${model.newPasswordRepeat}"/> </li>

		<li><input type="submit" value="Submit"/></li>
	</ul>

</form>