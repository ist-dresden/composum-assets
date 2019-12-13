<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<sling:defineObjects/>
<cpn:component id="model" type="com.composum.assets.commons.widget.AssetModel">
    <div class="asset-select-widget widget">
        <div class="asset-select-widget_path form-group">
            <label class="control-label">Asset Path</label>
            <input name="path" type="text" class="path-input form-control widget path-widget"/>
        </div>
        <ul class="asset-select-widget_tabs-nav nav nav-tabs">
            <li><a data-toggle="tab" data-name="finder" class="fa fa-search"
                   href="#${model.domId}_tab_finder" title="${cpn:i18n(slingRequest,'Find an Asset')}"></a></li>
            <li><a data-toggle="tab" data-name="browser" class="fa fa-th"
                   href="#${model.domId}_tab_browser" title="${cpn:i18n(slingRequest,'Asset Browser')}"></a></li>
            <li><a data-toggle="tab" data-name="tree" class="fa fa-sitemap"
                   href="#${model.domId}_tab_tree" title="${cpn:i18n(slingRequest,'Select in Tree')}"></a></li>
        </ul>
        <div class="asset-select-widget_tabs-content">
            <div id="${model.domId}_tab_finder"
                 class="asset-select-widget_tab_finder asset-select-widget_tab tab-pane"
                 data-name="finder" data-label="${cpn:i18n(slingRequest,'Asset Finder')}">
                <sling:include resourceType="composum/assets/commons/widgets/asset/finder" replaceSelectors="lazy"/>
            </div>
            <div id="${model.domId}_tab_browser"
                 class="asset-select-widget_tab_browser asset-select-widget_tab tab-pane"
                 data-name="browser" data-label="${cpn:i18n(slingRequest,'Asset Browser')}">
                <sling:include resourceType="composum/assets/commons/widgets/asset/browser" replaceSelectors="lazy"/>
            </div>
            <div id="${model.domId}_tab_tree"
                 class="asset-select-widget_tab_tree asset-select-widget_tab tab-pane"
                 data-name="tree" data-label="${cpn:i18n(slingRequest,'Select in Tree')}">
                <div class="asset-tree-widget widget">
                    <div class="asset-tree">
                    </div>
                </div>
            </div>
        </div>
    </div>
</cpn:component>
