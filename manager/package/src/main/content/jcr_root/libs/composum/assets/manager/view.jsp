<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<cpn:component id="manager" type="com.composum.assets.manager.view.ManagerBean" scope="request">
    <div class="detail-view">
        <sling:include resourceType="composum/assets/manager/components/breadcrumbs"/>
        <sling:include path="${slingRequest.requestPathInfo.suffix}"
                       resourceType="composum/assets/manager/tabs/${manager.viewType}"/>
    </div>
</cpn:component>