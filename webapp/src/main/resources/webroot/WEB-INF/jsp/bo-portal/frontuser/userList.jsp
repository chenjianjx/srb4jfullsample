<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<h2>Front Users</h2>

<c:choose>
    <c:when test="${it.users != null && not empty it.users}">
        <table>
            <tr>
                <th>Id</th>
                <th>Principal</th>
                <th>Source</th>
                <th>Email</th>
                <th>Email Verified</th>
            </tr>
            <c:forEach items="${it.users}" var="user">
                <tr>
                    <td>
                        <c:out value="${user.id}"/>
                    </td>
                    <td>
                        <c:out value="${user.principal}"/>
                    </td>
                    <td>
                        <c:out value="${user.source}"/>
                    </td>
                    <td>
                        <c:out value="${user.email}"/>
                    </td>
                    <td>
                        <c:out value="${user.emailVerified? 'Y': 'N'}"/>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </c:when>

    <c:otherwise>
        <div>There are no front users yet</div>
    </c:otherwise>

</c:choose>
