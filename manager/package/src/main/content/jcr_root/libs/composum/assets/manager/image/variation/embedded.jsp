<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<cpn:component id="assetBean" type="com.composum.assets.manager.image.ImageVariationBean">
    <div class="variation config panel panel-default accordion-item widget">
        <div class="panel-heading">
            <h2 class="panel-title">
                <a data-toggle="collapse" data-parent="#${assetBean.config.path}"
                   data-child="#${assetBean.config.path}">
                        ${assetBean.config.name}
                    <em>${assetBean.config.categoryString}</em>
                </a>
            </h2>
        </div>
        <div id="${assetBean.config.path}" class="panel-collapse collapse in">
            <div class="panel-body">
                <div class="panel-group" id="${assetBean.config.path}">
                    <c:forEach items="${assetBean.renditionConfigs}" var="rendition">
                        <sling:include resource="${rendition.resource}" resourceType="composum/assets/manager/image/rendition"/>
                    </c:forEach>
                </div>
            </div>
        </div>
    </div>
</cpn:component>
