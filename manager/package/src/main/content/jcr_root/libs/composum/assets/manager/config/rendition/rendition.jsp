<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<cpn:component id="configBean" type="com.composum.assets.manager.config.RenditionConfigBean">
<html data-context-path="${slingRequest.contextPath}">
    <head>
        <cpn:clientlib type="css" path="composum/assets/manager/clientlibs/page"/>
    </head>
    <body id="renditionConfig" class="rendition assets config page view">
    <div id="ui">
         <div id="content-wrapper">
            <h2 class="page title">Assets Config Rendition</h2>
            <sling:include replaceSelectors="embedded.rendition"/>
        </div>
    </div>
    <cpn:clientlib type="js" path="composum/assets/manager/clientlibs/page"/>
    </body>
</html>
</cpn:component>