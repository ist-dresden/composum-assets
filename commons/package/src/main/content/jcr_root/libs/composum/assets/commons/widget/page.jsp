<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<sling:defineObjects/>
<html class="composum-assets-widget_page" data-context-path="${slingRequest.contextPath}">
<head>
    <cpn:clientlib type="css" category="composum.assets.widgets"/>
</head>
<body class="composum-assets-widget_page-body">
<%--
 the example to use the assets widgets (a page with the widegts examples embedded)
  - ensure that the CSS und JS code is avaialable (use the widgets clientlib)
  - include the designated widget component - this is simply one include call (inside of a 'form')
  - finally set up all loaded widgets using the general widget registry
--%>
<form>
    <%-- the asset reference input field 'parameterized' using selector and suffix --%>
    <sling:include resourceType="composum/assets/commons/widget/assetfield"
                   replaceSelectors="Asset_Field_Label._.required"
                   replaceSuffix="image/imageRef"/>
    <%-- an embedded asset navigator widget example --%>
    <sling:include resourceType="composum/assets/commons/widget/navigator"/>
</form>
<cpn:clientlib type="js" category="composum.assets.widgets"/>
<script>
    $(document).ready(function () {
        window.widgets.setUp(document);
    });
</script>
</body>
</html>
