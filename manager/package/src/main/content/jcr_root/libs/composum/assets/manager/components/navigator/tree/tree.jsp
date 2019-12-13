<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<sling:defineObjects/>
<cpn:component var="model" type="com.composum.assets.commons.widget.FolderModel">
    <div class="composum-assets-navigator" data-path="${model.path}">
        <div class="composum-assets-navigator_tree">
        </div>
        <div class="composum-assets-navigator_view">
            <div class="composum-assets-navigator_browse">
            </div>
            <div class="composum-assets-navigator_search">
            </div>
        </div>
    </div>
</cpn:component>
