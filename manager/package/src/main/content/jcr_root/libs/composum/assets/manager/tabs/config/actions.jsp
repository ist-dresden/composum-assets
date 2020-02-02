<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<cpn:component var="model" type="com.composum.assets.commons.widget.ConfigModel" scope="request">
    <div class="btn-group btn-group-sm" role="group">
        <button class="create fa fa-plus btn btn-default"
                <c:if test="${model.valid}">disabled</c:if>
                title="${cpn:i18n(slingRequest,'Create Configuration')}"><cpn:text
                value="Create" tagName="span" class="label" i18n="true"/></button>
        <button class="copy fa fa-copy btn btn-default"
                <c:if test="${!model.valid}">disabled</c:if>
                title="${cpn:i18n(slingRequest,'Copy Configuration')}"><cpn:text
                value="Copy" tagName="span" class="label" i18n="true"/></button>
        <button class="paste fa fa-paste btn btn-default"
                title="${cpn:i18n(slingRequest,'Paste Configuration')}"><cpn:text
                value="Paste" tagName="span" class="label" i18n="true"/></button>
    </div>
    <div class="btn-group btn-group-sm" role="group">
        <button class="delete fa fa-trash btn btn-default" title="${cpn:i18n(slingRequest,'Delete')}"><cpn:text
                value="Delete" tagName="span" class="label" i18n="true"/></button>
    </div>
    <div class="btn-group btn-group-sm" role="group">
        <button class="reload fa fa-refresh btn btn-default" title="${cpn:i18n(slingRequest,'Reload')}"><cpn:text
                value="Reload" tagName="span" class="label" i18n="true"/></button>
    </div>
</cpn:component>
