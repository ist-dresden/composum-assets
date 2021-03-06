<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<cpn:component var="model" type="com.composum.assets.commons.widget.ConfigModel" scope="request">
    <label class="checkbox-inline">
        <input class="composum-assets-widget-config-form_default" type="checkbox" disabled
               <c:if test="${model.handle.defaultConfig}">checked</c:if>/>${cpn:i18n(slingRequest,'Default')}
    </label>
    <div class="composum-assets-widget-config-form_actions btn-group btn-group-sm" role="group">
        <button class="composum-assets-widget-config-form_action-edit fa fa-pencil btn btn-default"
                title="${cpn:i18n(slingRequest,'Change Configuration')}" type="button"><cpn:text
                value="Edit" tagName="span" class="label" i18n="true"/></button>
    </div>
</cpn:component>

