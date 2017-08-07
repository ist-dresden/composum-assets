<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<div class="asset config form">
    <div class="accordion">
        <div class="panel-group" id="asset-config-form-aspects">
            <div class="panel panel-default accordion-item widget">
                <div class="panel-heading">
                    <h2 class="panel-title">
                        <a data-toggle="collapse" data-parent="#asset-config-form-aspects"
                           data-child="#asset-config-form-aspect-file">${cpn:i18n(slingRequest,'Categories / File / Example')}</a>
                        <div class="extension-checkbox widget checkbox-widget">
                            <input type="hidden" class="sling-post-type-hint" name="extension@TypeHint" value="Boolean"/>
                            <input type="hidden" class="sling-post-delete-hint" name="extension@Delete" value="true"/>
                            <label>extension<input name="extension" class="smart" type="checkbox" value="true"/></label>
                        </div>
                    </h2>
                </div>
                <div id="asset-config-form-aspect-file" class="panel-collapse collapse">
                    <div class="panel-body">
                        <div class="row">
                            <div class="col-lg-5 col-md-5 col-sm-5 col-xs-5">
                                <div class="widget multi-form-widget form-group" data-name="categories">
                                    <cpn:text tagName="label" tagClass="control-label" value="Categories"
                                              i18n="true"/>
                                    <input type="hidden" name="categories@TypeHint" value="String[]" />
                                    <div class="multi-form-content">
                                        <div class="multi-form-item">
                                            <input class="widget mixin-type-widget form-control" type="text" name="categories"/>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="col-lg-7 col-md-7 col-sm-7 col-xs-7">
                                <div class="row">
                                    <div class="col-lg-6 col-md-6 col-sm-6 col-xs-6">
                                        <div class="form-group file-jpeg-quality">
                                            <cpn:text tagName="label" tagClass="control-label" value="JPEG Quality"
                                                      i18n="true"/>
                                            <input name="file.jpg.quality" class="widget text-field-widget form-control"
                                                   type="text" data-rules="blank"
                                                   data-pattern="^(0\.[0-9]+|1\.0|[0-9]+)$"/>
                                            <div class="config-path"></div>
                                        </div>
                                    </div>
                                </div>
                                <div class="row">
                                    <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
                                        <div class="form-group">
                                            <cpn:text tagName="label" tagClass="control-label" value="Example Image"
                                                      i18n="true"/>
                                            <div class="input-group widget path-widget" data-rules="blank">
                                                <input name="example.image.path" class="form-control" type="text"/>
                                                <span class="input-group-btn"><button
                                                        class="select btn btn-default" type="button"
                                                        title="${cpn:i18n(slingRequest,'Select Repository Path')}">...</button></span>
                                            </div>
                                            <div class="config-path"></div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="panel panel-default accordion-item widget">
                <div class="panel-heading">
                    <h2 class="panel-title">
                        <a data-toggle="collapse" data-parent="#asset-config-form-aspects"
                           data-child="#asset-config-form-aspect-size">${cpn:i18n(slingRequest,'Size')}</a>
                    </h2>
                </div>
                <div id="asset-config-form-aspect-size" class="panel-collapse collapse in">
                    <div class="panel-body">
                        <div class="row">
                            <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                <div class="form-group size-width">
                                    <cpn:text tagName="label" tagClass="control-label" value="Width"
                                              i18n="true"/>
                                    <input name="size.width" class="widget text-field-widget form-control"
                                           type="text" data-rules="blank"
                                           data-pattern="^[0-9]+$"/>
                                    <div class="config-path"></div>
                                </div>
                            </div>
                            <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                <div class="form-group size-height">
                                    <cpn:text tagName="label" tagClass="control-label" value="Height"
                                              i18n="true"/>
                                    <input name="size.height" class="widget text-field-widget form-control"
                                           type="text" data-rules="blank"
                                           data-pattern="^[0-9]+$"/>
                                    <div class="config-path"></div>
                                </div>
                            </div>
                            <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                <div class="form-group size-aspectRatio">
                                    <cpn:text tagName="label" tagClass="control-label" value="Aspect Ratio"
                                              i18n="true"/>
                                    <input name="size.aspectRatio" class="widget text-field-widget form-control"
                                           type="text" data-rules="blank"
                                           data-pattern="^([0-9]+:[0-9]+|[0-9]+(\.[0-9]+)?)$"
                                           data-pattern-hint="${cpn:i18n(slingRequest,'e.g. 16:9, 4:3, 1.5(2:3)')}"/>
                                    <div class="config-path"></div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="panel panel-default accordion-item widget">
                <div class="panel-heading">
                    <h2 class="panel-title">
                        <a data-toggle="collapse" data-parent="#asset-config-form-aspects"
                           data-child="#asset-config-form-aspect-crop">${cpn:i18n(slingRequest,'Crop')}</a>
                    </h2>
                </div>
                <div id="asset-config-form-aspect-crop" class="panel-collapse collapse">
                    <div class="panel-body">
                        <div class="row">
                            <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                <div class="form-group crop-horizontal">
                                    <cpn:text tagName="label" tagClass="control-label" value="Horizontal"
                                              i18n="true"/>
                                    <input name="crop.horizontal" class="widget text-field-widget form-control"
                                           type="text" data-rules="blank"
                                           data-pattern="^(0\.[0-9]+|1\.0|[0-9]+)$"/>
                                    <div class="config-path"></div>
                                </div>
                            </div>
                            <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                <div class="form-group crop-vertical">
                                    <cpn:text tagName="label" tagClass="control-label" value="Vertical"
                                              i18n="true"/>
                                    <input name="crop.vertical" class="widget text-field-widget form-control"
                                           type="text" data-rules="blank"
                                           data-pattern="^(0\.[0-9]+|1\.0|[0-9]+)$"/>
                                    <div class="config-path"></div>
                                </div>
                            </div>
                            <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                <div class="form-group crop-scale">
                                    <cpn:text tagName="label" tagClass="control-label" value="Scale"
                                              i18n="true"/>
                                    <input name="crop.scale" class="widget text-field-widget form-control"
                                           type="text" data-rules="blank"
                                           data-pattern="^(0\.[0-9]+|1\.0)$"/>
                                    <div class="config-path"></div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="panel panel-default accordion-item widget">
                <div class="panel-heading">
                    <h2 class="panel-title">
                        <a data-toggle="collapse" data-parent="#asset-config-form-aspects"
                           data-child="#asset-config-form-aspect-watermark">${cpn:i18n(slingRequest,'Watermark')}</a>
                    </h2>
                </div>
                <div id="asset-config-form-aspect-watermark" class="panel-collapse collapse">
                    <div class="panel-body">
                        <div class="row">
                            <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
                                <div class="form-group watermark-text">
                                    <cpn:text tagName="label" tagClass="control-label" value="Text"
                                              i18n="true"/>
                                    <input name="watermark.text" class="widget text-field-widget form-control"
                                           type="text" data-rules="blank"
                                           data-pattern="^[0-9]+$"/>
                                    <div class="config-path"></div>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-lg-6 col-md-6 col-sm-6 col-xs-6">
                                <div class="form-group watermark-font-family">
                                    <cpn:text tagName="label" tagClass="control-label" value="Font Family"
                                              i18n="true"/>
                                    <input name="watermark.font.family" class="widget text-field-widget form-control"
                                           type="text" data-rules="blank"/>
                                    <div class="config-path"></div>
                                </div>
                            </div>
                            <div class="col-lg-2 col-md-2 col-sm-2 col-xs-2">
                                <div class="form-group watermark-font-size">
                                    <cpn:text tagName="label" tagClass="control-label" value="Size"
                                              i18n="true"/>
                                    <input name="watermark.font.size" class="widget text-field-widget form-control"
                                           type="text" data-rules="blank"
                                           data-pattern="^[0-9]+$"/>
                                    <div class="config-path"></div>
                                </div>
                            </div>
                            <div class="col-lg-2 col-md-2 col-sm-21 col-xs-2">
                                <div class="form-group watermark-font-bold">
                                    <cpn:text tagName="label" tagClass="control-label" value="bold"
                                              i18n="true"/>
                                    <div class="widget checkbox-widget form-control">
                                        <input type="hidden" class="sling-post-type-hint"
                                               name="watermark.font.bold@TypeHint" value="Boolean"/>
                                        <input type="hidden" class="sling-post-delete-hint"
                                               name="watermark.font.bold@Delete" value="true"/>
                                        <input name="watermark.font.bold" type="checkbox" value="true"/>
                                    </div>
                                    <div class="config-path"></div>
                                </div>
                            </div>
                            <div class="col-lg-2 col-md-2 col-sm-2 col-xs-2">
                                <div class="form-group watermark-font-italic">
                                    <cpn:text tagName="label" tagClass="control-label" value="italic"
                                              i18n="true"/>
                                    <div class="widget checkbox-widget form-control">
                                        <input type="hidden" class="sling-post-type-hint"
                                               name="watermark.font.italic@TypeHint" value="Boolean"/>
                                        <input type="hidden" class="sling-post-delete-hint"
                                               name="watermark.font.italic@Delete" value="true"/>
                                        <input name="watermark.font.italic" type="checkbox" value="true"/>
                                    </div>
                                    <div class="config-path"></div>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-lg-3 col-md-3 col-sm-3 col-xs-3">
                                <div class="form-group watermark-horizontal">
                                    <cpn:text tagName="label" tagClass="control-label" value="Horizontal"
                                              i18n="true"/>
                                    <input name="watermark.horizontal" class="widget text-widget form-control"
                                           type="text" data-rules="blank"
                                           data-pattern="^(0\.[0-9]+|1\.0|[+-]?[0-9]+)$"/>
                                    <div class="config-path"></div>
                                </div>
                            </div>
                            <div class="col-lg-3 col-md-3 col-sm-3 col-xs-3">
                                <div class="form-group watermark-vertical">
                                    <cpn:text tagName="label" tagClass="control-label" value="Vertical"
                                              i18n="true"/>
                                    <input name="watermark.vertical" class="widget text-widget form-control"
                                           type="text" data-rules="blank"
                                           data-pattern="^(0\.[0-9]+|1\.0|[+-]?[0-9]+)$"/>
                                    <div class="config-path"></div>
                                </div>
                            </div>
                            <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                <div class="form-group watermark-color">
                                    <cpn:text tagName="label" tagClass="control-label" value="Color"
                                              i18n="true"/>
                                    <div class="widget colorpicker-widget input-group" data-rules="blank">
                                        <input name="watermark.color" class="form-control" type="text"/>
                                        <span class="input-group-addon"><i></i></span>
                                    </div>
                                    <div class="config-path"></div>
                                </div>
                            </div>
                            <div class="col-lg-2 col-md-2 col-sm-2 col-xs-2">
                                <div class="form-group watermark-alpha">
                                    <cpn:text tagName="label" tagClass="control-label" value="Opacity"
                                              i18n="true"/>
                                    <input name="watermark.alpha" class="widget  text-field-widget form-control"
                                           type="text" data-rules="blank"
                                           data-pattern="^(0\.[0-9]+|1\.0)$"/>
                                    <div class="config-path"></div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="panel panel-default accordion-item widget">
                <div class="panel-heading">
                    <h2 class="panel-title">
                        <a data-toggle="collapse" data-parent="#asset-config-form-aspects"
                           data-child="#asset-config-form-aspect-transform">${cpn:i18n(slingRequest,'Transformation')}</a>
                    </h2>
                </div>
                <div id="asset-config-form-aspect-transform" class="panel-collapse collapse">
                    <div class="panel-body">
                        <div class="row">
                            <div class="col-lg-3 col-md-3 col-sm-3 col-xs-3">
                                <div class="form-group transformation-blur-factor">
                                    <cpn:text tagName="label" tagClass="control-label" value="Blur Factor" i18n="true"/>
                                    <input name="transformation.blur.factor"
                                           class="widget text-field-widget form-control"
                                           type="text" data-rules="blank"
                                           data-pattern="^[0-9]+$"/>
                                    <div class="config-path"></div>
                                </div>
                            </div>
                            <div class="col-lg-3 col-md-3 col-sm-3 col-xs-3">
                                <div class="form-group">
                                </div>
                            </div>
                            <div class="col-lg-3 col-md-3 col-sm-3 col-xs-3">
                                <div class="form-group">
                                </div>
                            </div>
                            <div class="col-lg-3 col-md-3 col-sm-3 col-xs-3">
                                <div class="form-group">
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
