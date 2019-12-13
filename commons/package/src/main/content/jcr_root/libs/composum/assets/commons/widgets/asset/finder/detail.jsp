<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<sling:defineObjects/>
<cpn:component id="finder" type="com.composum.assets.commons.widget.NavigatorBase">
    <div class="detail-panel">
        <div class="detail-tabs action-bar btn-toolbar asset-finder_actions" role="toolbar">
            <div class="btn-group btn-group-sm" role="group">
            </div>
            <sling:call script="viewModeButtons.jsp"/>
        </div>
        <div class="detail-content asset-finder-widget_content asset-finder_content">
        </div>
    </div>
</cpn:component>
