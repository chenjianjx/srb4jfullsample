<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<nav>
  <a href="/bo/portal/logout">Log out</a>
</nav>

<div>Hello, <c:out value="${sessionBoUsername}"/> </div>
