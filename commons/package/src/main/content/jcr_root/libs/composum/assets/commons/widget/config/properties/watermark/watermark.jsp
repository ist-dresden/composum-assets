<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<cpn:component id="model" type="com.composum.assets.commons.widget.ConfigView" scope="request">
    <c:if test="${model.watermark.valid}">
        <tr class="watermark aspect">
            <th class="key" rowspan="3">${cpn:i18n(slingRequest,'Watermark')}</th>
            <cpn:text tagName="td" tagClass="value" value="${model.watermark.text}"/>
        </tr>
        <tr><cpn:text tagName="td" tagClass="value" value="${model.watermark.font}"/></tr>
        <tr>
            <td class="value">
                    ${cpn:text(model.watermark.horizontal)}
                /
                    ${cpn:text(model.watermark.vertical)}
                ;
                    ${cpn:text(model.watermark.colorCode)}
                (${cpn:text(model.watermark.alpha)})
            </td>
        </tr>
    </c:if>
</cpn:component>
