<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<sling:defineObjects/>
<sling:call script="../thumbnails.jsp"/>
<sling:include resourceType="composum/assets/manager/tabs/folder/subfolders"/>
