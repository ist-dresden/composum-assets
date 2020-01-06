<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<sling:defineObjects/>
<video class="composum-assets-widget-preview_file_video"
       src="${slingRequest.contextPath}${cpn:path(resource.path)}" controls="controls"></video>
