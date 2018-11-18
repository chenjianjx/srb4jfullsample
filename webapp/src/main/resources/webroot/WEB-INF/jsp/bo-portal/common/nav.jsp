<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>


<nav>
    <ul>
        <li> <a href="/bo/portal/user/change-password">Change Password</a> </li>
        <li> <a href="/bo/portal/logout">Log out</a> </li>
    </ul>
</nav>

<c:if test="${sessionStaffUser != null}">
    <div>Hello, <c:out value="${sessionStaffUser.username}"/> </div>
</c:if>