<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<sling:defineObjects/>
    <img class="composum-assets-widget-preview_file_asset"
         style="background-image:url(${cpn:unmappedUrl(slingRequest,'/libs/composum/nodes/commons/images/image-background.png')})"
         src="${slingRequest.contextPath}${cpn:path(resource.path)}" alt="${cpn:attr(slingRequest,resource.path,0)}"/>
