<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<h2>Change Password</h2>

<form action="/bo/portal/user/change-password" method="post">
	<c:if test="${it.changeReason != null}">
	    <div><c:out value="${it.changeReason}"/></div>
	</c:if>

	<ul>
	    <li>Username: <c:out value="${sessionStaffUser.username}"/> </li>
		<li>Current Password: <input name="currentPassword" type="password" value="${it.currentPassword}"/> </li>
		<li>New Password: <input name="newPassword" type="password" value="${it.newPassword}"/> </li>
		<li>Repeat New Password: <input name="newPasswordRepeat" type="password" value="${it.newPasswordRepeat}"/> </li>
	</ul>
	    <input type="submit" value="Submit"/>

</form>