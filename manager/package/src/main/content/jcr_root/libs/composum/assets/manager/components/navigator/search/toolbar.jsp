<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<sling:defineObjects/>
<cpn:component id="model" type="com.composum.assets.commons.widget.SearchModel" scope="request">
    <div class="composum-assets-navigator-search_toolbar btn-toolbar panel-heading" role="toolbar">
        <div class="btn-group btn-group-sm" role="group">
            <button type="button"
                    class="composum-assets-navigator_change-root fa fa-folder-o text-muted btn btn-default"
                    title="${cpn:i18n(slingRequest,'select root folder')}" data-path="${model.path}"><cpn:text
                    value="Select Root" tagName="span" class="label" i18n="true"/></button>
        </div>
        <div class="composum-assets-navigator-search_input input-group inout-group-sm">
            <span class="composum-assets-navigator-search_clear input-group-addon fa fa-times-circle"></span>
            <input type="text" class="composum-assets-navigator-search_term form-control" placeholder="search term"/>
            <span class="input-group-btn"><button
                    class="composum-assets-navigator-search_exec btn btn-default fa fa-search"
                    type="button" title="${cpn:i18n(slingRequest,'Search')}"></button></span>
        </div>
        <sling:include resourceType="composum/assets/manager/components/navigator/viewtype"/>
    </div>
</cpn:component>
