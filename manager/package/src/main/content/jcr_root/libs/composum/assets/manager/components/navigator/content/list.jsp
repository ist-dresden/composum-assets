<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<cpn:component id="model" type="com.composum.assets.commons.widget.NavigatorBase">
    <div class="composum-assets-navigator_item-list" data-path="${model.path}">
        <c:forEach items="${model.thumbnails}" var="thumbnail">
            <c:set var="thumbnail" value="${thumbnail}" scope="request"/>
            <sling:include path="${thumbnail.path}" replaceSelectors="list"
                           resourceType="composum/assets/manager/components/navigator/thumbnail"/>
        </c:forEach>
    </div>
</cpn:component>