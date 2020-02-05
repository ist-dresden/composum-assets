<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<cpn:component var="model" type="com.composum.assets.manager.image.ImageVariationBean">
    <div class="composum-assets-manager-image_variation variation panel panel-default">
        <div class="panel-heading" role="tab" id="tab-${model.domId}">
            <h2 class="composum-assets-manager-image_panel-title">
                <a data-toggle="collapse" href="#panel-${model.domId}" aria-controls="panel-${model.domId}">
                        ${model.config.name}
                    <em>${model.config.categoryString}</em>
                </a>
            </h2>
        </div>
        <div id="panel-${model.domId}" role="tabpanel" aria-labelledby="tab-${model.domId}"
             data-key="${model.config.name}"
             class="composum-assets-manager-image_variation-panel panel-collapse collapse">
            <div class="panel-body">
                <div class="panel-group" id="${model.config.path}">
                    <c:forEach items="${model.renditionConfigList}" var="rendition">
                        <sling:include resource="${rendition.resource}"
                                       resourceType="composum/assets/manager/image/rendition"/>
                    </c:forEach>
                </div>
            </div>
        </div>
    </div>
</cpn:component>
