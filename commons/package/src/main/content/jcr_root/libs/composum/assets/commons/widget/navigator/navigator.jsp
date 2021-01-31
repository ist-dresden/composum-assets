<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<cpn:bundle basename="composum-assets">
    <cpn:component var="model" type="com.composum.assets.commons.widget.NavigatorModel">
        <div class="composum-assets-widget-navigator widget" data-path="${model.path}" data-filter="${model.filter}">
            <sling:call script="content.jsp"/>
        </div>
    </cpn:component>
</cpn:bundle>
