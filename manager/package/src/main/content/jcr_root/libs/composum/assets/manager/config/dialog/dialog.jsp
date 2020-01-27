<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<div id="asset-config-dialog" class="dialog modal fade" role="dialog" aria-labelledby="Edit Asset Configuration"
     aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <cpn:form classes="widget-form default form-action_Sling-POST" action="" method="POST">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                            aria-hidden="true">&times;</span></button>
                    <h4 class="modal-title"></h4>
                    <h5 class="modal-subtitle"></h5>
                </div>

                <div class="modal-body">
                    <div class="row">
                        <div class="col-lg-12 col-ms-12 col-sm-12 col-xs-12 column image-view">
                            <sling:include resourceType="composum/assets/manager/config/dialog/preview"/>
                        </div>
                        <div class="col-lg-12 col-ms-12 col-sm-12 col-xs-12 column config-form">
                            <sling:include resourceType="composum/assets/manager/config/dialog/form"/>
                        </div>
                    </div>
                </div>

                <div class="modal-footer buttons">
                    <button type="button" class="btn btn-default cancel" data-dismiss="modal">Cancel</button>
                    <button type="button" class="btn btn-primary save">Save</button>
                </div>
            </cpn:form>
        </div>
    </div>
</div>
