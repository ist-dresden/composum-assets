<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<cpn:component id="model" type="com.composum.assets.manager.image.ImageRenditionBean">
    <div class="composum-assets-rendition_view">
        <div class="overlay">
            <div class="overlay-background image view">
                <div class="omposum-assets-widget-preview_wrapper">
                    <div class="composum-assets-widget-preview_frame">
                        <img class="composum-assets-widget-preview_rendition" data-src="${model.imageUrl}">
                    </div>
                </div>
            </div>
            <div class="overlay-foreground">
                <sling:include resourceType="composum/assets/commons/widget/config/properties"/>
            </div>
        </div>
    </div>
</cpn:component>
