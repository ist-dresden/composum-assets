<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<cpn:component var="model" type="com.composum.assets.commons.widget.ConfigModel" scope="request">
    <div class="composum-assets-dialog_content">
        <cpn:div class="row" test="${not empty model.basePath}">
            <div class="col col-xs-9">
                <div class="form-group">
                    <cpn:text tagName="label" tagClass="control-label" value="Base" i18n="true"/>
                    <input value="${model.baseHolder}" type="text" disabled
                           class="composum-assets-dialog_base widget path-widget form-control"
                           data-root="${model.contentRoot}"/>
                </div>
            </div>
            <div class="col col-xs-3">
                <div class="form-group ">
                    <label class="checkbox-inline">
                        <input type="hidden" name="extension@TypeHint" value="Boolean"/>
                        <input type="hidden" name="extension@Delete" value="true"/>
                        <input name="extension" type="checkbox" disabled
                               <c:if test="${model.extension}">checked</c:if>
                               class="composum-assets-dialog_extension"/>${cpn:i18n(slingRequest,'Extension')}
                    </label>
                </div>
            </div>
        </cpn:div>
        <div class="row">
            <div class="col col-xs-6">
                <div class="form-group">
                    <cpn:text tagName="label" tagClass="control-label" value="Title" i18n="true"/>
                    <input name="jcr:title" value="${cpn:value(model.title)}" type="text" disabled
                           class="composum-assets-dialog_title widget text-field-widget form-control"/>
                </div>
            </div>
            <div class="col col-xs-6">
            </div>
        </div>
        <div class="row">
            <div class="col col-xs-12">
                <div class="form-group">
                    <label class="control-label">${cpn:i18n(slingRequest,'Description')}</label>
                    <div class="richtext-value form-control readonly">${cpn:rich(slingRequest,model.description)}</div>
                </div>
            </div>
        </div>
    </div>
</cpn:component>
