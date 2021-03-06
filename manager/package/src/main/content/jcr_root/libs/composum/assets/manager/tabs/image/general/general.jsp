<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<cpn:component id="image" type="com.composum.assets.commons.handle.SimpleImage">
    <div class="image-detail" data-path="${image.path}">
        <div class="image-toolbar detail-toolbar">
            <div class="btn-group btn-group-sm" role="group">
                <button class="delete fa fa-trash btn btn-default"
                        title="${cpn:i18n(slingRequest,'Delete Image')}"><cpn:text
                        value="Delete" tagName="span" class="label" i18n="true"/></button>
            </div>
            <div class="btn-group btn-group-sm" role="group">
                <button class="reload fa fa-refresh btn btn-default"
                        title="${cpn:i18n(slingRequest,'Reload')}"><cpn:text
                        value="Reload" tagName="span" class="label" i18n="true"/></button>
            </div>
        </div>
        <sling:include resourceType="composum/assets/commons/widget/preview" replaceSelectors="lightbox"/>
    </div>
</cpn:component>