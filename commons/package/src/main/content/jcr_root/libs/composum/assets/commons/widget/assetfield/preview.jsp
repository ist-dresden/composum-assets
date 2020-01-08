<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<sling:defineObjects/><%
%><cpn:component var="model" type="com.composum.assets.commons.widget.Thumbnail"
                 path="${slingRequest.requestPathInfo.suffix}"><% if (model.isValid()) { %>
    <div class="composum-assets-widget-assetfield_view">
        <sling:include path="${slingRequest.requestPathInfo.suffix}"
                       resourceType="composum/assets/commons/widget/preview" replaceSelectors=""/>
    </div>
    <div class="composum-assets-widget-assetfield_data">
        <sling:include path="${slingRequest.requestPathInfo.suffix}"
                       resourceType="composum/assets/commons/widget/data/short" replaceSelectors=""/>
    </div>
    <% } else {
        // if the path is 'invalid' (not an asset, e.g. a folder) answer
        // with a successful blank result with a checkable status code
        response.setStatus(HttpServletResponse.SC_ACCEPTED);
    }
    %></cpn:component>
