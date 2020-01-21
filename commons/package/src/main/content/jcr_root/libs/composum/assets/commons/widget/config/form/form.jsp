<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<cpn:component var="model" type="com.composum.assets.commons.widget.ConfigModel">
    <div class="composum-assets-widget-config-form">
        <form class="composum-assets-widget-config-form_form widget-form">
            <div class="composum-assets-widget-config-form_content">
                <ul class="composum-assets-widget-config-form_tabs nav nav-tabs" role="tablist">
                    <li role="presentation"><a href="#${model.domId}_general" aria-controls="${model.domId}_general"
                                               role="tab" data-toggle="tab"
                                               data-key="general">${cpn:i18n(slingRequest,'General')}</a></li>
                    <li role="presentation"><a href="#${model.domId}_watermark" aria-controls="${model.domId}_watermark"
                                               role="tab" data-toggle="tab"
                                               data-key="watermark">${cpn:i18n(slingRequest,'Watermatk')}</a>
                    </li>
                </ul>
                <div class="composum-assets-widget-config-form_panels tab-content">
                    <div id="${model.domId}_general" class="composum-assets-widget-config-form_tab-panel tab-pane"
                         role="tabpanel">
                        <div class="form-group">
                            <div class="row">
                                <div class="col col-xs-3">
                                    <label>${cpn:i18n(slingRequest,'Size')}</label>
                                </div>
                                <div class="col col-xs-9">
                                    <span>${cpn:i18n(slingRequest,'short description for the size configuration')}</span>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col col-xs-3">
                                    <div>${cpn:i18n(slingRequest,'Width')}</div>
                                    <input class="widget text-field-widget form-control" type="text"
                                           name="size_width" value="${model.size.width}"
                                           placeholder="${model.handle.inherited.size_width}"/>
                                </div>
                                <div class="col col-xs-3">
                                    <div>${cpn:i18n(slingRequest,'Height')}</div>
                                    <input class="widget text-field-widget form-control" type="text"
                                           name="size_height" value="${model.size.height}"
                                           placeholder="${model.handle.inherited.size_height}"/>
                                </div>
                                <div class="col col-xs-3">
                                    <div>${cpn:i18n(slingRequest,'Aspect Ratio')}</div>
                                    <input class="widget text-field-widget form-control" type="text"
                                           name="size_aspectRatio" value="${model.size.aspectRatioRule}"
                                           placeholder="${model.handle.inherited.size_aspectRatio}"/>
                                </div>
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="row">
                                <div class="col col-xs-3">
                                    <label>${cpn:i18n(slingRequest,'Crop')}</label>
                                </div>
                                <div class="col col-xs-9">
                                    <span>${cpn:i18n(slingRequest,'short description for the crop configuration')}</span>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col col-xs-3">
                                    <div>${cpn:i18n(slingRequest,'Vertical')}</div>
                                    <input class="widget text-field-widget form-control" type="text"
                                           name="crop_vertical" value="${model.crop.vertical}"
                                           placeholder="${model.handle.inherited.crop_vertical}"/>
                                </div>
                                <div class="col col-xs-3">
                                    <div>${cpn:i18n(slingRequest,'Horizontal')}</div>
                                    <input class="widget text-field-widget form-control" type="text"
                                           name="crop_horizontal" value="${model.crop.horizontal}"
                                           placeholder="${model.handle.inherited.crop_horizontal}"/>
                                </div>
                                <div class="col col-xs-3">
                                    <div>${cpn:i18n(slingRequest,'Scale')}</div>
                                    <input class="widget text-field-widget form-control" type="text"
                                           name="crop_scale" value="${model.crop.scale}"
                                           placeholder="${model.handle.inherited.crop_scale}"/>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div id="${model.domId}_watermark" class="composum-assets-widget-config-form_tab-panel tab-pane"
                         role="tabpanel">
                        <div class="form-group">
                            <div class="row">
                                <div class="col col-xs-3">
                                    <label>${cpn:i18n(slingRequest,'Font')}</label>
                                </div>
                                <div class="col col-xs-9">
                                    <span>${cpn:i18n(slingRequest,'short description for the font configuration')}</span>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col col-xs-3">
                                    <div>${cpn:i18n(slingRequest,'Width')}</div>
                                    <input class="widget text-field-widget form-control" type="text"
                                           name="size_width" value="${model.size.width}"
                                           placeholder="${model.handle.inherited.size_width}"/>
                                </div>
                                <div class="col col-xs-3">
                                    <div>${cpn:i18n(slingRequest,'Height')}</div>
                                    <input class="widget text-field-widget form-control" type="text"
                                           name="size_height" value="${model.size.height}"
                                           placeholder="${model.handle.inherited.size_height}"/>
                                </div>
                                <div class="col col-xs-3">
                                    <div>${cpn:i18n(slingRequest,'Aspect Ratio')}</div>
                                    <input class="widget text-field-widget form-control" type="text"
                                           name="size_aspectRatio" value="${model.size.aspectRatioRule}"
                                           placeholder="${model.handle.inherited.size_aspectRatio}"/>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </form>
    </div>
</cpn:component>

