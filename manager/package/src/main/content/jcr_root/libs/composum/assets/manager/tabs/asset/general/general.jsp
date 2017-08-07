<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<cpn:component id="asset" type="com.composum.assets.manager.view.AssetBean">
    <div class="asset-detail asset-original" data-path="${asset.path}">
        <div class="originals-toolbar detail-toolbar">
            <div class="btn-group btn-group-sm" role="group">
                <button class="add fa fa-plus btn btn-default" title="Add / Replace Original"><span class="label">Add / Upload</span>
                </button>
                <button class="remove fa fa-minus btn btn-default" title="Remove Original"><span
                        class="label">Remove</span></button>
            </div>
            <div class="btn-group btn-group-sm" role="group">
                <button class="delete fa fa-trash btn btn-default"
                        title="${cpn:i18n(slingRequest,'Delete Asset')}"><cpn:text
                        value="Delete" tagName="span" tagClass="label" i18n="true"/></button>
            </div>
            <div class="btn-group btn-group-sm" role="group">
                <button class="reload fa fa-refresh btn btn-default"
                        title="${cpn:i18n(slingRequest,'Reload')}"><cpn:text
                        value="Reload" tagName="span" tagClass="label" i18n="true"/></button>
            </div>
        </div>
        <sling:include path="${asset.path}" resourceType="composum/assets/manager/image/originals"/>
    </div>
</cpn:component>