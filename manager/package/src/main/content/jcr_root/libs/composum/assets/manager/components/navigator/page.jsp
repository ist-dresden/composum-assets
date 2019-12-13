<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<sling:defineObjects/>
<cpn:bundle basename="composum-assets">
    <html class="composum-assets-navigator_page" data-context-path="${slingRequest.contextPath}">
    <head>
        <cpn:clientlib type="css" category="composum.assets.navigator"/>
    </head>
    <body class="composum-assets-navigator_page-body">
    <sling:include replaceSelectors=""/>
    <cpn:clientlib type="js" category="composum.assets.navigator"/>
    <script>
        $(document).ready(function () {
            window.widgets.setUp(document);
        });
    </script>
    </body>
    </html>
</cpn:bundle>
