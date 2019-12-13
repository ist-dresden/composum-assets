<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<sling:defineObjects/>
<cpn:component var="model" type="com.composum.assets.commons.widget.FolderModel">
    <div class="composum-assets-navigator" data-path="${model.path}">
        <div class="composum-assets-navigator_left hidden">
        </div>
        <div class="composum-assets-navigator_right">
            <div class="composum-assets-navigator_tabs">
                <ul class="nav nav-tabs" role="tablist">
                    <li role="presentation"><a href="#tab_${model.domId}_tree" role="tab"
                                               aria-controls="tab_${model.domId}_tree"
                                               data-toggle="tab">${cpn:i18n(slingRequest,'Tree')}</a>
                    </li>
                    <li role="presentation"><a href="#tab_${model.domId}_browse" role="tab"
                                               aria-controls="tab_${model.domId}_browse"
                                               data-toggle="tab">${cpn:i18n(slingRequest,'Browse')}</a>
                    </li>
                    <li role="presentation" class="active"><a href="#tab_${model.domId}_search" role="tab"
                                                              aria-controls="tab_${model.domId}_search"
                                                              data-toggle="tab">${cpn:i18n(slingRequest,'Search')}</a>
                    </li>
                </ul>
                <div class="tab-content">
                    <div id="tab_${model.domId}_tree" class="composum-assets-navigator_tree tab-pane">
                        <sling:include resourceType="composum/assets/manager/components/navigator/tree"/>
                    </div>
                    <div id="tab_${model.domId}_browse" class="composum-assets-navigator_browse tab-pane">
                        <sling:include resourceType="composum/assets/manager/components/navigator/browse"/>
                    </div>
                    <div id="tab_${model.domId}_search" class="composum-assets-navigator_search tab-pane active">
                        <sling:include resourceType="composum/assets/manager/components/navigator/search"/>
                    </div>
                </div>
            </div>
            <div class="composum-assets-navigator_preview">
                <sling:include resourceType="composum/assets/manager/components/navigator/preview"/>
            </div>
        </div>
    </div>
</cpn:component>
