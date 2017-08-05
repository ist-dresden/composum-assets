<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<cpn:component id="folder" type="com.composum.assets.commons.widget.Folder">
    <div class="thumbnail-panel" data-path="${folder.path}">
        <c:forEach items="${folder.thumbnails}" var="thumbnail">
            <a href="#" data-path="${thumbnail.path}" class="asset-link">
                <div class="thumbnail-wrapper">
                    ${thumbnail.content}
                </div>
            </a>
        </c:forEach>
    </div>
</cpn:component>