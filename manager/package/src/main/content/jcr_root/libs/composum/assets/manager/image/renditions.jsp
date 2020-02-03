<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<cpn:component id="imageAssetBean" type="com.composum.assets.manager.image.ImageAssetBean" scope="request">
    <div class="composum-assets-manager-image_content">
        <h2 class="page title">Image Asset - Renditions</h2>
        <sling:include replaceSelectors="embedded"/>
    </div>
</cpn:component>