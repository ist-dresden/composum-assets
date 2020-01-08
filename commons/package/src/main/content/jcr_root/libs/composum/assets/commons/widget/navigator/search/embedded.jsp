<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<sling:defineObjects/>
<cpn:component id="model" type="com.composum.assets.commons.widget.SearchModel" scope="request">
    <div class="composum-assets-widget-navigator-search"
         data-path="${model.selectedPath}" data-filter="${model.filterKey}">
    </div>
</cpn:component>
