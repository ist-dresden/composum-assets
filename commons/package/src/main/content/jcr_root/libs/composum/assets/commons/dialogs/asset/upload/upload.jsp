<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<cpn:component var="folder" type="com.composum.assets.commons.pages.model.AssetsFolder"><div class="composum-assets-dialog-create dialog modal fade" role="dialog" aria-hidden="true">
        <div class="composum-assets-dialog modal-dialog modal-lg">
            <div class="modal-content form-panel create">
                <cpn:form action="/bin/cpm/assets/commons.createImage.json" method="POST"
                          class="composum-assets-dialog_form widget-form " enctype="multipart/form-data">
                    <div class="composum-assets-dialog_header modal-header">
                        <button type="button" class="modal-dialog_close fa fa-close" data-dismiss="modal"
                                title="${cpn:i18n(slingRequest,'Close')}"
                                aria-label="${cpn:i18n(slingRequest,'Close')}"></button>
                        <cpn:text tagName="h4" class="modal-title composum-pages-stage-edit-dialog_dialog-title"
                                  value="Upload Asset Original" i18n="true"/>
                    </div>
                    <div class="composum-assets-dialog_body modal-body">
                        <div class="composum-assets-dialog_messages messages">
                            <div class="panel panel-warning hidden">
                                <div class="panel-heading"></div>
                                <div class="panel-body hidden"></div>
                            </div>
                        </div>
                        <input name="_charset_" type="hidden" value="UTF-8"/>
                        <div class="row">
                            <div class="col-xs-8">
                                <div class="form-group composum-pages-edit-widget_pathfield assets-dialog-form_path">
                                    <cpn:text tagName="label" class="control-label composum-pages-edit-widget_label"
                                              value="Image Asset Path" i18n="true"/>
                                    <div class="input-group widget path-widget" data-rules="required">
                                        <input name="path" class="form-control" type="text"/>
                                        <span class="input-group-btn"><button
                                                class="select btn btn-default" type="button"
                                                title="${cpn:i18n(slingRequest,'Select Repository Path')}">...</button></span>
                                    </div>
                                </div>
                            </div>
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
                        </div>
                        <div class="row">
                            <div class="col-xs-8">
                                <div class="form-group composum-pages-edit-widget_textfield assets-dialog-form_name">
                                    <label class="control-label composum-pages-edit-widget_label"><span
                                            class="label-text">${cpn:i18n(slingRequest,'Asset Name')}</span><span
                                            class="composum-pages-edit-widget_hint widget-hint">${cpn:i18n(slingRequest,'add a name if the file name is not the right choice')}</span></label>
                                    <input name="name" class="widget text-field-widget form-control" type="text"
                                           placeholder="enter asset name"/>
                                </div>
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
                            <input name="file" class="widget file-upload-widget form-control required" type="file"
                                   data-rules="required"/>
                        </div>
                    </div>
                    <div class="composum-assets-dialog_footer modal-footer buttons">
                        <button type="button" class="btn btn-default cancel"
                                data-dismiss="modal">${cpn:i18n(slingRequest,'Cancel')}</button>
                        <button type="submit" class="btn btn-primary upload">${cpn:i18n(slingRequest,'Upload')}</button>
                    </div>
                </cpn:form>
            </div>
        </div>
    </div>
</cpn:component>
