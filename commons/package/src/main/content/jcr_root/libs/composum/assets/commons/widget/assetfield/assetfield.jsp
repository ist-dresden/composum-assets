<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<cpn:component var="model" type="com.composum.assets.commons.widget.ReferenceModel">
    <div class="composum-assets-widget-assetfield${model.required?' required':''} form-group">
        <sling:call script="label.jsp"/>
        <c:if test="${!model.blankAllowed}">
            <input type="hidden" class="sling-post-hint" name="${model.name}@Delete" value="true"/>
            <input type="hidden" class="sling-post-hint" name="${model.name}@IgnoreBlanks" value="true"/>
        </c:if>
        <div class="composum-assets-widget-assetfield_path-field widget assetfield-widget widget-name_${model.cssName}">
            <div class="composum-assets-widget-assetfield_preview empty-value">
                <%-- <sling:include path="${model.content.path}" replaceSelectors="preview"/> --%>
            </div>
            <div class="input-group">
                <input name="${model.name}" class="composum-assets-widget-assetfield_input path-input form-control"
                       type="text" value="${model.value}" placeholder=""${cpn:i18n(slingRequest,'asset path')}"/>
                <span class="composum-assets-widget-assetfield_popup-button input-group-btn"><button
                        class="composum-assets-widget-assetfield_select select btn btn-default" type="button"
                        title="${cpn:i18n(slingRequest,'Select the asset path')}">...</button></span>
            </div>
        </div>
    </div>
</cpn:component>

