<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<cpn:component id="model" type="com.composum.assets.commons.widget.ConfigView" scope="request">
    <tr class="size aspect">
        <cpn:text tagName="th" tagClass="key" value="Size" i18n="true"/>
        <td class="value">
                ${cpn:text(model.size.width)}
            x
                ${cpn:text(model.size.height)}
            (${cpn:text(model.size.aspectRatio)})
        </td>
    </tr>
</cpn:component>
