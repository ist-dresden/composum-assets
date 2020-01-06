<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<cpn:component var="model" type="com.composum.assets.commons.widget.Thumbnail">
    <cpn:text tagName="h4" class="composum-assets-widget-data_name name" value="${model.name}"/>
    <table class="composum-assets-widget-data_data"
           title="${cpn:attr(slingRequest,model.metaAvailable?model.metaData.description:'',0)}">
        <tbody>
        <tr class="composum-assets-widget-data_last-modified timestamp">
            <cpn:text tagName="td" class="label label-default" value="last modified" i18n="true"/>
            <cpn:text tagName="td" class="value" value="${model.lastModified}"
                      format="{Date}YYYY-MM-dd HH:mm:ss"/>
        </tr>
        <c:if test="${not empty model.mimeType}">
            <tr class="composum-assets-widget-data_mime-type">
                <cpn:text tagName="td" class="label label-default" value="mime type" i18n="true"/>
                <cpn:text tagName="td" class="value" value="${model.mimeType}"/>
            </tr>
        </c:if>
        <c:if test="${model.metaAvailable}">
            <c:if test="${model.metaData.hasDimension}">
                <tr class="composum-assets-widget-data_size dimension">
                    <cpn:text tagName="td" class="label label-default" value="width / height" i18n="true"/>
                    <td class="value">
                        <cpn:text tagName="span" class="width" value="${model.metaData.width}"/>
                        x
                        <cpn:text tagName="span" class="height"
                                  value="${model.metaData.height}"/>
                    </td>
                </tr>
            </c:if>
            <c:if test="${model.metaData.hasSize}">
                <tr class="composum-assets-widget-data_filesize">
                    <cpn:text tagName="td" class="label label-default" value="size (bytes)" i18n="true"/>
                    <cpn:text tagName="td" class="value" value="${model.metaData.size}"/>
                </tr>
            </c:if>
        </c:if>
        </tbody>
    </table>
</cpn:component>
