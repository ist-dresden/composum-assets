<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<sling:defineObjects/>
<div class="btn-group btn-group-sm" role="group">
    <button class="create-folder fa fa-folder-o btn btn-default"
            title="${cpn:i18n(slingRequest,'Create a new Folder')}"><i
            class="fa fa-plus fa-stack-1x text-muted"></i><cpn:text
            value="Create Folder" tagName="span" class="label" i18n="true"/></button>
    </button>
    <button class="create-asset fa fa-plus btn btn-default" title="${cpn:i18n(slingRequest,'Create Asset')}"><cpn:text
            value="Create a new Asset" tagName="span" class="label" i18n="true"/></button>
</div>
<div class="btn-group btn-group-sm" role="group">
    <button class="delete fa fa-trash btn btn-default" title="${cpn:i18n(slingRequest,'Delete Folder')}"><cpn:text
            value="Delete" tagName="span" class="label" i18n="true"/></button>
</div>
<div class="btn-group btn-group-sm" role="group">
    <button class="meta fa fa-paperclip btn btn-default" title="${cpn:i18n(slingRequest,'Refresh Meta Data')}"><cpn:text
            value="Refresh Meta" tagName="span" class="label" i18n="true"/></button>
    <button class="reload fa fa-refresh btn btn-default" title="${cpn:i18n(slingRequest,'Reload')}"><cpn:text
            value="Reload" tagName="span" class="label" i18n="true"/></button>
</div>
