<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<cpn:component var="model" type="com.composum.assets.commons.widget.ConfigModel" scope="request">
    <div class="row headline">
        <div class="col col-xs-3">
            <label class="section-label">${cpn:i18n(slingRequest,'Size')}</label>
        </div>
        <div class="col col-xs-9">
            <div class="hint hint-remark">${cpn:i18n(slingRequest,'the renditions size - width or height probably with an aspect ratio')}</div>
        </div>
    </div>
    <div class="row">
        <div class="col col-xs-3">
            <div class="form-group">
                <label class="control-label sublabel">${cpn:i18n(slingRequest,'Width')}</label>
                <input class="widget text-field-widget form-control" disabled
                       name="size_width" value="${model.handle.property.size_width}"
                       type="text" placeholder="${model.handle.inherited.size_width}"
                       data-rules="blank" data-pattern="^[0-9]+$"/>
            </div>
        </div>
        <div class="col col-xs-3">
            <div class="form-group">
                <label class="control-label sublabel">${cpn:i18n(slingRequest,'Height')}</label>
                <input class="widget text-field-widget form-control" disabled
                       name="size_height" value="${model.handle.property.size_height}"
                       type="text" placeholder="${model.handle.inherited.size_height}"
                       data-rules="blank" data-pattern="^[0-9]+$"/>
            </div>
        </div>
        <div class="col col-xs-3">
            <div class="form-group">
                <label class="control-label sublabel">${cpn:i18n(slingRequest,'Aspect Ratio')}</label>
                <input class="widget text-field-widget form-control" disabled
                       name="size_aspectRatio" value="${model.handle.property.size_aspectRatio}"
                       type="text" placeholder="${model.handle.inherited.size_aspectRatio}"
                       data-rules="blank" data-pattern="^([0-9]+:[0-9]+|[0-9]+(\.[0-9]+)?)$"
                       data-pattern-hint="${cpn:i18n(slingRequest,'e.g. 16:9, 4:3, 1.5(2:3)')}"/>
            </div>
        </div>
    </div>
    <div class="row headline">
        <div class="col col-xs-3">
            <label class="section-label">${cpn:i18n(slingRequest,'Crop')}</label>
        </div>
        <div class="col col-xs-9">
            <div class="hint hint-remark">${cpn:i18n(slingRequest,'the position and scale of the renditions part of the image')}</div>
        </div>
    </div>
    <div class="row">
        <div class="col col-xs-3">
            <div class="form-group">
                <label class="control-label sublabel">${cpn:i18n(slingRequest,'Vertical')}</label>
                <input class="widget text-field-widget form-control" disabled
                       name="crop_vertical" value="${model.handle.property.crop_vertical}"
                       type="text" placeholder="${model.handle.inherited.crop_vertical}"
                       data-rules="blank" data-pattern="^(0\.[0-9]+|1\.0|[0-9]+)$"/>
            </div>
        </div>
        <div class="col col-xs-3">
            <div class="form-group">
                <label class="control-label sublabel">${cpn:i18n(slingRequest,'Horizontal')}</label>
                <input class="widget text-field-widget form-control" disabled
                       name="crop_horizontal" value="${model.handle.property.crop_horizontal}"
                       type="text" placeholder="${model.handle.inherited.crop_horizontal}"
                       data-rules="blank" data-pattern="^(0\.[0-9]+|1\.0|[0-9]+)$"/>
            </div>
        </div>
        <div class="col col-xs-3">
            <div class="form-group">
                <label class="control-label sublabel">${cpn:i18n(slingRequest,'Scale')}</label>
                <input class="widget text-field-widget form-control" disabled
                       name="crop_scale" value="${model.handle.property.crop_scale}"
                       type="text" placeholder="${model.handle.inherited.crop_scale}"
                       data-rules="blank" data-pattern="^(0\.[0-9]+|1\.0)$"/>
            </div>
        </div>
    </div>
    <div class="row headline">
        <div class="col col-xs-3">
            <label class="section-label">${cpn:i18n(slingRequest,'Other')}</label>
        </div>
        <div class="col col-xs-9">
            <div class="hint hint-remark">${cpn:i18n(slingRequest,'settings for special transformations')}</div>
        </div>
    </div>
    <div class="row">
        <div class="col col-xs-3">
            <div class="form-group">
                <label class="control-label sublabel">${cpn:i18n(slingRequest,'Blur Factor')}</label>
                <input class="widget text-field-widget form-control" disabled
                       name="transformation_blur_factor" value="${model.handle.property.transformation_blur_factor}"
                       type="text" placeholder="${model.handle.inherited.transformation_blur_factor}"
                       data-rules="blank" data-pattern="^[0-9]+$"/>
            </div>
        </div>
    </div>
</cpn:component>

