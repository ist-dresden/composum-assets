<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<sling:defineObjects/><%
%><cpn:component var="thumbnail" type="com.composum.assets.commons.widget.Thumbnail" scope="request"
                 path="${cpn:filter(slingRequest.requestPathInfo.suffix)}"><% if (thumbnail.isValid()) {
%><sling:call script="short.jsp"/><% } else { // if the thumbnail is 'invalid' (not an asset, e.g. a folder) answer...
    response.setStatus(HttpServletResponse.SC_ACCEPTED); // with a successful blank result with a checkable status code
}
%></cpn:component>
