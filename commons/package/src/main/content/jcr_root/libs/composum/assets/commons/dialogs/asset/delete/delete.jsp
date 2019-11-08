<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<sling:defineObjects/>
<div class="composum-pages-stage-edit-dialog  dialog modal fade in" role="dialog" aria-hidden="true"
     data-pages-edit-success="content:deleted">
    <div class="modal-dialog">
        <div class="modal-content form-panel">
            <cpn:form class="widget-form composum-pages-stage-edit-dialog_form" method="POST"
                      action="/bin/cpm/assets/commons.deleteImage.json" enctype="multipart/form-data">
                <div class="modal-header composum-pages-stage-edit-dialog_header">
                    <button type="button" class="composum-pages-stage-edit-dialog_button-close fa fa-close"
                            data-dismiss="modal" aria-label="Close"></button>
                    <cpn:text tagName="h4" class="modal-title composum-pages-stage-edit-dialog_dialog-title"
                              value="Delete Asset Image" i18n="true"/>
                </div>
                <div class="modal-body composum-pages-stage-edit-dialog_content">
                    <div class="composum-pages-stage-edit-dialog_messages messages">
                        <div class="panel panel-danger">
                            <div class="panel-heading">${cpn:i18n(slingRequest,'Do you really want to delete the seleted asset?')}</div>
                            <div class="panel-body hidden"></div>
                        </div>
                    </div>

                    <div class="form-group composum-pages-edit-widget_pathfield assets-dialog-form_path">
                        <cpn:text tagName="label" class="control-label composum-pages-edit-widget_label"
                                  value="Asset Path" i18n="true"/>
                        <div class="input-group widget path-widget" data-rules="required">
                            <input name="path" class="form-control" type="text"/>
                            <span class="input-group-btn"><button
                                    class="select btn btn-default" type="button"
                                    title="${cpn:i18n(slingRequest,'Select Repository Path')}">...</button></span>
                        </div>
                    </div>

                </div>
                <div class="modal-footer composum-pages-stage-edit-dialog_footer">
                    <div class="composum-pages-stage-edit-dialog_hints">
                    </div>
                    <button type="button"
                            class="composum-pages-stage-edit-dialog_button-cancel composum-pages-stage-edit-dialog_button btn btn-default"
                            data-dismiss="modal">${cpn:i18n(slingRequest,'Cancel')}</button>
                    <button type="submit"
                            class="composum-pages-stage-edit-dialog_button-submit-delete composum-pages-stage-edit-dialog_button btn btn-danger">${cpn:i18n(slingRequest,'Delete')}</button>
                </div>
            </cpn:form>
        </div>
    </div>
</div>
