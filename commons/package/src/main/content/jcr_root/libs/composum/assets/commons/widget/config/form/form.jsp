<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<cpn:component var="model" type="com.composum.assets.commons.widget.ConfigModel" scope="request">
    <div class="composum-assets-widget-config-form">
        <form class="composum-assets-widget-config-form_form widget-form"
              data-example="${model.handle.inherited.example_image_path}">
            <div class="composum-assets-widget-config-form_content">
                <ul class="composum-assets-widget-config-form_tabs nav nav-tabs" role="tablist">
                    <li role="presentation"><a href="#${model.domId}_general" aria-controls="${model.domId}_general"
                                               role="tab" data-toggle="tab"
                                               data-key="general">${cpn:i18n(slingRequest,'General')}</a></li>
                    <li role="presentation"><a href="#${model.domId}_transform" aria-controls="${model.domId}_transform"
                                               role="tab" data-toggle="tab"
                                               data-key="transform">${cpn:i18n(slingRequest,'Transformation')}</a></li>
                    <li role="presentation"><a href="#${model.domId}_watermark" aria-controls="${model.domId}_watermark"
                                               role="tab" data-toggle="tab"
                                               data-key="watermark">${cpn:i18n(slingRequest,'Watermark')}</a>
                    </li>
                </ul>
                <div class="composum-assets-widget-config-form_panels tab-content">
                    <div id="${model.domId}_general" class="composum-assets-widget-config-form_tab-panel tab-pane"
                         role="tabpanel">
                        <sling:include resourceType="composum/assets/commons/widget/config/form/general"/>
                    </div>
                    <div id="${model.domId}_transform" class="composum-assets-widget-config-form_tab-panel tab-pane"
                         role="tabpanel">
                        <sling:include resourceType="composum/assets/commons/widget/config/form/transform"/>
                    </div>
                    <div id="${model.domId}_watermark" class="composum-assets-widget-config-form_tab-panel tab-pane"
                         role="tabpanel">
                        <sling:include resourceType="composum/assets/commons/widget/config/form/watermark"/>
                    </div>
                </div>
            </div>
        </form>
    </div>
</cpn:component>

