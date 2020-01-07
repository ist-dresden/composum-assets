<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<sling:defineObjects/>
<cpn:component id="manager" type="com.composum.assets.manager.view.ManagerBean" scope="request">
    <sling:include path="${slingRequest.requestPathInfo.suffix}"
                   resourceType="composum/assets/manager/tabs/${manager.viewType}/${manager.tabType}"/>
</cpn:component>