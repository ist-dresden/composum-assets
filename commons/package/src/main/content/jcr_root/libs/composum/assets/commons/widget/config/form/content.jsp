<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<cpn:component var="model" type="com.composum.assets.commons.widget.ConfigModel" scope="request">
    <div class="composum-assets-widget-config-form_content">
        <ul class="composum-commons-form-tab-nav nav nav-tabs" role="tablist">
        </ul>
        <div class="composum-assets-widget-config-form_header">
            <sling:include resourceType="composum/assets/commons/widget/config/form/header/${model.scope}"/>
        </div>
        <div class="composum-assets-widget-config-form_panels tab-content composum-commons-form-tabbed">
            <div id="${model.domId}_general" data-key="general"
                 data-label="${cpn:i18n(slingRequest,'General')}"
                 class="composum-commons-form-tab-panel tab-pane" role="tabpanel">
                <sling:include resourceType="composum/assets/commons/widget/config/form/general"/>
            </div>
            <div id="${model.domId}_transform" data-key="transform"
                 data-label="${cpn:i18n(slingRequest,'Transformation')}"
                 class="composum-commons-form-tab-panel tab-pane" role="tabpanel">
                <sling:include resourceType="composum/assets/commons/widget/config/form/transform"/>
            </div>
            <div id="${model.domId}_watermark" data-key="watermark"
                 data-label="${cpn:i18n(slingRequest,'Watermark')}"
                 class="composum-commons-form-tab-panel tab-pane" role="tabpanel">
                <sling:include resourceType="composum/assets/commons/widget/config/form/watermark"/>
            </div>
        </div>
    </div>
</cpn:component>

