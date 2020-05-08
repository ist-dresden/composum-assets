<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<cpn:component id="manager" type="com.composum.assets.manager.view.ManagerBean" scope="request">
    <div class="composum-assets-dialog_folder-create composum-assets-dialog dialog modal fade"
         role="dialog" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content form-panel default">
                <cpn:form action="/bin/cpm/nodes/node.create.json" method="POST"
                          class="composum-assets-dialogs-folder_form widget-form" >
                    <div class="composum-assets-dialogs-folder_header modal-header">
                        <button type="button" class="modal-dialog_close fa fa-close" data-dismiss="modal"
                                title="${cpn:i18n(slingRequest,'Close')}"
                                aria-label="${cpn:i18n(slingRequest,'Close')}"></button>
                        <cpn:text tagName="h4" class="modal-title composum-pages-stage-edit-dialog_dialog-title"
                                  value="Create Folder" i18n="true"/>
                    </div>
                    <div class="composum-assets-dialogs-folder_body modal-body">
                        <div class="composum-assets-dialogs-folder_messages messages">
                            <div class="panel panel-warning hidden">
                                <div class="panel-heading"></div>
                                <div class="panel-body hidden"></div>
                            </div>
                        </div>
                        <input name="_charset_" type="hidden" value="UTF-8"/>
                        <input name="path" type="hidden"/>
                        <input name="type" type="hidden"/>
                        <div class="row">
                            <div class="col col-xs-8">
                                <div class="form-group">
                                    <label class="control-label">Folder Name</label>
                                    <input name="name" class="widget text-field-widget form-control" type="text"
                                           placeholder="enter folder name" data-rules="mandatory"/>
                                </div>
                            </div>
                            <div class="col col-xs-4">
                                <div class="form-group checkbox">
                                    <label class="control-label"><input name="ordered" class="widget checkbox-widget"
                                                                        type="checkbox"/>Ordered Folder</label>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="composum-assets-dialogs-asset_footer modal-footer buttons">
                        <button type="button" class="btn btn-default cancel"
                                data-dismiss="modal">${cpn:i18n(slingRequest,'Cancel')}</button>
                        <button type="submit" class="btn btn-primary create">${cpn:i18n(slingRequest,'Create')}</button>
                    </div>
                </cpn:form>
            </div>
        </div>
    </div>
</cpn:component>