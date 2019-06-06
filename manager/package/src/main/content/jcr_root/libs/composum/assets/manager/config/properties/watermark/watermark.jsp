<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<cpn:component id="model" type="com.composum.assets.manager.config.RenditionConfigBean" scope="request">
    <c:if test="${model.config.watermark.valid}">
        <tr class="watermark aspect">
            <th class="key" rowspan="3"><cpn:text tagName="none" value="watermark" i18n="true"/></th>
            <cpn:text tagName="td" class="value" value="${model.config.watermark.text}"/>
        </tr>
        <tr><cpn:text tagName="td" class="value" value="${model.config.watermark.font}"/></tr>
        <tr>
            <td class="value">${cpn:text(model.config.watermark.horizontal)}
                / ${cpn:text(model.config.watermark.vertical)}; ${cpn:text(model.config.watermark.colorCode)}
                (${cpn:text(model.config.watermark.alpha)})
            </td>
        </tr>
    </c:if>
</cpn:component>
