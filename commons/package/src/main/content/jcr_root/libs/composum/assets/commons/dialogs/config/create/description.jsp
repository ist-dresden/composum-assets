<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<cpn:component var="model" type="com.composum.assets.commons.widget.ConfigModel" scope="request">
    <div class="row">
        <div class="col col-xs-12">
            <div class="form-group">
                <label class="control-label">${cpn:i18n(slingRequest,'Description')}</label>
                <div class="composum-widgets-richtext richtext-widget widget form-control"
                     data-rules="blank">
                                    <textarea name="jcr:description" style="height: 120px;"
                                              class="composum-widgets-richtext_value rich-editor"></textarea>
                </div>
            </div>
        </div>
    </div>
</cpn:component>
