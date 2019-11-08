<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<div class="composum-assets_pages-dialog composum-pages-stage-edit-dialog dialog-folder-config dialog modal fade"
     role="dialog" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content form-panel create">

            <cpn:form classes="widget-form composum-pages-stage-edit-dialog_form" method="POST"
                      action="/bin/cpm/assets/assets.createImage.json" enctype="multipart/form-data">
                <input name="_charset_" type="hidden" value="UTF-8"/>

                <div class="modal-header composum-pages-stage-edit-dialog_header">
                    <button type="button" class="composum-pages-stage-edit-dialog_button-close fa fa-close"
                            data-dismiss="modal" aria-label="Close"></button>
                    <cpn:text tagName="h4" class="modal-title composum-pages-stage-edit-dialog_dialog-title"
                              value="Create a New Asset" i18n="true"/>
                </div>

                <div class="modal-body composum-pages-stage-edit-dialog_content">
                    <div class="composum-pages-stage-edit-dialog_messages messages">
                        <div class="panel panel-warning hidden">
                            <div class="panel-heading"></div>
                            <div class="panel-body hidden"></div>
                        </div>
                    </div>

                    <div class="row">
                        <div class="col colxs-9">
                            <div class="form-group composum-pages-edit-widget_pathfield composum-pages-edit-widget_path">
                                <cpn:text tagName="label" class="control-label composum-pages-edit-widget_label"
                                          value="Parent Folder" i18n="true"/>
                                <div class="input-group widget path-widget" data-rules="required">
                                    <input name="path" class="form-control" type="text"/>
                                    <span class="input-group-btn"><button
                                            class="select btn btn-default" type="button"
                                            title="${cpn:i18n(slingRequest,'Select Repository Path')}">...</button></span>
                                </div>
                            </div>
                        </div>
                        <div class="col colxs-3">
                            <div class="form-group composum-pages-edit-widget_select composum-pages-edit-widget_variation">
                                <cpn:text tagName="label" class="control-label composum-pages-edit-widget_label"
                                          value="Variation" i18n="true"/>
                                <select name="variation" class="widget select-widget form-control">
                                </select>
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col colxs-9">
                            <div class="form-group composum-pages-edit-widget_textfield composum-pages-edit-widget_name">
                                <cpn:text tagName="label" class="control-label composum-pages-edit-widget_label"
                                          value="Resource Name" i18n="true"/>
                                <input name="name" class="widget text-field-widget form-control" type="text"
                                       placeholder="enter node name" data-rules="mandatory"/>
                            </div>
                        </div>
                        <div class="col colxs-3">
                            <div class="form-group composum-pages-edit-widget_textfield composum-pages-edit-widget_mime-type">
                                <cpn:text tagName="label" class="control-label composum-pages-edit-widget_label"
                                          value="Mime Type" i18n="true"/>
                                <input name="mimeType" type="text" class="form-control"/>
                            </div>
                        </div>
                    </div>

                    <div class="form-group composum-pages-edit-widget_fileupload composum-pages-edit-widget_file">
                        <cpn:text tagName="label" class="control-label composum-pages-edit-widget_label"
                                  value="Upload File" i18n="true"/>
                        <input name="file" class="widget file-upload-widget form-control" type="file"
                               data-options="hidePreview"/>
                    </div>
                </div>

                <div class="modal-footer buttons composum-pages-stage-edit-dialog_footer">
                    <div class="composum-pages-stage-edit-dialog_hints">
                    </div>
                    <button type="button"
                            class="btn btn-default composum-pages-stage-edit-dialog_button composum-pages-stage-edit-dialog_button-cancel"
                            data-dismiss="modal">${cpn:i18n(slingRequest,'Cancel')}</button>
                    <button type="submit"
                            class="btn btn-primary composum-pages-stage-edit-dialog_button composum-pages-stage-edit-dialog_button-submit">${cpn:i18n(slingRequest,'Upload')}</button>
                </div>
            </cpn:form>
        </div>
    </div>
</div>