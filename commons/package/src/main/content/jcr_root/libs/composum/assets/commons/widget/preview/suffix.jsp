<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<sling:defineObjects/><%
%><cpn:component var="model" type="com.composum.assets.commons.widget.Thumbnail"
                 path="${cpn:filter(slingRequest.requestPathInfo.suffix)}"><% if (model.isValid()) {
%><sling:include path="${cpn:filter(slingRequest.requestPathInfo.suffix)}"
                 resourceType="composum/assets/commons/widget/preview" replaceSelectors=""/><% } else {
    // if the path is 'invalid' (not an asset, e.g. a folder) answer
    // with a successful blank result with a checkable status code
    response.setStatus(HttpServletResponse.SC_ACCEPTED);
}
%></cpn:component>
