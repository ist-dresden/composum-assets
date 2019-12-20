<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<sling:defineObjects/>
<cpn:component var="model" type="com.composum.assets.commons.widget.FolderModel" scope="request">
    <div class="composum-assets-navigator_view-type-keys btn-group btn-group-sm" role="group">
        <a class="composum-assets-navigator_view-type fa fa-th btn btn-default${model.viewType=='small'?' active':''}"
           href="#" data-view-type="small" title="${cpn:i18n(slingRequest,'Small Thumbnails')}"><cpn:text
                value="small" tagName="span" class="label" i18n="true"/></a>
        <a class="composum-assets-navigator_view-type fa fa-th-large btn btn-default${model.viewType=='large'?' active':''}"
           href="#" data-view-type="large" title="${cpn:i18n(slingRequest,'Large Thumbnails')}"><cpn:text
                value="large" tagName="span" class="label" i18n="true"/></a>
        <a class="composum-assets-navigator_view-type fa fa-th-list btn btn-default${model.viewType=='list'?' active':''}"
           href="#" data-view-type="list" title="${cpn:i18n(slingRequest,'Assets List')}"><cpn:text
                value="List" tagName="span" class="label" i18n="true"/></a>
    </div>
</cpn:component>
