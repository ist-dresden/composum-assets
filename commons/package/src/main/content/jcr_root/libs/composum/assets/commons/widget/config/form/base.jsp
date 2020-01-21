<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<cpn:component var="model" type="com.composum.assets.commons.widget.ConfigModel">
    <div class="composum-assets-widget-config_header">
        <div class="composum-assets-widget-config_path">/path/to/base</div>
    </div>
    <sling:include replaceSelectors=""/>
</cpn:component>
