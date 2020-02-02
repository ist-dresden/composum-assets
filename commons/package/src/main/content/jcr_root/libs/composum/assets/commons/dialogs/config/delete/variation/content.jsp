<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<cpn:component var="model" type="com.composum.assets.commons.widget.ConfigModel" scope="request">
    <div class="composum-assets-dialogs-config_content">
        <div class="row" style="align-items: flex-start;">
            <div class="col col-xs-6">
                <div class="form-group">
                    <cpn:text tagName="label" tagClass="control-label" value="Delete" i18n="true"/>
                    <select name="variation"
                            class="composum-assets-dialogs-config_name widget select-widget form-control">
                        <c:forEach items="${model.variations}" var="variation">
                            <option value="${variation.name}"
                                    <c:if test="${variation.name==model.variation.name}">selected</c:if>>${variation.name}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-group">
                    <div class="hint hint-warning">
                            ${cpn:i18n(slingRequest,'The variation will be removed permanently.')}
                            ${cpn:i18n(slingRequest,'All configuration data will be lost.')}</div>
                </div>
            </div>
            <div class="col col-xs-6">
                <div class="form-group">
                    <cpn:text tagName="label" tagClass="control-label" value="available" i18n="true"/>
                    <ul class="list-group">
                        <c:forEach items="${model.variations}" var="variation">
                            <li class="list-group-item">
                                <cpn:div test="${variation.defaultConfig}" tagName="span"
                                         class="badge">default</cpn:div>
                                <cpn:div test="${variation.originalConfig}" tagName="span"
                                         class="badge">original</cpn:div>
                                <cpn:text tagName="span" class="name" value="${variation.name}"/>
                            </li>
                        </c:forEach>
                    </ul>
                </div>
            </div>
        </div>
    </div>
</cpn:component>
