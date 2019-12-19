<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<cpn:component id="configBean" type="com.composum.assets.manager.config.RenditionConfigBean" scope="request">
    <div class="config properties">
        <table class="key value">
            <sling:include resourceType="composum/assets/manager/config/properties/size"/>
            <sling:include resourceType="composum/assets/manager/config/properties/crop"/>
            <sling:include resourceType="composum/assets/manager/config/properties/transformation"/>
            <sling:include resourceType="composum/assets/manager/config/properties/watermark"/>
        </table>
    </div>
</cpn:component>