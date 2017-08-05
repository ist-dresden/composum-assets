<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<cpn:component id="manager" type="com.composum.assets.manager.view.ManagerBean" scope="request">
    <div id="asset-upload-dialog" class="dialog modal fade" role="dialog" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content form-panel create">

                <cpn:form classes="widget-form" enctype="multipart/form-data"
                          data-asset-action="/bin/cpm/assets/assets.createImage.json"
                          data-file-action="/bin/cpm/nodes/node.create.json"
                          action="">
                    <input name="_charset_" type="hidden" value="UTF-8"/>
                    <input name="type" type="hidden" value="nt:file"/>

                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                                aria-hidden="true">&times;</span></button>
                        <cpn:text tagName="h4" tagClass="modal-title create" value="Create a New Asset" i18n="true"/>
                        <cpn:text tagName="h4" tagClass="modal-title original" value="Upload Asset Original"
                                  i18n="true"/>
                    </div>

                    <div class="modal-body">
                        <div class="messages">
                            <div class="alert"></div>
                        </div>

                        <div class="type-selection">
                            <div class="type btn-group btn-group-sm widget select-buttons-widget">
                                <button type="button" data-value="asset" class="fa fa-picture-o btn btn-default"
                                        title="${cpn:i18n(slingRequest,'create an image asset')}"><cpn:text
                                        tagName="span" tagClass="label" value="Image Asset" i18n="true"/></button>
                                <button type="button" data-value="file" class="fa fa-file-image-o btn btn-default"
                                        title="${cpn:i18n(slingRequest,'upload a simple file')}"><cpn:text
                                        tagName="span" tagClass="label" value="Simple File" i18n="true"/></button>
                            </div>
                        </div>

                        <div class="form-group path">
                            <cpn:text tagName="label" tagClass="control-label" value="Parent Folder" i18n="true"/>
                            <div class="input-group widget path-widget" data-rules="mandatory">
                                <input name="path" class="form-control" type="text"/>
                                <span class="input-group-btn"><button
                                        class="select btn btn-default" type="button"
                                        title="${cpn:i18n(slingRequest,'Select Repository Path')}">...</button></span>
                            </div>
                        </div>
                        <div class="form-group variation">
                            <cpn:text tagName="label" tagClass="control-label" value="Variation" i18n="true"/>
                            <select name="variation" class="widget select-widget form-control">
                            </select>
                        </div>

                        <div class="form-group name">
                            <cpn:text tagName="label" tagClass="control-label" value="Resource Name" i18n="true"/>
                            <input name="name" class="widget text-field-widget form-control" type="text"
                                   placeholder="enter node name" data-rules="mandatory"/>
                        </div>
                        <div class="form-group file">
                            <cpn:text tagName="label" tagClass="control-label" value="Upload File" i18n="true"/>
                            <input name="file" class="widget file-upload-widget form-control" type="file"
                                   data-options="hidePreview"/>
                        </div>
                        <div class="form-group mime-type">
                            <cpn:text tagName="label" tagClass="control-label" value="Mime Type" i18n="true"/>
                            <input name="mimeType" type="text" class="form-control"/>
                        </div>
                    </div>

                    <div class="modal-footer buttons">
                        <button type="button" class="btn btn-default cancel"
                                data-dismiss="modal">${cpn:i18n(slingRequest,'Cancel')}</button>
                        <button type="submit" class="btn btn-primary upload">${cpn:i18n(slingRequest,'Upload')}</button>
                    </div>
                </cpn:form>
            </div>
        </div>
    </div>
</cpn:component>