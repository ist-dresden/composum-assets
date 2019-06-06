<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<cpn:component id="asset" type="com.composum.assets.manager.view.AssetBean">
    <div class="detail-panel asset" data-path="${asset.path}">
        <div class="detail-tabs action-bar btn-toolbar" role="toolbar">
            <div class="btn-group btn-group-sm" role="group">
                <button type="button" class="go-up fa fa-folder-o text-muted btn btn-default"
                        title="${cpn:i18n(slingRequest,'Open containing Folder')}"><i
                        class="fa fa-chevron-up fa-stack-1x"></i><cpn:text
                        value="Folder" tagName="span" class="label" i18n="true"/></button>
            </div>
            <div class="btn-group btn-group-sm" role="group">
                <a class="general fa fa-picture-o btn btn-default" href="#general" data-group="general"
                   title="${cpn:i18n(slingRequest,'Asset Files (Originals)')}"><cpn:text
                        value="Asset" tagName="span" class="label" i18n="true"/></a>
                <a class="view fa fa-eye btn btn-default" href="#renditions" data-group="view"
                   title="${cpn:i18n(slingRequest,'Renditions View')}"><cpn:text
                        value="Renditions" tagName="span" class="label" i18n="true"/></a>
            </div>
            <div class="btn-group btn-group-sm" role="group">
                <button class="transform fa fa-file-image-o btn btn-default"
                        title="${cpn:i18n(slingRequest,'Transform into a simple Image')}"><i
                        class="fa fa-magic fa-stack-1x text-danger"></i><cpn:text
                        value="Transform" tagName="span" class="label" i18n="true"/></button>
            </div>
        </div>
        <div class="detail-content">
        </div>
    </div>
</cpn:component>
