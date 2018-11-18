<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:if test="${sessionStaffUser != null}">

    <nav>
        <ul>
            <li> <a href="/bo/portal/frontusers">Front Users</a> </li>
            <li> <a href="/bo/portal/staffusers/admin/change-password">Change My Password</a> </li>
            <li> <a href="/bo/portal/logout">Log out</a> </li>
        </ul>
    </nav>

    <div>Hello, <c:out value="${sessionStaffUser.username}"/> </div>

    <hr/>
</c:if>