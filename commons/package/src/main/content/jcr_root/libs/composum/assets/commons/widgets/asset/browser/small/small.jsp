<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<sling:defineObjects/>
<cpn:component id="finder" type="com.composum.assets.commons.widget.FolderModel" scope="request">
    <div class="finder-content small-thumbnails" data-path="${finder.path}"
         data-type="composum/assets/commons/widgets/asset/browser/small">
        <sling:call script="reload.jsp"/>
    </div>
</cpn:component>