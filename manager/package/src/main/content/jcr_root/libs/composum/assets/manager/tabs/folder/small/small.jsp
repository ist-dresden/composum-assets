<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<sling:defineObjects/>
<cpn:component id="folder" type="com.composum.assets.commons.widget.Folder">
    <div class="folder-detail" data-path="${folder.path}" data-type="composum/assets/manager/tabs/folder/small">
        <div class="folder-toolbar detail-toolbar">
            <sling:call script="../actions.jsp"/>
        </div>
        <div class="folder-content small-thumbnails">
            <sling:call script="reload.jsp"/>
        </div>
    </div>
</cpn:component>