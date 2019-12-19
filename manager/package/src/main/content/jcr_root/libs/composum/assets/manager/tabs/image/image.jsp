<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<cpn:component id="model" type="com.composum.assets.commons.handle.SimpleImage">
    <div class="detail-panel image" data-path="${model.path}">
        <div class="detail-tabs action-bar btn-toolbar" role="toolbar">
            <div class="btn-group btn-group-sm" role="group">
                <button type="button" class="go-up fa fa-folder-o text-muted btn btn-default"
                        title="${cpn:i18n(slingRequest,'Open containing Folder')}"><i
                        class="fa fa-chevron-up fa-stack-1x"></i><cpn:text
                        value="Folder" tagName="span" class="label" i18n="true"/></button>
            </div>
            <div class="btn-group btn-group-sm" role="group">
                <a class="general fa fa-file-image-o btn btn-default" href="#general" data-group="general"
                   title="${cpn:i18n(slingRequest,'Image')}"><cpn:text
                        value="Image" tagName="span" class="label" i18n="true"/></a>
            </div>
            <div class="btn-group btn-group-sm" role="group">
                <button class="transform fa fa-image btn btn-default"
                        title="${cpn:i18n(slingRequest,'Transform into an Image Asset')}"><i
                        class="fa fa-magic fa-stack-1x text-danger"></i><cpn:text
                        value="Transform" tagName="span" class="label" i18n="true"/></button>
            </div>
        </div>
        <div class="detail-content">
        </div>
    </div>
</cpn:component>
