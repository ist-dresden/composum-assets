<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<a href="#" data-path="${thumbnail.path}" class="composum-assets-navigator_asset-link">
    <div class="composum-assets-navigator_thumbnail">
        <div class="composum-assets-navigator_thumbnail-wrapper">
            ${thumbnail.content}
        </div>
    </div>
    <div class="composum-assets-navigator_thumbnail_list-data">
        <cpn:text tagName="h4" class="name" value="${thumbnail.name}"/>
        <table>
            <tbody>
            <tr class="composum-assets-navigator_thumbnail_last-modified timestamp">
                <cpn:text tagName="td" class="label label-default" value="last modified" i18n="true"/>
                <cpn:text tagName="td" class="value" value="${thumbnail.lastModified}"
                          format="{Date}YYYY-MM-dd HH:mm:ss"/>
            </tr>
            <c:if test="${not empty thumbnail.mimeType}">
                <tr class="composum-assets-navigator_thumbnail_mime-type">
                    <cpn:text tagName="td" class="label label-default" value="mime type" i18n="true"/>
                    <cpn:text tagName="td" class="value" value="${thumbnail.mimeType}"/>
                </tr>
            </c:if>
            <c:if test="${thumbnail.metaAvailable}">
                <c:if test="${thumbnail.metaData.hasDimension}">
                    <tr class="composum-assets-navigator_thumbnail_size dimension">
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
                    <tr class="composum-assets-navigator_thumbnail_filesize">
                        <cpn:text tagName="td" class="label label-default" value="size (bytes)" i18n="true"/>
                        <cpn:text tagName="td" class="value" value="${thumbnail.metaData.size}"/>
                    </tr>
                </c:if>
                <c:if test="${thumbnail.metaData.hasDate}">
                    <div class="composum-assets-navigator_thumbnail_date">
                        <cpn:text tagName="td" class="label label-default" value="date" i18n="true"/>
                        <cpn:text tagName="td" class="value" value="${thumbnail.metaData.date}"
                                  format="{Date}YYYY-MM-dd HH:mm:ss"/>
                    </div>
                </c:if>
            </c:if>
            </tbody>
        </table>
        <c:if test="${thumbnail.metaAvailable}">
            <cpn:text class="composum-assets-navigator_thumbnail_description"
                      value="${thumbnail.metaData.description}" type="rich"/>
        </c:if>
    </div>
</a>
