<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<cpn:component var="model" type="com.composum.assets.commons.widget.ConfigModel" scope="request">
    <div class="composum-assets-widget-config-form">
        <form class="composum-assets-widget-config-form_form widget-form"
              data-example="${model.handle.inherited.example_image_path}">
            <div class="composum-assets-widget-config-form_content">
                <ul class="composum-assets-widget-config-form_tabs nav nav-tabs" role="tablist">
                    <li role="presentation"><a href="#${model.domId}_general" aria-controls="${model.domId}_general"
                                               role="tab" data-toggle="tab"
                                               data-key="general">${cpn:i18n(slingRequest,'General')}</a></li>
                    <li role="presentation"><a href="#${model.domId}_transform" aria-controls="${model.domId}_transform"
                                               role="tab" data-toggle="tab"
                                               data-key="transform">${cpn:i18n(slingRequest,'Transformation')}</a></li>
                    <li role="presentation"><a href="#${model.domId}_watermark" aria-controls="${model.domId}_watermark"
                                               role="tab" data-toggle="tab"
                                               data-key="watermark">${cpn:i18n(slingRequest,'Watermark')}</a>
                    </li>
                </ul>
                <div class="composum-assets-widget-config-form_panels tab-content">
                    <div id="${model.domId}_general" class="composum-assets-widget-config-form_tab-panel tab-pane"
                         role="tabpanel">
                        <div class="row">
                            <div class="col col-xs-12">
                                <div class="form-group">
                                    <label class="control-label">${cpn:i18n(slingRequest,'Example Image')}</label>
                                    <div class="input-group widget assetfield-widget composum-assets-widget-assetfield"
                                         data-rules="blank" data-root="${model.contentRoot}">
                                        <input name="example_image_path"
                                               type="text" value="${model.example.path}"
                                               placeholder="${model.handle.inherited.example_image_path}"
                                               class="composum-assets-widget-assetfield_input form-control path-input"
                                               <c:if test="${model.disabled}">disabled</c:if>/>
                                        <span class="composum-assets-widget-assetfield_popup-button input-group-btn"><button
                                                class="composum-assets-widget-assetfield_select select btn btn-default fa fa-folder-open-o"
                                                type="button" <c:if test="${model.disabled}">disabled</c:if>
                                                title="${cpn:i18n(slingRequest,'Select the preview image path')}"></button></span>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col col-xs-7">
                                <div class="row">
                                    <div class="col col-xs-6">
                                        <div class="form-group">
                                            <label class="control-label">${cpn:i18n(slingRequest,'JPEG Quality')}</label>
                                            <input name="file_jpg_quality" value="${model.file.quality}"
                                                   placeholder="${model.handle.inherited.file_jpg_quality}"
                                                   <c:if test="${model.disabled}">disabled</c:if>
                                                   class="widget text-field-widget form-control"
                                                   type="text" data-rules="blank"
                                                   data-pattern="^(0\.[0-9]+|1\.0|[0-9]+)$"/>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="col col-xs-5">
                                <div class="widget multi-form-widget form-group">
                                    <input type="hidden" name="categories@Delete"/>
                                    <label class="control-label">${cpn:i18n(slingRequest,'Categories')}</label>
                                    <div class="multi-form-content">
                                        <c:forEach items="${model.categoriesSet}" var="category">
                                            <div class="multi-form-item">
                                                <div class="categories_combobox widget combobox-widget input-group">
                                                    <input name="categories" class="combobox_input form-control"
                                                           type="text" value="${category}"
                                                           <c:if test="${model.disabled}">disabled</c:if>/>
                                                    <div class="input-group-btn">
                                                        <button type="button" class="btn btn-default dropdown-toggle"
                                                                data-toggle="dropdown" aria-haspopup="true"
                                                                <c:if test="${model.disabled}">disabled</c:if>><i
                                                                class="fa fa-caret-down"></i></button>
                                                        <ul class="dropdown-menu dropdown-menu-right">
                                                            <li data-value="default"><a href="#">default</a></li>
                                                            <li data-value="original"><a href="#">original</a></li>
                                                        </ul>
                                                    </div>
                                                </div>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div id="${model.domId}_transform" class="composum-assets-widget-config-form_tab-panel tab-pane"
                         role="tabpanel">
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
                                    <input class="widget text-field-widget form-control"
                                           name="size_width" value="${model.size.width}"
                                           placeholder="${model.handle.inherited.size_width}"
                                           <c:if test="${model.disabled}">disabled</c:if>
                                           type="text" data-rules="blank" data-pattern="^[0-9]+$"/>
                                </div>
                                <div class="col col-xs-3">
                                    <div>${cpn:i18n(slingRequest,'Height')}</div>
                                    <input class="widget text-field-widget form-control"
                                           name="size_height" value="${model.size.height}"
                                           placeholder="${model.handle.inherited.size_height}"
                                           <c:if test="${model.disabled}">disabled</c:if>
                                           type="text" data-rules="blank" data-pattern="^[0-9]+$"/>
                                </div>
                                <div class="col col-xs-3">
                                    <div>${cpn:i18n(slingRequest,'Aspect Ratio')}</div>
                                    <input class="widget text-field-widget form-control"
                                           name="size_aspectRatio" value="${model.size.aspectRatioRule}"
                                           placeholder="${model.handle.inherited.size_aspectRatio}"
                                           <c:if test="${model.disabled}">disabled</c:if>
                                           type="text" data-rules="blank"
                                           data-pattern="^([0-9]+:[0-9]+|[0-9]+(\.[0-9]+)?)$"
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
                                    <input class="widget text-field-widget form-control"
                                           name="crop_vertical" value="${model.crop.vertical}"
                                           placeholder="${model.handle.inherited.crop_vertical}"
                                           <c:if test="${model.disabled}">disabled</c:if>
                                           type="text" data-rules="blank" data-pattern="^(0\.[0-9]+|1\.0|[0-9]+)$"/>
                                </div>
                                <div class="col col-xs-3">
                                    <div>${cpn:i18n(slingRequest,'Horizontal')}</div>
                                    <input class="widget text-field-widget form-control"
                                           name="crop_horizontal" value="${model.crop.horizontal}"
                                           placeholder="${model.handle.inherited.crop_horizontal}"
                                           <c:if test="${model.disabled}">disabled</c:if>
                                           type="text" data-rules="blank" data-pattern="^(0\.[0-9]+|1\.0|[0-9]+)$"/>
                                </div>
                                <div class="col col-xs-3">
                                    <div>${cpn:i18n(slingRequest,'Scale')}</div>
                                    <input class="widget text-field-widget form-control"
                                           name="crop_scale" value="${model.crop.scale}"
                                           placeholder="${model.handle.inherited.crop_scale}"
                                           <c:if test="${model.disabled}">disabled</c:if>
                                           type="text" data-rules="blank" data-pattern="^(0\.[0-9]+|1\.0)$"/>
                                </div>
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="row">
                                <div class="col col-xs-3">
                                    <label class="control-label">${cpn:i18n(slingRequest,'More')}</label>
                                </div>
                                <div class="col col-xs-9">
                                    <span>${cpn:i18n(slingRequest,'short description for the other transformations')}</span>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col col-xs-3">
                                    <div>${cpn:i18n(slingRequest,'Blur Factor')}</div>
                                    <input class="widget text-field-widget form-control"
                                           name="transformation_blur_factor" value="${model.blur.factorStr}"
                                           placeholder="${model.handle.inherited.transformation_blur_factor}"
                                           <c:if test="${model.disabled}">disabled</c:if>
                                           type="text" data-rules="blank" data-pattern="^[0-9]+$"/>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div id="${model.domId}_watermark" class="composum-assets-widget-config-form_tab-panel tab-pane"
                         role="tabpanel">
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
                                           placeholder="${model.handle.inherited.watermark_text}"
                                           <c:if test="${model.disabled}">disabled</c:if>
                                           class="widget text-field-widget form-control"
                                           type="text" data-rules="blank"/>
                                </div>
                                <div class="col col-xs-3">
                                    <div>${cpn:i18n(slingRequest,'Color')}</div>
                                    <div class="widget textfield-widget input-group" data-rules="blank">
                                        <input name="watermark_color" class="form-control"
                                               type="text" value="${model.watermark.colorCode}"
                                               placeholder="${model.handle.inherited.watermark_color}"
                                               <c:if test="${model.disabled}">disabled</c:if>/>
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
                                           placeholder="${model.handle.inherited.watermark_horizontal}"
                                           <c:if test="${model.disabled}">disabled</c:if>
                                           class="widget text-widget form-control"
                                           type="text" data-rules="blank"
                                           data-pattern="^(0\.[0-9]+|1\.0|[+-]?[0-9]+)$"/>
                                </div>
                                <div class="col col-xs-3">
                                    <div>${cpn:i18n(slingRequest,'Vertical')}</div>
                                    <input name="watermark_vertical" value="${model.watermark.vertical}"
                                           placeholder="${model.handle.inherited.watermark_vertical}"
                                           <c:if test="${model.disabled}">disabled</c:if>
                                           class="widget text-widget form-control"
                                           type="text" data-rules="blank"
                                           data-pattern="^(0\.[0-9]+|1\.0|[+-]?[0-9]+)$"/>
                                </div>
                                <div class="col col-xs-3">
                                </div>
                                <div class="col col-xs-3">
                                    <div>${cpn:i18n(slingRequest,'Opacity')}</div>
                                    <input name="watermark_alpha" value="${model.watermark.alpha}"
                                           placeholder="${model.handle.inherited.watermark_alpha}"
                                           <c:if test="${model.disabled}">disabled</c:if>
                                           class="widget text-field-widget form-control"
                                           type="text" data-rules="blank" data-pattern="^(0\.[0-9]+|1\.0)$"/>
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
                                           placeholder="${model.handle.inherited.watermark_font_family}"
                                           <c:if test="${model.disabled}">disabled</c:if>
                                           class="widget text-field-widget form-control"
                                           type="text" data-rules="blank"/>
                                </div>
                                <div class="col col-xs-2">
                                    <div>${cpn:i18n(slingRequest,'Size')}</div>
                                    <input name="watermark_font_size" value="${model.watermark.font.size}"
                                           placeholder="${model.handle.inherited.watermark_font_size}"
                                           <c:if test="${model.disabled}">disabled</c:if>
                                           class="widget text-field-widget form-control"
                                           type="text" data-rules="blank" data-pattern="^[0-9]+$"/>
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
                                                   value="${model.watermark.font.bold}"
                                                   <c:if test="${model.disabled}">disabled</c:if>/>${cpn:i18n(slingRequest,'Bold')}
                                        </label>
                                        <input type="hidden" class="sling-post-type-hint"
                                               name="watermark_font_italic@TypeHint" value="Boolean"/>
                                        <input type="hidden" class="sling-post-delete-hint"
                                               name="watermark_font_italic@Delete" value="true"/>
                                        <label class="checkbox-inline">
                                            <input name="watermark_font_italic" type="checkbox"
                                                   value="${model.watermark.font.italic}"
                                                   <c:if test="${model.disabled}">disabled</c:if>/>${cpn:i18n(slingRequest,'Italic')}
                                        </label>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </form>
    </div>
</cpn:component>

