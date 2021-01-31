<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<sling:defineObjects/>
<cpn:component var="model" type="com.composum.assets.commons.widget.ReferenceModel">
    <label class="composum-assets-widget-assetfield_label control-label"><span
            class="composum-assets-widget-assetfield_label-text label-text">${model.label}</span></label>
</cpn:component>
