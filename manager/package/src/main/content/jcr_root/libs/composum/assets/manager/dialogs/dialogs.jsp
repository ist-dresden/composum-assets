<%@page session="false" pageEncoding="utf-8"%>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2"%>
<sling:defineObjects />
<sling:include resourceType="/libs/composum/nodes/console/dialogs"/>
<sling:call script="create-folder.jsp"/>
<sling:call script="asset-upload.jsp"/>
<sling:include resourceType="composum/assets/manager/config/dialog"/>
<sling:include resourceType="composum/assets/manager/config/dialog/create"/>
<sling:include resourceType="composum/assets/manager/config/dialog/delete"/>
