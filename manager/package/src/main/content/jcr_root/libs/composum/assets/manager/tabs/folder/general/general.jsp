<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<sling:defineObjects/>
<cpn:component id="model" type="com.composum.assets.commons.widget.FolderModel">
    <div class="folder-detail">
        <div class="folder-toolbar detail-toolbar">
            <sling:call script="../actions.jsp"/>
        </div>
        <div class="folder-content">
            <div class="composum-assets-navigator-browse widget"
                 data-path="${model.selectedPath}"
                 data-resource-type="composum/assets/manager/tabs/folder/general">
            </div>
        </div>
    </div>
</cpn:component>