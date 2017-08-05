<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<cpn:component id="finder" type="com.composum.assets.commons.widget.Finder">
    <div class="finder-content large-thumbnails"
         data-type="composum/assets/commons/widgets/asset/finder/thumbnails">
        <div class="thumbnail-panel">
            <c:forEach items="${finder.thumbnails}" var="thumbnail">
                <a href="#" data-path="${thumbnail.path}" class="asset-link">
                    <div class="thumbnail-wrapper">
                            ${thumbnail.content}
                    </div>
                </a>
            </c:forEach>
        </div>
    </div>
</cpn:component>