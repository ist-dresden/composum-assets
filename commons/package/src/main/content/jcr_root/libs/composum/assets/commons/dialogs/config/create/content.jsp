<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<cpn:component var="model" type="com.composum.assets.commons.widget.ConfigModel" scope="request">
    <div class="composum-assets-dialogs-config_content">
        <div class="row">
            <div class="col col-xs-12">
                <div class="form-group">
                    <cpn:text tagName="label" tagClass="control-label" value="Path"
                              i18n="true"/>
                    <input value="${model.path}" data-root="${model.contentRoot}" type="text" disabled
                           class="composum-assets-dialogs-config_path widget path-widget form-control"/>
                </div>
            </div>
        </div>
        <cpn:div class="row" test="${not empty model.basePath}">
            <div class="col col-xs-9">
                <div class="form-group">
                    <cpn:text tagName="label" tagClass="control-label" value="Base" i18n="true"/>
                    <input value="${model.baseHolder}" data-root="${model.contentRoot}" type="text" disabled
                           class="composum-assets-dialogs-config_base widget path-widget form-control"/>
                </div>
            </div>
            <div class="col col-xs-3">
                <div class="form-group ">
                    <label class="checkbox-inline">
                        <input type="hidden" name="extension@TypeHint" value="Boolean"/>
                        <input type="hidden" name="extension@Delete" value="true"/>
                        <input name="extension" type="checkbox"
                               class="composum-assets-dialogs-config_extension"/>${cpn:i18n(slingRequest,'Extension')}
                    </label>
                </div>
            </div>
        </cpn:div>
        <div class="row">
            <div class="col col-xs-6">
                <div class="form-group">
                    <cpn:text tagName="label" tagClass="control-label" value="Title" i18n="true"/>
                    <input name="jcr:title" type="text"
                           class="composum-assets-dialogs-config_title widget text-field-widget form-control"/>
                </div>
            </div>
            <div class="col col-xs-6">
            </div>
        </div>
        <sling:call script="description.jsp"/>
    </div>
</cpn:component>
