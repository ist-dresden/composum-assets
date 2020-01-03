<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<sling:defineObjects/>
<cpn:component var="model" type="com.composum.assets.commons.widget.ReferenceModel">
    <div class="composum-assets-widget-assetfield_preview empty-value">
        <div class="composum-assets-widget-assetfield_wrapper">
            <div class="composum-assets-widget-assetfield_frame">
                <img class="composum-assets-widget-assetfield_picture"
                     style="background-image:url(${cpn:unmappedUrl(slingRequest,'/libs/composum/nodes/commons/images/image-background.png')})"/>
            </div>
        </div>
        <div class="composum-assets-widget-assetfield_data"></div>
    </div>
</cpn:component>
