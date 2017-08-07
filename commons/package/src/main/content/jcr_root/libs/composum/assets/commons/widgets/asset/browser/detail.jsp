<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<sling:defineObjects/>
<cpn:component id="finder" type="com.composum.assets.commons.widget.Folder" scope="request">
    <div class="detail-panel" data-path="${finder.path}">
        <div class="detail-tabs action-bar btn-toolbar asset-finder_actions" role="toolbar">
            <div class="btn-group btn-group-sm" role="group">
                <button type="button" class="go-up fa fa-folder-o text-muted btn btn-default"
                        title="${cpn:i18n(slingRequest,'Go one level Up')}"><i
                        class="fa fa-chevron-up fa-stack-1x"></i><cpn:text
                        value="Go Up" tagName="span" tagClass="label" i18n="true"/></button>
            </div>
            <sling:call script="viewModeButtons.jsp"/>
        </div>
        <div class="detail-content asset-browser-widget_content asset-finder_content">
        </div>
    </div>
</cpn:component>
