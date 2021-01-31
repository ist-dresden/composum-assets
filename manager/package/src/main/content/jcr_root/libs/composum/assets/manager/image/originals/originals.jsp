<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/><%-- the 'imageAssetBean' is used as request attribute... --%>
<cpn:component var="imageAssetBean" type="com.composum.assets.manager.image.ImageAssetBean" scope="request">
    <div class="composum-assets-manager-image_content">
        <div class="composum-assets-manager-image_originals panel-group" id="${imageAssetBean.domId}">
            <c:forEach items="${imageAssetBean.variationList}" var="variation">
                <sling:include resource="${variation.resource}" replaceSelectors="original"
                               resourceType="composum/assets/manager/image/variation"/>
            </c:forEach>
        </div>
    </div>
</cpn:component>
