<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<sling:defineObjects/>
<cpn:component id="configBean" type="com.composum.assets.manager.view.ConfigBean">
    <div class="config-detail config-edit" data-path="${configBean.path}">
        <div class="config-toolbar detail-toolbar">
            <sling:call script="actions.jsp"/>
            <%--<sling:call script="../../folder/actions.jsp"/>--%>
        </div>
        <h2 class="page title">Assets Config</h2>
        <sling:include resource="${configBean.resource}" replaceSelectors="edit.config"/>
    </div>
</cpn:component>