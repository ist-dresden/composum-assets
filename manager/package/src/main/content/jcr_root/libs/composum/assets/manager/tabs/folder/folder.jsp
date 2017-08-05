<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<sling:defineObjects/>
<cpn:component id="folder" type="com.composum.assets.commons.widget.Folder">
    <div class="detail-panel folder" data-path="${folder.path}">
        <div class="detail-tabs action-bar btn-toolbar" role="toolbar">
            <div class="btn-group btn-group-sm" role="group">
                <button type="button" class="go-up fa fa-folder-o text-muted btn btn-default"
                        title="${cpn:i18n(slingRequest,'Go one level Up')}"><i
                        class="fa fa-chevron-up fa-stack-1x"></i><cpn:text
                        value="Go Up" tagName="span" tagClass="label" i18n="true"/></button>
            </div>
            <div class="btn-group btn-group-sm" role="group">
                <a class="small fa fa-th btn btn-default" href="#small" data-group="general"
                   title="${cpn:i18n(slingRequest,'Small Thumbnails')}"><cpn:text
                        value="small" tagName="span" tagClass="label" i18n="true"/></a>
                <a class="large fa fa-th-large btn btn-default" href="#large" data-group="large"
                   title="${cpn:i18n(slingRequest,'Large Thumbnails')}"><cpn:text
                        value="large" tagName="span" tagClass="label" i18n="true"/></a>
                <a class="list fa fa-th-list btn btn-default" href="#list" data-group="list"
                   title="${cpn:i18n(slingRequest,'Assets List')}"><cpn:text
                        value="List" tagName="span" tagClass="label" i18n="true"/></a>
            </div>
            <div class="btn-group btn-group-sm" role="group">
                <button class="config transform fa fa-sliders btn btn-default"
                        title="${cpn:i18n(slingRequest,'Create Asset Configuration')}"><i
                        class="fa fa-magic fa-stack-1x text-danger"></i><cpn:text
                        value="Configuration" tagName="span" tagClass="label" i18n="true"/></button>
            </div>
        </div>
        <div class="detail-content">
        </div>
    </div>
</cpn:component>
