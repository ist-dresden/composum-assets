<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<a href="#" data-path="${thumbnail.path}" class="composum-assets-widget-navigator_asset-link"
   title="${cpn:text(thumbnail.path)}">
    <div class="composum-assets-widget-thumbnail_wrapper">
        <sling:include path="${thumbnail.path}" resourceType="composum/assets/commons/widget/preview"
                       replaceSelectors="thumbnail"/>
    </div>
    <div class="composum-assets-widget-thumbnail_meta">
        <cpn:text class="composum-assets-widget-thumbnail_meta_name" value="${thumbnail.name}"/>
        <cpn:text class="composum-assets-widget-thumbnail_meta_last-modified"
                  value="${thumbnail.lastModified}" format="{Date}YYYY-MM-dd HH:mm:ss"/>
        <c:if test="${thumbnail.metaAvailable}">
            <cpn:div test="${thumbnail.metaData.hasDimension}"
                     class="composum-assets-widget-thumbnail_meta_dimension">
                <cpn:text tagName="span" class="width" value="${thumbnail.metaData.width}"/>
                x <cpn:text tagName="span" class="height" value="${thumbnail.metaData.height}"/>
            </cpn:div>
        </c:if>
    </div>
</a>
