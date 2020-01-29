<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<cpn:component var="model" type="com.composum.assets.commons.widget.ConfigModel" scope="request">
    <div class="form-group">
        <div class="row">
            <div class="col col-xs-3">
                <label class="control-label">${cpn:i18n(slingRequest,'Text')}</label>
            </div>
            <div class="col col-xs-9">
                <span>${cpn:i18n(slingRequest,'short description for the watermark text')}</span>
            </div>
        </div>
        <div class="row">
            <div class="col col-xs-9">
                <div>${cpn:i18n(slingRequest,'Text')}</div>
                <input name="watermark_text" value="${model.watermark.text}"
                       type="text" placeholder="${model.handle.inherited.watermark_text}"
                       class="widget text-field-widget form-control"
                       data-rules="blank"/>
            </div>
            <div class="col col-xs-3">
                <div>${cpn:i18n(slingRequest,'Color')}</div>
                <div class="widget textfield-widget input-group" data-rules="blank">
                    <input name="watermark_color" value="${model.watermark.colorCode}"
                           type="text" placeholder="${model.handle.inherited.watermark_color}"
                           class="form-control"/>
                    <span class="input-group-addon"><i></i></span>
                </div>
            </div>
        </div>
    </div>
    <div class="form-group">
        <div class="row">
            <div class="col col-xs-3">
                <label class="control-label">${cpn:i18n(slingRequest,'Position')}</label>
            </div>
            <div class="col col-xs-9">
                <span>${cpn:i18n(slingRequest,'short description for the font configuration')}</span>
            </div>
        </div>
        <div class="row">
            <div class="col col-xs-3">
                <div>${cpn:i18n(slingRequest,'Horizontal')}</div>
                <input name="watermark_horizontal" value="${model.watermark.horizontal}"
                       type="text" placeholder="${model.handle.inherited.watermark_horizontal}"
                       class="widget text-widget form-control"
                       data-rules="blank" data-pattern="^(0\.[0-9]+|1\.0|[+-]?[0-9]+)$"/>
            </div>
            <div class="col col-xs-3">
                <div>${cpn:i18n(slingRequest,'Vertical')}</div>
                <input name="watermark_vertical" value="${model.watermark.vertical}"
                       type="text" placeholder="${model.handle.inherited.watermark_vertical}"
                       class="widget text-widget form-control"
                       data-rules="blank" data-pattern="^(0\.[0-9]+|1\.0|[+-]?[0-9]+)$"/>
            </div>
            <div class="col col-xs-3">
            </div>
            <div class="col col-xs-3">
                <div>${cpn:i18n(slingRequest,'Opacity')}</div>
                <input name="watermark_alpha" value="${model.watermark.alpha}"
                       type="text" placeholder="${model.handle.inherited.watermark_alpha}"
                       class="widget text-field-widget form-control"
                       data-rules="blank" data-pattern="^(0\.[0-9]+|1\.0)$"/>
            </div>
        </div>
    </div>
    <div class="form-group">
        <div class="row">
            <div class="col col-xs-3">
                <label class="control-label">${cpn:i18n(slingRequest,'Font')}</label>
            </div>
            <div class="col col-xs-9">
                <span>${cpn:i18n(slingRequest,'short description for the font configuration')}</span>
            </div>
        </div>
        <div class="row">
            <div class="col col-xs-7">
                <div>${cpn:i18n(slingRequest,'Family')}</div>
                <input name="watermark_font_family" value="${model.watermark.font.family}"
                       ptype="text" placeholder="${model.handle.inherited.watermark_font_family}"
                       class="widget text-field-widget form-control"
                       data-rules="blank"/>
            </div>
            <div class="col col-xs-2">
                <div>${cpn:i18n(slingRequest,'Size')}</div>
                <input name="watermark_font_size" value="${model.watermark.font.size}"
                       type="text" placeholder="${model.handle.inherited.watermark_font_size}"
                       class="widget text-field-widget form-control"
                       data-rules="blank" data-pattern="^[0-9]+$"/>
            </div>
            <div class="col col-xs-3">
                <div>${cpn:i18n(slingRequest,'Style')}</div>
                <div class="widget checkbox-widget form-control">
                    <input type="hidden" class="sling-post-type-hint"
                           name="watermark_font_bold@TypeHint" value="Boolean"/>
                    <input type="hidden" class="sling-post-delete-hint"
                           name="watermark_font_bold@Delete" value="true"/>
                    <label class="checkbox-inline">
                        <input name="watermark_font_bold" type="checkbox"
                               <c:if test="${model.watermark.font.bold}">checked</c:if>/>${cpn:i18n(slingRequest,'Bold')}
                    </label>
                    <input type="hidden" class="sling-post-type-hint"
                           name="watermark_font_italic@TypeHint" value="Boolean"/>
                    <input type="hidden" class="sling-post-delete-hint"
                           name="watermark_font_italic@Delete" value="true"/>
                    <label class="checkbox-inline">
                        <input name="watermark_font_italic" type="checkbox"
                               <c:if test="${model.watermark.font.italic}">checked</c:if>/>${cpn:i18n(slingRequest,'Italic')}
                    </label>
                </div>
            </div>
        </div>
    </div>
</cpn:component>

