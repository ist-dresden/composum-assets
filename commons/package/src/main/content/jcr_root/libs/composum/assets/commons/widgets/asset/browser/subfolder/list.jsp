<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<sling:defineObjects/>
<cpn:component id="finder" type="com.composum.assets.commons.widget.FolderModel" scope="request" replace="true">
    <sling:include resourceType="composum/assets/commons/widgets/asset/browser/list" replaceSelectors="content"/>
</cpn:component>
