<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<cpn:component var="model" type="com.composum.assets.commons.widget.ConfigModel" scope="request">
    <div class="composum-assets-dialogs-config-create dialog modal fade" role="dialog" aria-hidden="true">
        <div class="composum-assets-dialogs-config modal-dialog">
            <div class="modal-content form-panel default">
                <cpn:form action="/bin/cpm/assets/config.create.json${model.path}" method="POST"
                          class="composum-assets-dialogs-config_form widget-form">
                    <div class="composum-assets-dialogs-config_header modal-header">
                        <button type="button" class="modal-dialog_close fa fa-close" data-dismiss="modal"
                                title="${cpn:i18n(slingRequest,'Close')}"
                                aria-label="${cpn:i18n(slingRequest,'Close')}"></button>
                        <sling:call script="header.jsp"/>
                    </div>
                    <div class="composum-assets-dialogs-config_body modal-body">
                        <div class="composum-assets-dialogs-config_messages messages">
                            <div class="panel panel-warning hidden">
                                <div class="panel-heading"></div>
                                <div class="panel-body hidden"></div>
                            </div>
                        </div>
                        <input name="_charset_" type="hidden" value="UTF-8"/>
                        <sling:call script="content.jsp"/>
                    </div>
                    <div class="composum-assets-dialogs-config_footer modal-footer buttons">
                        <button type="button" class="btn btn-default cancel" data-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-primary create">Create</button>
                    </div>
                </cpn:form>
            </div>
        </div>
    </div>
</cpn:component>
