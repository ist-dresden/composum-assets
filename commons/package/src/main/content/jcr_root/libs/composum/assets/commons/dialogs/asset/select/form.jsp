<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<sling:defineObjects/>
<div class="modal-header">
    <button type="button" class="modal-dialog_close fa fa-close" data-dismiss="modal"
            title="${cpn:i18n(slingRequest,'Close')}" aria-label="${cpn:i18n(slingRequest,'Close')}"></button>
    <h4 class="modal-title" data-default="${cpn:i18n(slingRequest,'Select Asset Resource')}"></h4>
</div>
<div class="modal-body">
    <div class="messages">
        <div class="alert hidden"></div>
    </div>
    <sling:call script="assetfield.jsp"/>
    <sling:include resourceType="composum/assets/commons/widget/navigator" addSelectors="selectFilter"/>
</div>
<div class="modal-footer buttons">
    <button type="button" class="btn btn-default cancel"
            data-dismiss="modal">${cpn:i18n(slingRequest,'Cancel')}</button>
    <button type="button" class="btn btn-primary select">${cpn:i18n(slingRequest,'Select')}</button>
</div>
