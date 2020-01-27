<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<sling:defineObjects/>
<cpn:component var="model" type="com.composum.assets.commons.widget.ConfigModel" scope="request">
    <div class="composum-assets-widget-config" data-scope="${model.scope}" data-valid="${model.valid}"
         data-path="${model.path}" data-config="${model.handlePath}" data-base="${model.basePath}">
        <div class="composum-assets-widget-config_preview">
            <div class="composum-assets-widget-config_preview_image">
                <sling:include resourceType="composum/assets/commons/widget/preview/asset"
                               replaceSelectors="placeholder"/>
            </div>
            <div class="composum-assets-widget-config_preview_image-loading"><i
                    class="fa fa-spinner fa-pulse fa-5x fa-fw"></i></div>
            <div class="composum-assets-widget-config_preview_path">
                <div class="input-group composum-assets-widget-assetfield"
                     data-root="${model.contentRoot}" data-filter="image">
                    <span class="composum-assets-widget-config_preview_path-clear path-reset input-group-addon fa fa-times-circle"></span>
                    <input class="composum-assets-widget-assetfield_input path-input form-control path-input form-control"
                           type="text"/>
                    <span class="composum-assets-widget-assetfield_popup-button input-group-btn"><button
                            class="composum-assets-widget-assetfield_select select btn btn-default fa fa-folder-open-o"
                            type="button"
                            title="${cpn:i18n(slingRequest,'Select the preview image path')}"></button><button
                            class="composum-assets-widget-config_preview_refresh btn btn-default fa fa-refresh"
                            type="button"
                            title="${cpn:i18n(slingRequest,'Refresh the image view')}"></button></span>
                </div>
            </div>
        </div>
        <div class="composum-assets-widget-config_form">
            <div class="composum-assets-widget-config_form-panel">
            </div>
            <div class="composum-assets-widget-config_form-tabs">
                <div class="composum-assets-widget-config_tab composum-assets-widget-config_tab_base"
                     data-key="base">
                    <h5 class="composum-assets-widget-config_tab-title"><a href="#">${cpn:i18n(slingRequest,'Base')}</a>
                    </h5>
                </div>
                <div class="composum-assets-widget-config_tab composum-assets-widget-config_tab_node composum-assets-widget-config_tab_valid"
                     data-key="node">
                    <h5 class="composum-assets-widget-config_tab-title"><a
                            href="#">${cpn:i18n(slingRequest,'Configuration')}</a></h5>
                    <div class="composum-assets-widget-config_tab-actions btn-group btn-group-sm">
                        <button class="delete fa fa-trash btn btn-default"
                                title="${cpn:i18n(slingRequest,'Delete Configuration')}"></button>
                    </div>
                </div>
                <div class="composum-assets-widget-config_tab composum-assets-widget-config_tab_variation composum-assets-widget-config_tab_valid"
                     data-key="variation">
                    <h5 class="composum-assets-widget-config_tab-title"><a
                            href="#">${cpn:i18n(slingRequest,'Variation')}</a></h5>
                    <div class="composum-assets-widget-config_tab-actions btn-group btn-group-sm">
                        <button class="add fa fa-plus btn btn-default"
                                title="${cpn:i18n(slingRequest,'Add Variation')}"></button>
                        <button class="remove fa fa-minus btn btn-default"
                                title="${cpn:i18n(slingRequest,'Remove Variation')}"></button>
                    </div>
                    <div class="composum-assets-widget-config_select-variation">
                        <select class="composum-assets-widget-config_select"></select>
                    </div>
                </div>
                <div class="composum-assets-widget-config_tab composum-assets-widget-config_tab_rendition composum-assets-widget-config_tab_valid"
                     data-key="rendition">
                    <h5 class="composum-assets-widget-config_tab-title"><a
                            href="#">${cpn:i18n(slingRequest,'Rendition')}</a></h5>
                    <div class="composum-assets-widget-config_tab-actions btn-group btn-group-sm">
                        <button class="add fa fa-plus btn btn-default"
                                title="${cpn:i18n(slingRequest,'Add Rendition')}"></button>
                        <button class="remove fa fa-minus btn btn-default"
                                title="${cpn:i18n(slingRequest,'Remove Rendition')}"></button>
                    </div>
                    <div class="composum-assets-widget-config_select-rendition">
                        <select class="composum-assets-widget-config_select"></select>
                    </div>
                </div>
                <div class="composum-assets-widget-config_tab composum-assets-widget-config_tab_space"></div>
            </div>
        </div>
    </div>
</cpn:component>

