<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<sling:defineObjects/>
<cpn:component var="model" type="com.composum.assets.commons.widget.FolderModel" scope="request">
    <div class="composum-assets-manager-browse_toolbar btn-toolbar" role="toolbar">
        <div class="btn-group btn-group-sm" role="group">
            <button type="button" class="composum-assets-widget-navigator_go-up fa fa-folder-o text-muted btn btn-default"
                    title="${cpn:i18n(slingRequest,'Go one level Up')}" data-path="${model.path}"><i
                    class="fa fa-chevron-up fa-stack-1x"></i><cpn:text
                    value="Go Up" tagName="span" class="label" i18n="true"/></button>
        </div>
        <sling:include resourceType="composum/assets/commons/widget/navigator/viewtype"/>
    </div>
</cpn:component>
