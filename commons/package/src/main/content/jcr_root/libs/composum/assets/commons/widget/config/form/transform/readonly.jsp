<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<cpn:component var="model" type="com.composum.assets.commons.widget.ConfigModel" scope="request">
    <div class="form-group">
        <div class="row">
            <div class="col col-xs-3">
                <label class="control-label">${cpn:i18n(slingRequest,'Size')}</label>
            </div>
            <div class="col col-xs-9">
                <span>${cpn:i18n(slingRequest,'short description for the size configuration')}</span>
            </div>
        </div>
        <div class="row">
            <div class="col col-xs-3">
                <div>${cpn:i18n(slingRequest,'Width')}</div>
                <input class="widget text-field-widget form-control" disabled
                       name="size_width" value="${model.size.width}"
                       type="text" placeholder="${model.handle.inherited.size_width}"
                       data-rules="blank" data-pattern="^[0-9]+$"/>
            </div>
            <div class="col col-xs-3">
                <div>${cpn:i18n(slingRequest,'Height')}</div>
                <input class="widget text-field-widget form-control" disabled
                       name="size_height" value="${model.size.height}"
                       type="text" placeholder="${model.handle.inherited.size_height}"
                       data-rules="blank" data-pattern="^[0-9]+$"/>
            </div>
            <div class="col col-xs-3">
                <div>${cpn:i18n(slingRequest,'Aspect Ratio')}</div>
                <input class="widget text-field-widget form-control" disabled
                       name="size_aspectRatio" value="${model.size.aspectRatioRule}"
                       type="text" placeholder="${model.handle.inherited.size_aspectRatio}"
                       data-rules="blank" data-pattern="^([0-9]+:[0-9]+|[0-9]+(\.[0-9]+)?)$"
                       data-pattern-hint="${cpn:i18n(slingRequest,'e.g. 16:9, 4:3, 1.5(2:3)')}"/>
            </div>
        </div>
    </div>
    <div class="form-group">
        <div class="row">
            <div class="col col-xs-3">
                <label class="control-label">${cpn:i18n(slingRequest,'Crop')}</label>
            </div>
            <div class="col col-xs-9">
                <span>${cpn:i18n(slingRequest,'short description for the crop configuration')}</span>
            </div>
        </div>
        <div class="row">
            <div class="col col-xs-3">
                <div>${cpn:i18n(slingRequest,'Vertical')}</div>
                <input class="widget text-field-widget form-control" disabled
                       name="crop_vertical" value="${model.crop.vertical}"
                       type="text" placeholder="${model.handle.inherited.crop_vertical}"
                       data-rules="blank" data-pattern="^(0\.[0-9]+|1\.0|[0-9]+)$"/>
            </div>
            <div class="col col-xs-3">
                <div>${cpn:i18n(slingRequest,'Horizontal')}</div>
                <input class="widget text-field-widget form-control" disabled
                       name="crop_horizontal" value="${model.crop.horizontal}"
                       type="text" placeholder="${model.handle.inherited.crop_horizontal}"
                       data-rules="blank" data-pattern="^(0\.[0-9]+|1\.0|[0-9]+)$"/>
            </div>
            <div class="col col-xs-3">
                <div>${cpn:i18n(slingRequest,'Scale')}</div>
                <input class="widget text-field-widget form-control" disabled
                       name="crop_scale" value="${model.crop.scale}"
                       type="text" placeholder="${model.handle.inherited.crop_scale}"
                       data-rules="blank" data-pattern="^(0\.[0-9]+|1\.0)$"/>
            </div>
        </div>
    </div>
    <div class="form-group">
        <div class="row">
            <div class="col col-xs-3">
                <label class="control-label">${cpn:i18n(slingRequest,'Other')}</label>
            </div>
            <div class="col col-xs-9">
                <span>${cpn:i18n(slingRequest,'short description for the other transformations')}</span>
            </div>
        </div>
        <div class="row">
            <div class="col col-xs-3">
                <div>${cpn:i18n(slingRequest,'Blur Factor')}</div>
                <input class="widget text-field-widget form-control" disabled
                       name="transformation_blur_factor" value="${model.blur.factorStr}"
                       type="text" placeholder="${model.handle.inherited.transformation_blur_factor}"
                       data-rules="blank" data-pattern="^[0-9]+$"/>
            </div>
        </div>
    </div>
</cpn:component>

