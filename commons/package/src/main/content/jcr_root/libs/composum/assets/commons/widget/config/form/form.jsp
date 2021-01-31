<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<cpn:component var="model" type="com.composum.assets.commons.widget.ConfigModel" scope="request">
    <div class="composum-assets-widget-config-form">
        <form class="composum-assets-widget-config-form_form widget-form"
              data-config="${model.configPath}" data-example="${model.handle.inherited.example_image_path}"
              action="${model.configPath}" method="POST" data-valid="${model.valid}">
            <sling:call script="content.jsp"/>
        </form>
    </div>
</cpn:component>

