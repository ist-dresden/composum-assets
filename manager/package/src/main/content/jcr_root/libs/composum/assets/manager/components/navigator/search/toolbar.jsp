<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<sling:defineObjects/>
<cpn:component id="model" type="com.composum.assets.commons.widget.SearchModel" scope="request">
    <div class="composum-assets-navigator-search_toolbar panel-heading" role="toolbar">
        <div class="composum-assets-navigator-search_toolbar-top btn-toolbar">
            <div class="btn-group btn-group-sm composum-assets-navigator-search_toggle-grp" role="group">
                <button type="button"
                        class="composum-assets-navigator-search_open-root fa fa-folder-o text-muted btn btn-default"
                        title="${cpn:i18n(slingRequest,'select root folder')}" data-path="${model.path}"><cpn:text
                        value="Select Root" tagName="span" class="label" i18n="true"/></button>
            </div>
            <div class="composum-assets-navigator-search_input input-group inout-group-sm">
                <span class="composum-assets-navigator-search_clear input-group-addon fa fa-times-circle"></span>
                <input type="text" class="composum-assets-navigator-search_term form-control" placeholder="search term"
                       value="${model.searchTerm}"/>
                <span class="input-group-btn"><button
                        class="composum-assets-navigator-search_exec btn btn-default fa fa-search"
                        type="button" title="${cpn:i18n(slingRequest,'Search')}"></button></span>
            </div>
            <sling:include resourceType="composum/assets/manager/components/navigator/viewtype"/>
        </div>
        <div class="composum-assets-navigator-search_toolbar-bottom btn-toolbar">
            <span class="toolbar-label">${cpn:i18n(slingRequest,'Search Root')}</span>
            <div class="composum-assets-navigator-search_root input-group widget path-widget">
                <input name="path" class="form-control" type="text" value="${model.searchRoot}"/>
                <span class="input-group-btn"><button
                        class="select btn btn-default" type="button"
                        title="${cpn:i18n(slingRequest,'Select Repository Path')}">...</button></span>
            </div>
            <a class="composum-assets-navigator-search_folder-as-root" href="#"><i
                    class="icon fa fa-caret-left"></i>${cpn:i18n(slingRequest,'use current folder as root')}</a>
        </div>
    </div>
</cpn:component>
