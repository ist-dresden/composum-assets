<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/><%-- the ${thumbnail} model must be available (in request scope) --%>
<cpn:text tagName="h4" class="composum-assets-widget-thumbnail_name name" value="${thumbnail.name}"/>
<table class="composum-assets-widget-thumbnail_data"
       title="${cpn:attr(slingRequest,thumbnail.metaAvailable?thumbnail.metaData.description:'',0)}">
    <tbody>
    <tr class="composum-assets-widget-thumbnail_last-modified timestamp">
        <cpn:text tagName="td" class="label label-default" value="last modified" i18n="true"/>
        <cpn:text tagName="td" class="value" value="${thumbnail.lastModified}"
                  format="{Date}YYYY-MM-dd HH:mm:ss"/>
    </tr>
    <c:if test="${not empty thumbnail.mimeType}">
        <tr class="composum-assets-widget-thumbnail_mime-type">
            <cpn:text tagName="td" class="label label-default" value="mime type" i18n="true"/>
            <cpn:text tagName="td" class="value" value="${thumbnail.mimeType}"/>
        </tr>
    </c:if>
    <c:if test="${thumbnail.metaAvailable}">
        <c:if test="${thumbnail.metaData.hasDimension}">
            <tr class="composum-assets-widget-thumbnail_size dimension">
                <cpn:text tagName="td" class="label label-default" value="width / height" i18n="true"/>
                <td class="value">
                    <cpn:text tagName="span" class="width" value="${thumbnail.metaData.width}"/>
                    x
                    <cpn:text tagName="span" class="height"
                              value="${thumbnail.metaData.height}"/>
                </td>
            </tr>
        </c:if>
        <c:if test="${thumbnail.metaData.hasSize}">
            <tr class="composum-assets-widget-thumbnail_filesize">
                <cpn:text tagName="td" class="label label-default" value="size (bytes)" i18n="true"/>
                <cpn:text tagName="td" class="value" value="${thumbnail.metaData.size}"/>
            </tr>
        </c:if>
        <c:if test="${thumbnail.metaData.hasDate}">
            <div class="composum-assets-widget-thumbnail_date">
                <cpn:text tagName="td" class="label label-default" value="date" i18n="true"/>
                <cpn:text tagName="td" class="value" value="${thumbnail.metaData.date}"
                          format="{Date}YYYY-MM-dd HH:mm:ss"/>
            </div>
        </c:if>
    </c:if>
    </tbody>
</table>
