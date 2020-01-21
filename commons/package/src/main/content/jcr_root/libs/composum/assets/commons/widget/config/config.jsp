<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<cpn:clientlib type="css" category="composum.assets.configuration"/>
<cpn:component var="model" type="com.composum.assets.commons.widget.ConfigModel">
    <div class="composum-assets-widget-config" data-path="${model.path}">
        <div class="composum-assets-widget-config_preview">
            <sling:include resourceType="composum/assets/commons/widget/preview/asset" replaceSelectors="placeholder"/>
        </div>
        <div class="composum-assets-widget-config_form">
            <div class="composum-assets-widget-config_form-panel">
            </div>
            <div class="composum-assets-widget-config_form-tabs">
                <div class="composum-assets-widget-config_tab composum-assets-widget-config_tab_base disabled"
                     data-key="base">
                    <h5 class="composum-assets-widget-config_tab-title"><a href="#">${cpn:i18n(slingRequest,'Base')}</a>
                    </h5>
                </div>
                <div class="composum-assets-widget-config_tab composum-assets-widget-config_tab_node"
                     data-key="node">
                    <h5 class="composum-assets-widget-config_tab-title"><a
                            href="#">${cpn:i18n(slingRequest,'Configuration')}</a></h5>
                    <div class="composum-assets-widget-config_tab-actions btn-group btn-group-sm">
                        <button class="delete fa fa-trash btn btn-default"
                                title="${cpn:i18n(slingRequest,'Delete Configuration')}"></button>
                    </div>
                </div>
                <div class="composum-assets-widget-config_tab composum-assets-widget-config_tab_variation"
                     data-key="variation">
                    <h5 class="composum-assets-widget-config_tab-title"><a
                            href="#">${cpn:i18n(slingRequest,'Variation')}</a></h5>
                    <div class="composum-assets-widget-config_tab-actions btn-group btn-group-sm">
                        <button class="add fa fa-plus btn btn-default"
                                title="${cpn:i18n(slingRequest,'Add Variation')}"></button>
                        <button class="remove fa fa-minus btn btn-default"
                                title="${cpn:i18n(slingRequest,'Remove Variation')}"></button>
                    </div>
                    <select class="composum-assets-widget-config_select composum-assets-widget-config_select-variation"
                            data-default="${model.variation.name}">
                        <c:forEach items="${model.variations}" var="item">
                            <option value="${item.name}">${item.name}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="composum-assets-widget-config_tab composum-assets-widget-config_tab_rendition"
                     data-key="redition">
                    <h5 class="composum-assets-widget-config_tab-title"><a
                            href="#">${cpn:i18n(slingRequest,'Rendition')}</a></h5>
                    <div class="composum-assets-widget-config_tab-actions btn-group btn-group-sm">
                        <button class="add fa fa-plus btn btn-default"
                                title="${cpn:i18n(slingRequest,'Add Rendition')}"></button>
                        <button class="remove fa fa-minus btn btn-default"
                                title="${cpn:i18n(slingRequest,'Remove Rendition')}"></button>
                    </div>
                    <select class="composum-assets-widget-config_select composum-assets-widget-config_select-rendition"
                            data-default="${model.rendition.name}">
                        <c:forEach items="${model.renditions}" var="item">
                            <option value="${item.name}">${item.name}</option>
                        </c:forEach>
                    </select>
                </div>
            </div>
        </div>
    </div>
    <cpn:clientlib type="js" category="composum.assets.configuration"/>
    <script>
        $(document).ready(function () {
            window.core.getWidget(document, '.composum-assets-widget-config', window.composum.assets.config.ConfigEditor);
        });
    </script>
</cpn:component>

