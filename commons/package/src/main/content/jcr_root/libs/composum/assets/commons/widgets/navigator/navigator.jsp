<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<sling:defineObjects/>
<cpn:bundle basename="composum-assets">
    <cpn:component var="model" type="com.composum.assets.commons.widget.FolderModel">
        <div class="composum-assets-navigator" data-path="${model.path}">
            <div class="composum-assets-navigator_left">
                <ul class="nav nav-tabs" role="tablist">
                    <li role="presentation" class="composum-assets-navigator_toggle-tree close-tree-panel active"><a
                            role="tab" class="tabbed-tab toggle-label">${cpn:i18n(slingRequest,'Tree')}</a><i
                            class="toggle-handle fa fa-angle-double-right"></i>
                    </li>
                </ul>
                <div class="composum-assets-navigator_tree-panel">
                </div>
            </div>
            <div class="composum-assets-navigator_right">
                <div class="composum-assets-navigator_tabs">
                    <ul class="nav nav-tabs" role="tablist">
                        <li role="presentation" class="composum-assets-navigator_toggle-tree open-tree-panel"><i
                                class="toggle-handle fa fa-angle-double-left"></i><a
                                href="#tab_${model.domId}_tree" role="tab" data-key="tree"
                                class="tabbed-tab toggle-label"
                                aria-controls="tab_${model.domId}_tree"
                                data-toggle="tab">${cpn:i18n(slingRequest,'Tree')}</a>
                        </li>
                        <li role="presentation"><a href="#tab_${model.domId}_browse" role="tab" data-key="browse"
                                                   class="tabbed-tab" aria-controls="tab_${model.domId}_browse"
                                                   data-toggle="tab">${cpn:i18n(slingRequest,'Browse')}</a>
                        </li>
                        <li role="presentation"><a href="#tab_${model.domId}_search" role="tab" data-key="search"
                                                   class="tabbed-tab" aria-controls="tab_${model.domId}_search"
                                                   data-toggle="tab">${cpn:i18n(slingRequest,'Search')}</a>
                        </li>
                    </ul>
                    <div class="tab-content">
                        <div id="tab_${model.domId}_tree" class="composum-assets-navigator_tree tab-pane">
                            <sling:include resourceType="composum/assets/commons/widgets/navigator/tree"/>
                        </div>
                        <div id="tab_${model.domId}_browse" class="composum-assets-navigator_browse tab-pane">
                            <sling:include resourceType="composum/assets/commons/widgets/navigator/browse"/>
                        </div>
                        <div id="tab_${model.domId}_search" class="composum-assets-navigator_search tab-pane">
                            <sling:include resourceType="composum/assets/commons/widgets/navigator/search"/>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </cpn:component>
</cpn:bundle>
