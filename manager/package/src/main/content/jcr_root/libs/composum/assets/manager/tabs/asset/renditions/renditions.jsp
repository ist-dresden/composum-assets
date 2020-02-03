<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<cpn:component id="asset" type="com.composum.assets.manager.view.AssetBean">
    <div class="asset-detail asset-renditions" data-path="${asset.path}">
        <div class="config-toolbar detail-toolbar">
            <%-- sling:call script="../../folder/config/actions.jsp"/ --%>
        </div>
        <sling:include path="${asset.path}" replaceSelectors="renditions" resourceType="composum/assets/manager/image"/>
    </div>
</cpn:component>