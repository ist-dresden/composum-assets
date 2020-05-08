<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<cpn:component var="model" type="com.composum.assets.commons.widget.ConfigModel" scope="request">
    <div class="composum-assets-dialog_content">
        <div class="row" style="align-items: flex-start;">
            <div class="col col-xs-6">
                <div class="form-group">
                    <cpn:text tagName="label" tagClass="control-label" value="Name" i18n="true"/>
                    <input name="name" type="text" data-pattern="^[a-zA-Z_][a-zA-Z_0-9]*$"
                           class="composum-assets-dialog_name widget text-field-widget form-control"/>
                </div>
                <div class="form-group">
                    <cpn:text tagName="label" tagClass="control-label" value="Title" i18n="true"/>
                    <input name="jcr:title" type="text"
                           class="composum-assets-dialog_title widget text-field-widget form-control"/>
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
        <sling:call script="description.jsp"/>
    </div>
</cpn:component>
