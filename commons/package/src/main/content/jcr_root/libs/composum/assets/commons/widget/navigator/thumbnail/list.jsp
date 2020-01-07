<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<a href="#" data-path="${thumbnail.path}" title="${thumbnail.path}" class="composum-assets-widget-navigator_asset-link">
    <div class="composum-assets-widget-thumbnail">
        <div class="composum-assets-widget-thumbnail_wrapper">
            <sling:include path="${thumbnail.path}" resourceType="composum/assets/commons/widget/preview"
                           replaceSelectors="thumbnail"/>
        </div>
    </div>
    <div class="composum-assets-widget-thumbnail_list-data">
        <sling:include path="${thumbnail.path}" resourceType="composum/assets/commons/widget/data/short"/>
    </div>
</a>
