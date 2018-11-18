<%@ taglib prefix="decorator" uri="http://www.opensymphony.com/sitemesh/decorator" %>
<!DOCTYPE html>
<html>

    <head>
        <meta charset="UTF-8">
        <title><decorator:title/></title>
    </head>

    <body>
        <header>
            <%@ include file="/WEB-INF/jsp/bo-portal/common/nav.jsp" %>
        </header>

        <hr/>

        <section>
            <div><font color="red"><c:out value="${it.err.userErrMsg}"/></font><div>
            <decorator:body />
        </section>



    </body>
</html>