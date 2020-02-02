<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<sling:defineObjects/>
<div class="composum-assets-dialogs-config_messages messages">
    <div class="panel panel-warning">
        <div class="panel-heading">${cpn:i18n(slingRequest,'Do you really want to delete this Asset configuration?')}</div>
        <div class="panel-body hidden"></div>
    </div>
</div>
