<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<cpn:component var="folder" type="com.composum.assets.commons.pages.model.AssetsFolder">
    <div class="composum-assets_pages-dialog composum-pages-stage-edit-dialog dialog-asset-upload dialog modal fade"
         role="dialog" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content form-panel create">

                <cpn:form classes="widget-form composum-pages-stage-edit-dialog_form assets-dialog-form" method="POST"
                          action="/bin/cpm/assets/commons.uploadImage.json" enctype="multipart/form-data">
                    <input name="_charset_" type="hidden" value="UTF-8"/>

                    <div class="modal-header composum-pages-stage-edit-dialog_header">
                        <button type="button" class="composum-pages-stage-edit-dialog_button-close fa fa-close"
                                data-dismiss="modal" aria-label="Close"></button>
                        <cpn:text tagName="h4" class="modal-title composum-pages-stage-edit-dialog_dialog-title"
                                  value="Upload a New Asset Original" i18n="true"/>
                    </div>

                    <div class="modal-body composum-pages-stage-edit-dialog_content">
                        <div class="composum-pages-stage-edit-dialog_messages messages">
                            <div class="panel panel-warning hidden">
                                <div class="panel-heading"></div>
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

                        <div class="row">
                            <div class="col-xs-4">
                                <cpn:div test="${folder.assetConfigAvailable}"
                                         class="form-group composum-pages-edit-widget_select assets-dialog-form_variation">
                                    <cpn:text tagName="label" class="control-label composum-pages-edit-widget_label"
                                              value="Variation" i18n="true"/>
                                    <select name="variation" class="widget select-widget form-control">
                                        <c:forEach items="${folder.variations}" var="variation">
                                            <option ${variation.defaultConfig?'selected="selected"':''}>${cpn:text(variation.name)}</option>
                                        </c:forEach>
                                    </select>
                                </cpn:div>
                            </div>
                            <div class="col-xs-4">
                            </div>
                            <div class="col-xs-4">
                                <div class="form-group composum-pages-edit-widget_textfield assets-dialog-form_mime-type">
                                    <cpn:text tagName="label" class="control-label composum-pages-edit-widget_label"
                                              value="Mime Type" i18n="true"/>
                                    <input name="mimeType" type="text" class="form-control"/>
                                </div>
                            </div>
                        </div>

                        <div class="form-group composum-pages-edit-widget_fileupload assets-dialog-form_file">
                            <cpn:text tagName="label" class="control-label composum-pages-edit-widget_label"
                                      value="Upload File" i18n="true"/>
                            <input name="file" class="widget file-upload-widget form-control" type="file"
                                   data-rules="required"/>
                        </div>
                    </div>

                    <div class="modal-footer buttons composum-pages-stage-edit-dialog_footer">
                        <div class="composum-pages-stage-edit-dialog_hints">
                        </div>
                        <button class="btn btn-default composum-pages-stage-edit-dialog_button assets-dialog-form_button-cancel"
                                type="button" data-dismiss="modal">${cpn:i18n(slingRequest,'Cancel')}</button>
                        <button class="btn btn-primary composum-pages-stage-edit-dialog_button assets-dialog-form_button-submit"
                                type="submit">${cpn:i18n(slingRequest,'Upload')}</button>
                    </div>
                </cpn:form>
            </div>
        </div>
    </div>
</cpn:component>
