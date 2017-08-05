<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<cpn:component id="model" type="com.composum.assets.manager.view.ConfigBean">
    <div class="detail-panel config" data-path="${model.path}">
        <div class="detail-tabs action-bar btn-toolbar" role="toolbar">
            <div class="btn-group btn-group-sm" role="group">
                <button type="button" class="go-up fa fa-folder-o text-muted btn btn-default"
                        title="${cpn:i18n(slingRequest,'Go one level Up')}"><i
                        class="fa fa-chevron-up fa-stack-1x"></i><cpn:text
                        value="Go Up" tagName="span" tagClass="label" i18n="true"/></button>
            </div>
            <div class="btn-group btn-group-sm" role="group">
                <a class="small fa fa-th btn btn-default" href="#small" data-group="general"
                   title="Small Thumbnails"><span class="label">Small</span></a>
                <a class="large fa fa-th-large btn btn-default" href="#large" data-group="large"
                   title="Large Thumbnails"><span class="label">Large</span></a>
                <a class="list fa fa-th-list btn btn-default" href="#list" data-group="list" title="Assets List"><span
                        class="label">List</span></a>
            </div>
            <div class="btn-group btn-group-sm" role="group">
                <a class="edit fa fa-sliders btn btn-default" href="#edit" data-group="view"
                   title="Asset Configuration"><span class="label">Asset Configuration</span></a>
            </div>
        </div>
        <div class="detail-content">
        </div>
    </div>
</cpn:component>
