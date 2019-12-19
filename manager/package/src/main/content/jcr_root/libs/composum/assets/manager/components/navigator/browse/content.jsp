<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<sling:defineObjects/>
<cpn:component id="model" type="com.composum.assets.commons.widget.FolderModel" scope="request">
    <div class="composum-assets-navigator_content composum-assets-navigator_type-${model.viewType}">
        <sling:include resourceType="composum/assets/manager/components/navigator/content"
                       replaceSelectors="${model.viewType}"/>
        <sling:include resourceType="composum/assets/manager/components/navigator/browse/subfolders"
                       replaceSelectors="${model.viewType}"/>
    </div>
</cpn:component>