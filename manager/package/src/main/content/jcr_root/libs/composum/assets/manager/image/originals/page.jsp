<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<html data-context-path="${slingRequest.contextPath}">
<head>
    <cpn:clientlib type="css" category="composum.assets.manager.page"/>
</head>
<body class="composum-assets-manager-image_page originals assets page view assets-preview-mode-dark">
<h2 class="composum-assets-manager-image_page-title">Image Asset - Originals -
    <em>${slingRequest.requestPathInfo.suffix}</em></h2>
<sling:include path="${slingRequest.requestPathInfo.suffix}"
               resourceType="composum/assets/manager/image/originals" replaceSelectors=""/>
<cpn:clientlib type="js" category="composum.assets.manager.page"/>
<script>core.getView('body', composum.assets.manager.page.AssetOriginalsPageView)</script>
</body>
</html>