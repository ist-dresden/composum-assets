<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<cpn:component var="model" type="com.composum.assets.commons.widget.ConfigModel" scope="request">
    <div class="row" style="align-items: flex-start;">
        <div class="col col-xs-6">
            <div class="form-group">
                <cpn:text tagName="label" tagClass="control-label" value="Variation" i18n="true"/>
                <select name="variation"
                        class="composum-assets-dialogs-config_variation widget select-widget form-control">
                    <c:forEach items="${model.variations}" var="variation">
                        <option value="${variation.name}"
                                <c:if test="${variation.name==model.variation.name}">selected</c:if>>${variation.name}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="form-group">
                <cpn:text tagName="label" tagClass="control-label" value="Name" i18n="true"/>
                <input name="name" type="text" data-pattern="^[a-zA-Z_][a-zA-Z_0-9]*$"
                       class="composum-assets-dialogs-config_name widget text-field-widget form-control"/>
            </div>
            <div class="form-group">
                <cpn:text tagName="label" tagClass="control-label" value="Title" i18n="true"/>
                <input name="jcr:title" type="text"
                       class="composum-assets-dialogs-config_title widget text-field-widget form-control"/>
            </div>
        </div>
        <div class="col col-xs-6">
            <div class="form-group">
                <cpn:text tagName="label" tagClass="control-label" value="available" i18n="true"/>
                <ul class="list-group">
                    <c:forEach items="${model.renditions}" var="rendition">
                        <li class="list-group-item">
                            <cpn:div test="${rendition.defaultConfig}" tagName="span"
                                     class="badge">default</cpn:div>
                            <cpn:div test="${rendition.originalConfig}" tagName="span"
                                     class="badge">original</cpn:div>
                            <cpn:text tagName="span" class="name" value="${rendition.name}"/>
                        </li>
                    </c:forEach>
                </ul>
            </div>
        </div>
    </div>
    <sling:call script="description.jsp"/>
</cpn:component>
