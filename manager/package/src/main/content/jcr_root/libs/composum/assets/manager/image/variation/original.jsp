<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<cpn:component id="model" type="com.composum.assets.manager.image.ImageVariationBean">
    <div class="composum-assets-manager-image_original variation panel panel-default accordion-item widget">
        <div class="panel-heading" role="tab" id="tab-${model.domId}">
            <h2 class="composum-assets-manager-image_panel-title">
                <a data-toggle="collapse" href="#panel-${model.domId}" aria-controls="panel-${model.domId}">
                        ${model.config.name}
                    <em>${model.config.categoryString}</em>
                </a>
            </h2>
        </div>
        <div id="panel-${model.domId}" role="tabpanel" aria-labelledby="tab-${model.domId}"
             class="panel-collapse collapse in">
            <div class="panel-body">
                <cpn:div test="${model.hasOriginal}"
                         class="composum-assets-manager-image_original-view panel-group">
                    <sling:include path="${model.originalUri}" replaceSelectors="lightbox"
                                   resourceType="composum/assets/commons/widget/preview"/>
                </cpn:div>
            </div>
        </div>
    </div>
</cpn:component>
