<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<sling:defineObjects/>
<cpn:component var="model" type="com.composum.assets.commons.widget.TreeModel">
    <div class="composum-assets-navigator-tree" data-path="${model.path}">
        <div class="composum-assets-navigator-tree_wrapper tree-panel">
            <div class="composum-assets-navigator-tree_tree">
            </div>
        </div>
    </div>
</cpn:component>
