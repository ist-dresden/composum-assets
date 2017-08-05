<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<sling:defineObjects/>
<cpn:component id="model" type="com.composum.assets.commons.model.AdaptiveImage">
    <div class="composum-assets-commons-components-image-adaptive adaptive picture">
        <figure>
            <div class="composum-assets-commons-components-image-adaptive_background image-background">
                <div class="composum-assets-commons-components-image-adaptive_wrapper image-wrapper">
                    ${model.imageTag}
                </div>
            </div>
        </figure>
    </div>
</cpn:component>
