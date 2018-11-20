<%@ tag body-content="empty" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>



<%@ attribute name="it"    rtexprvalue="true"  required="true" type="java.lang.String"  description="the root MVC model" %>
<%@ attribute name="field"    rtexprvalue="true"  required="true" type="java.lang.String"  description="field name" %>

<c:out value="${it}"/>