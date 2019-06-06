<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<cpn:component id="model" type="com.composum.assets.manager.config.RenditionConfigBean" scope="request">
    <tr class="size aspect">
        <cpn:text tagName="th" class="key" value="size" i18n="true"/>
        <td class="value">${cpn:text(model.config.size.width)} x ${cpn:text(model.config.size.height)}
            (${cpn:text(model.config.size.aspectRatio)})
        </td>
    </tr>
</cpn:component>
