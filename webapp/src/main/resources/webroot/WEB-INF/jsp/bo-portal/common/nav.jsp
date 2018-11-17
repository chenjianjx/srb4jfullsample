<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<nav>
  <a href="/bo/portal/logout">Log out</a>
</nav>

<div>Hello, <c:out value="${staffUser.username}"/> </div>

<p><font color="red"><c:out value="${model.err.errorCode}"/></font><p>
<p><font color="red"><c:out value="${model.err.userErrMsg}"/></font><p>
