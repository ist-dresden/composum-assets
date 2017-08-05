<%@page session="false" pageEncoding="utf-8"%>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2"%>
<sling:defineObjects />
<sling:call script="/libs/composum/nodes/console/browser/dialogs/node-copy.jsp"/>
<sling:call script="/libs/composum/nodes/console/browser/dialogs/node-move.jsp"/>
<sling:call script="/libs/composum/nodes/console/browser/dialogs/node-rename.jsp"/>
<sling:call script="/libs/composum/nodes/console/browser/dialogs/node-delete.jsp"/>
<sling:call script="create-folder.jsp"/>
<sling:call script="asset-upload.jsp"/>
<sling:include resourceType="composum/assets/manager/config/dialog"/>
<sling:include resourceType="composum/assets/manager/config/dialog/create"/>
<sling:include resourceType="composum/assets/manager/config/dialog/delete"/>
<sling:call script="/libs/composum/nodes/console/page/dialogs.jsp"/>
