<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<cpn:component var="model" type="com.composum.assets.commons.widget.ConfigModel" scope="request">
    <label class="checkbox-inline">
        <input type="hidden" name="extension@TypeHint" value="Boolean"/>
        <input type="hidden" name="extension@Delete" value="true"/>
        <input name="extension" type="checkbox" class="composum-assets-widget-config-form_extension"
               <c:if test="${model.handle.extension}">checked</c:if>/>${cpn:i18n(slingRequest,'Extension')}
    </label>
</cpn:component>

