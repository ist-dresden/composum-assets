<%@page session="false" pageEncoding="utf-8"%>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2"%>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<sling:defineObjects/>
<div id="path-select-dialog" class="dialog modal fade" role="dialog" aria-labelledby="Select Repository Path"
     aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content form-panel">
            <form>
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                            aria-hidden="true">&times;</span></button>
                    <h4 class="modal-title" data-default="Select Repository Path">Select Repository Path</h4>
                </div>
                <div class="modal-body">
                    <div class="messages">
                        <div class="alert"></div>
                    </div>

                    <sling:include resourceType="composum/assets/commons/widgets/asset/select"/>
                </div>

                <div class="modal-footer buttons">
                    <button type="button" class="btn btn-default cancel" data-dismiss="modal">Cancel</button>
                    <button type="button" class="btn btn-primary select">Select</button>
                </div>
            </form>
        </div>
    </div>
</div>
