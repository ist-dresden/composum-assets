<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpn:component id="model" type="com.composum.assets.commons.widget.SearchModel">
    <div class="query-panel composum-assets-widget-navigator_search">
        <div class="composum-assets-widget-navigator-search widget" data-path="${model.selectedPath}"></div>
    </div>
</cpn:component>
