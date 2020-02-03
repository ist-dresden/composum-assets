<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<cpn:component id="variation" type="com.composum.assets.manager.image.ImageVariationBean">
    <div class="variation config panel panel-default accordion-item widget">
        <div class="panel-heading">
            <h2 class="panel-title">
                <a data-toggle="collapse" data-parent="#${variation.path}"
                   data-child="#${variation.path}">${variation.name}
                    <em>${variation.config.categoryString}</em>
                </a>
            </h2>
        </div>
        <div id="${variation.path}" class="panel-collapse collapse ${variation.hasOriginal?'in':''}">
            <div class="panel-body">
                <cpn:div test="${variation.hasOriginal}"
                         class="panel-group image-original" id="${variation.path}">
                    <sling:include path="${variation.originalUri}" replaceSelectors="lightbox"
                                   resourceType="composum/assets/commons/widget/preview"/>
                </cpn:div>
            </div>
        </div>
    </div>
</cpn:component>
