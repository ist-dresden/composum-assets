<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<sling:defineObjects/>
<cpn:component var="model" type="com.composum.assets.commons.widget.Thumbnail">
    <div class="composum-assets-widget-preview_wrapper composum-assets-widget-preview_type-${model.key} ${model.mimeTypeCss}">
        <div class="composum-assets-widget-preview_frame">
            <sling:include resourceType="composum/assets/commons/widget/preview/${model.key}"/>
        </div>
    </div>
</cpn:component>
