<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<div class="image-wrapper preview-image">
    <div class="image-background"
         style="background-image: url(${cpn:url(slingRequest,'/libs/composum/nodes/console/browser/images/image-background.png')})">
        <img class="image" src="">
    </div>
</div>
