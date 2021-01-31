<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<cpn:component id="model" type="com.composum.assets.manager.view.AssetBean">
    <div class="asset-detail asset-renditions" data-path="${model.path}"
         data-open="/libs/composum/assets/manager/image/renditions.page.html${model.path}">
        <div class="originals-toolbar detail-toolbar">
            <sling:call script="../actions.jsp"/>
        </div>
        <sling:include path="${model.path}" resourceType="composum/assets/manager/image/renditions"/>
    </div>
</cpn:component>