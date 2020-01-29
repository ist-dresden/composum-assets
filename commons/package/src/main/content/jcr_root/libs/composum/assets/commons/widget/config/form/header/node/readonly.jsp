<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<cpn:component var="model" type="com.composum.assets.commons.widget.ConfigModel" scope="request">
    <label class="checkbox-inline">
        <input name="extension" type="checkbox" disabled
               <c:if test="${model.handle.extension}">checked</c:if>/>${cpn:i18n(slingRequest,'Extension')}
    </label>
    <div class="actions btn-group btn-group-sm" role="group">
        <button class="edit fa fa-pencil btn btn-default"
                title="${cpn:i18n(slingRequest,'Change Configuration')}"><cpn:text
                value="Edit" tagName="span" class="label" i18n="true"/></button>
    </div>
</cpn:component>
