<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<cpn:component id="configBean" type="com.composum.assets.manager.config.VariationConfigBean">
    <div class="variation config panel panel-default accordion-item widget">
        <div class="panel-heading">
            <h2 class="panel-title">
                <a data-toggle="collapse" data-parent="#${configBean.config.assetConfig.path}"
                   data-child="#${configBean.config.path}">
                        ${configBean.config.name}
                    <em>${configBean.config.categoriesString}</em>
                </a>
            </h2>
        </div>
        <div id="${configBean.config.path}" class="panel-collapse collapse in">
            <div class="panel-body">
                <div class="panel-group" id="${configBean.config.path}">
                    <c:forEach items="${configBean.renditionList}" var="rendition">
                        <sling:include resource="${rendition.resource}"/>
                    </c:forEach>
                </div>
            </div>
        </div>
    </div>
</cpn:component>
