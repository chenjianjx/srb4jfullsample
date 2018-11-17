<!DOCTYPE html>
<html>

    <head>
        <meta charset="UTF-8">
        <title><sitemesh:write property='title'/></title>
    </head>

    <body>
        <header>
            <%@include file="common/nav.jsp"%>
        </header>

        <hr/>

        <section>
            <sitemesh:write property='body'/>
        </section>

        <hr/>

    </body>
</html>