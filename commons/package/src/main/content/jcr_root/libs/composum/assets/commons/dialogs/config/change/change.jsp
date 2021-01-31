<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<cpn:component var="model" type="com.composum.assets.commons.widget.ConfigModel" scope="request">
    <div class="composum-assets-dialog_config-change composum-assets-dialog dialog modal fade"
         role="dialog" aria-hidden="true">
        <div class="modal-dialog">
            <div class="composum-assets-widget-config-form modal-content form-panel default">
                <cpn:form action="${model.actionUrl}" method="POST"
                          data-config="${model.configPath}" data-valid="${model.valid}"
                          class="composum-assets-dialog_form composum-assets-widget-config-form_form widget-form">
                    <div class="modal-header">
                        <button type="button" class="modal-dialog_close fa fa-close" data-dismiss="modal"
                                title="${cpn:i18n(slingRequest,'Close')}"
                                aria-label="${cpn:i18n(slingRequest,'Close')}"></button>
                        <h4 class="modal-title">
                                ${cpn:i18n(slingRequest,'Change Configuration')}${model.configLabel}
                        </h4>
                        <h5 class="modal-subtitle path">${cpn:path(model.holderPath)}</h5>
                    </div>
                    <div class="composum-assets-dialog_body modal-body">
                        <div class="composum-assets-dialog_messages messages">
                            <div class="panel panel-warning hidden">
                                <div class="panel-heading"></div>
                                <div class="panel-body hidden"></div>
                            </div>
                        </div>
                        <input name="_charset_" type="hidden" value="UTF-8"/>
                        <div class="composum-assets-dialog_content">
                            <sling:call script="content.jsp"/>
                        </div>
                    </div>
                    <div class="composum-assets-dialog_footer modal-footer buttons">
                        <button type="button" class="btn btn-default cancel"
                                data-dismiss="modal">${cpn:i18n(slingRequest,'Cancel')}</button>
                        <button type="submit" class="btn btn-primary create">${cpn:i18n(slingRequest,'Save')}</button>
                    </div>
                </cpn:form>
            </div>
        </div>
    </div>
</cpn:component>
