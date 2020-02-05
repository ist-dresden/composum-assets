<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<cpn:component id="manager" type="com.composum.assets.manager.view.ManagerBean" scope="request">
    <div id="create-folder-dialog" class="dialog modal fade" role="dialog" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content form-panel default">
                <cpn:form classes="widget-form" action="/bin/cpm/nodes/node.create.json" method="POST">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                                aria-hidden="true">&times;</span></button>
                        <h4 class="modal-title">Create Folder</h4>
                    </div>
                    <div class="modal-body">
                        <div class="messages">
                            <div class="alert"></div>
                        </div>

                        <input name="_charset_" type="hidden" value="UTF-8"/>
                        <input name="path" type="hidden"/>
                        <input name="type" type="hidden"/>
                        <div class="row">
                            <div class="col-lg-8 col-md-8 col-sm-8 col-xs-8">
                                <div class="form-group">
                                    <label class="control-label">Folder Name</label>
                                    <input name="name" class="widget text-field-widget form-control" type="text"
                                           placeholder="enter folder name" data-rules="mandatory"/>
                                </div>
                            </div>
                            <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                <div class="form-group checkbox">
                                    <label class="control-label"><input name="ordered" class="widget checkbox-widget"
                                                                        type="checkbox"/>Ordered Folder</label>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="modal-footer buttons">
                        <button type="button" class="btn btn-default cancel" data-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-primary create">Create</button>
                    </div>
                </cpn:form>
            </div>
        </div>
    </div>
</cpn:component>