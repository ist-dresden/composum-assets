<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<sling:defineObjects/>
<cpn:bundle basename="composum-assets">
    <html>
    <head>
        <sling:call script="meta.jsp"/>
        <cpn:clientlib type="css" path="composum/assets/commons/widget/demo/clientlib"/>
    </head>
    <body>
    <div class="widgets-playground">
        <sling:call script="widgets.jsp"/>
    </div>
    <cpn:clientlib type="js" path="composum/assets/commons/widget/demo/clientlib"/>
    <script>
        $(document).ready(function () {
            window.widgets.setUp(document);
        });
    </script>
    </body>
    </html>
</cpn:bundle>
