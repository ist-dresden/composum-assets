<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<sling:defineObjects/>
<div class="btn-group btn-group-sm" role="group">
    <button class="edit fa fa-edit btn btn-default"
            title="${cpn:i18n(slingRequest,'Change Configuration')}"><cpn:text
            value="Edit" tagName="span" class="label" i18n="true"/></button>
    <button class="copy fa fa-copy btn btn-default"
            title="${cpn:i18n(slingRequest,'Copy Configuration')}"><cpn:text
            value="Copy" tagName="span" class="label" i18n="true"/></button>
    <button class="paste fa fa-paste btn btn-default"
            title="${cpn:i18n(slingRequest,'Paste Configuration')}"><cpn:text
            value="Paste" tagName="span" class="label" i18n="true"/></button>
</div>
<div class="btn-group btn-group-sm" role="group">
    <button class="add fa fa-plus btn btn-default"
            title="${cpn:i18n(slingRequest,'Add Configuration')}"><cpn:text
            value="Add" tagName="span" class="label" i18n="true"/></button>
    <button class="remove fa fa-minus btn btn-default"
            title="${cpn:i18n(slingRequest,'Remove Configuration')}"><cpn:text
            value="Remove" tagName="span" class="label" i18n="true"/></button>
</div>
<div class="btn-group btn-group-sm" role="group">
    <button class="reload fa fa-refresh btn btn-default" title="${cpn:i18n(slingRequest,'Reload')}"><cpn:text
            value="Reload" tagName="span" class="label" i18n="true"/></button>
</div>
