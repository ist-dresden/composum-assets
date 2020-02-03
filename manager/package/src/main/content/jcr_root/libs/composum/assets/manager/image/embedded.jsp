<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<cpn:component id="imageAssetBean" type="com.composum.assets.manager.image.ImageAssetBean" scope="request">
    <div class="composum-assets-manager-image_variations panel-group" id="${imageAssetBean.config.assetConfig.path}">
        <c:forEach items="${imageAssetBean.variationConfigList}" var="variation">
            <sling:include resource="${variation.resource}" resourceType="composum/assets/manager/image/variation"/>
        </c:forEach>
    </div>
</cpn:component>
