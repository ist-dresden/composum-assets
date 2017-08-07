<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<c:forEach items="${finder.thumbnails}" var="thumbnail">
    <a href="#" data-path="${thumbnail.path}" class="asset-link row">
        <div class="thumbnail-column col-lg-2 col-md-2 col-sm-3 col-xs-4">
            <div class="thumbnail-wrapper">
                    ${thumbnail.content}
            </div>
        </div>
        <div class="data-column col-lg-10 col-md-10 col-sm-9 col-xs-8">
            <div class="image-data">
                <cpn:text tagName="h4" tagClass="name" value="${thumbnail.name}"/>
                <table>
                    <tbody>
                    <tr class="last-modified timestamp">
                        <cpn:text tagName="td" tagClass="label label-default" value="last modified" i18n="true"/>
                        <cpn:text tagName="td" tagClass="value" value="${thumbnail.lastModified}"
                                  format="{Date}YYYY-MM-dd HH:mm:ss"/>
                    </tr>
                    <c:if test="${thumbnail.metaAvailable}">
                        <c:if test="${thumbnail.metaData.hasDimension}">
                            <tr class="size dimension">
                                <cpn:text tagName="td" tagClass="label label-default" value="width / height"
                                          i18n="true"/>
                                <td class="value">
                                    <cpn:text tagName="span" tagClass="width" value="${thumbnail.metaData.width}"/>
                                    x
                                    <cpn:text tagName="span" tagClass="height"
                                              value="${thumbnail.metaData.height}"/>
                                </td>
                            </tr>
                        </c:if>
                        <c:if test="${thumbnail.metaData.hasSize}">
                            <tr class="filesize">
                                <cpn:text tagName="td" tagClass="label label-default" value="size (bytes)"
                                          i18n="true"/>
                                <cpn:text tagName="td" tagClass="value" value="${thumbnail.metaData.size}"/>
                            </tr>
                        </c:if>
                        <c:if test="${thumbnail.metaData.hasDate}">
                            <div class="date">
                                <cpn:text tagName="td" tagClass="label label-default" value="date" i18n="true"/>
                                <cpn:text tagName="td" tagClass="value" value="${thumbnail.metaData.date}"
                                          format="{Date}YYYY-MM-dd HH:mm:ss"/>
                            </div>
                        </c:if>
                    </c:if>
                    </tbody>
                </table>
            </div>
            <c:if test="${thumbnail.metaAvailable}">
                <cpn:text tagClass="description" value="${thumbnail.metaData.description}" type="rich"/>
            </c:if>
        </div>
    </a>
</c:forEach>
