<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<sling:defineObjects/>
<cpn:component var="model" type="com.composum.assets.commons.widget.ConfigModel" scope="request">
    <h4 class="modal-title">
            ${cpn:i18n(slingRequest,'Configuration')} -
            ${cpn:i18n(slingRequest,'Add Variation')}
    </h4>
    <h5 class="modal-subtitle path">${cpn:path(model.currentPath)}</h5>
</cpn:component>
