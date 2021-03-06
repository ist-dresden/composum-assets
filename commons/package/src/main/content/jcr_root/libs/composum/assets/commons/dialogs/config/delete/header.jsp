<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<cpn:component var="model" type="com.composum.assets.commons.widget.ConfigModel" scope="request">
    <h4 class="modal-title">
            ${cpn:i18n(slingRequest,'Delete Configuration')}${model.configLabel}
    </h4>
    <h5 class="modal-subtitle path">${cpn:path(model.currentPath)}</h5>
</cpn:component>
