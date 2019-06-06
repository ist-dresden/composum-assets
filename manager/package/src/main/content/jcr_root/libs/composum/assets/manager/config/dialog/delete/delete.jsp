<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<div id="delete-asset-config-dialog" class="dialog modal fade" role="dialog" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content form-panel default">
            <cpn:form classes="widget-form" action="/bin/cpm/assets/assets.deleteConfig.json" method="POST">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                            aria-hidden="true">&times;</span></button>
                    <cpn:text tagName="h4" class="modal-title asset" value="Delete Configuration"
                              i18n="true"/>
                </div>
                <div class="modal-body">
                    <div class="messages">
                        <div class="alert"></div>
                    </div>
                    <input name="_charset_" type="hidden" value="UTF-8"/>
                    <input name="path" type="hidden" value="UTF-8"/>

                    <div class="row">
                        <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
                            <div class="form-group">
                                <cpn:text tagName="label" class="control-label" value="Configuration Path"
                                          i18n="true"/>
                                <input class="config-path widget text-field-widget form-control" type="text"
                                       disabled="true"/>
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-lg-6 col-md-6 col-sm-6 col-xs-6">
                            <div class="form-group">
                                <cpn:text tagName="label" class="control-label" value="Context" i18n="true"/>
                                <input class="config-context widget text-field-widget form-control" type="text"
                                       disabled="true"/>
                            </div>
                        </div>
                        <div class="col-lg-6 col-md-6 col-sm-6 col-xs-6">
                            <div class="form-group ">
                                <cpn:text tagName="label" class="control-label" value="Name" i18n="true"/>
                                <input class="config-name widget text-field-widget form-control" type="text"
                                       disabled="true"/>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="modal-footer buttons">
                    <button type="button" class="btn btn-default cancel" data-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-danger delete">Delete</button>
                </div>
            </cpn:form>
        </div>
    </div>
</div>
