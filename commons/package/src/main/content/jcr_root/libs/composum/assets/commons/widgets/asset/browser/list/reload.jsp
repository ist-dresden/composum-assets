<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<sling:defineObjects/>
<div class="list-content" data-path="${finder.path}">
    <sling:call script="content.jsp"/>
</div>
<sling:include resourceType="composum/assets/commons/widgets/asset/browser/subfolders" replaceSelectors="list"/>
