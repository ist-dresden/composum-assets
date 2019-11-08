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
                      action="/bin/cpm/assets/commons.assetConfig.json" enctype="multipart/form-data">
                <input name="_charset_" type="hidden" value="UTF-8"/>

                <div class="modal-header composum-pages-stage-edit-dialog_header">
                    <button type="button" class="composum-pages-stage-edit-dialog_button-close fa fa-close"
                            data-dismiss="modal" aria-label="Close"></button>
                    <cpn:text tagName="h4" class="modal-title composum-pages-stage-edit-dialog_dialog-title"
                              value="Asset Configuration settings" i18n="true"/>
                </div>

                <div class="modal-body composum-pages-stage-edit-dialog_content">
                    <div class="composum-pages-stage-edit-dialog_messages messages">
                        <div class="panel panel-warning hidden">
                            <div class="panel-heading"></div>
                            <div class="panel-body hidden"></div>
                        </div>
                    </div>

                    <h1>ToDo...</h1>
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