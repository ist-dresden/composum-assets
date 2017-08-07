<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<cpn:component id="model" type="com.composum.assets.manager.config.RenditionConfigBean" scope="request">
    <c:if test="${model.config.blur.valid}">
        <tr class="transform aspect">
            <cpn:text tagName="th" tagClass="key" value="transform" i18n="true"/>
            <td class="value">${cpn:i18n(slingRequest,'blur')}.${cpn:i18n(slingRequest,'factor')}:
                    ${cpn:text(model.config.blur.factor)}
            </td>
        </tr>
    </c:if>
</cpn:component>
