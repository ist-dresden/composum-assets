<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<sling:defineObjects/>
<div class="composum-assets-dialog_asset-select composum-assets-dialog dialog modal fade" role="dialog"
     aria-labelledby="${cpn:i18n(slingRequest,'Select Asset Resource')}" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content form-panel">
            <form>
                <sling:call script="form.jsp"/>
            </form>
        </div>
    </div>
</div>
