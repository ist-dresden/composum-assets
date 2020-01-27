<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<sling:defineObjects/>
<html class="composum-assets-widget_page" data-context-path="${slingRequest.contextPath}">
<head>
    <cpn:clientlib type="css" category="composum.assets.widgets"/>
</head>
<body class="composum-assets-widget_page-body">
<sling:include replaceSelectors=""/>
<cpn:clientlib type="js" category="composum.assets.widgets"/>
<script>
    $(document).ready(function () {
        window.core.getWidget(document, '.composum-assets-widget-config', window.composum.assets.config.ConfigEditor);
    });
</script>
</body>
</html>
