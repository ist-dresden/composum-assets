<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<cpn:component var="model" type="com.composum.assets.commons.widget.ConfigModel" scope="request">
    <label class="checkbox-inline">
        <input class="composum-assets-commons-widget-config_check-default" type="checkbox"
               <c:if test="${model.handle.defaultConfig}">checked</c:if>/>${cpn:i18n(slingRequest,'Default')}
    </label>
</cpn:component>

