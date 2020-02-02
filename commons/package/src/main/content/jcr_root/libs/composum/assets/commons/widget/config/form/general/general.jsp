<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<cpn:component var="model" type="com.composum.assets.commons.widget.ConfigModel" scope="request">
    <div class="row" style="align-items: flex-start;">
        <div class="col col-xs-8">
            <div class="row">
                <div class="col col-xs-12">
                    <div class="form-group">
                        <input type="hidden" name="jcr:title@Delete" value="true"/>
                        <label class="control-label">${cpn:i18n(slingRequest,'Title')}</label>
                        <input name="jcr:title" value="${model.title}"
                               type="text" data-rules="blank"
                               class="widget text-field-widget form-control"/>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col col-xs-6">
                    <div class="form-group">
                        <input type="hidden" name="file_jpg_quality@Delete" value="true"/>
                        <label class="control-label">${cpn:i18n(slingRequest,'JPEG Quality')}</label>
                        <input name="file_jpg_quality" value="${model.handle.property.file_jpg_quality}"
                               type="text" placeholder="${model.handle.inherited.file_jpg_quality}"
                               class="widget text-field-widget form-control"
                               data-rules="blank" data-pattern="^[0-9]+(\.[0-9]+)?$"/>
                    </div>
                </div>
            </div>
        </div>
        <div class="col col-xs-4">
            <div class="composum-assets-widget-config-form_category widget multi-form-widget form-group">
                <input type="hidden" name="category@Delete"/>
                <input type="hidden" name="category@TypeHint" value="String[]"/>
                <label class="control-label">${cpn:i18n(slingRequest,'Category')}</label>
                <div class="multi-form-content">
                    <c:forEach items="${model.categorySet}" var="category">
                        <div class="multi-form-item">
                            <div class="category_combobox widget combobox-widget input-group">
                                <input name="category" value="${category}" type="text"
                                       class="combobox_input form-control"/>
                                <div class="input-group-btn">
                                    <button type="button" class="btn btn-default dropdown-toggle"
                                            data-toggle="dropdown" aria-haspopup="true"><i
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
    <div class="row">
        <div class="col col-xs-12">
            <div class="form-group">
                <input type="hidden" name="description@Delete" value="true"/>
                <label class="control-label">${cpn:i18n(slingRequest,'Description')}</label>
                <div class="composum-widgets-richtext richtext-widget widget form-control"
                     data-rules="blank">
                            <textarea name="jcr:description" style="height: 120px;"
                                      class="composum-widgets-richtext_value rich-editor">${model.description}</textarea>
                </div>
            </div>
        </div>
    </div>
    <div class="row">
        <div class="col col-xs-12">
            <div class="form-group">
                <input type="hidden" name="example_image_path@Delete" value="true"/>
                <label class="control-label">${cpn:i18n(slingRequest,'Example Image')}</label>
                <div class="input-group widget assetfield-widget composum-assets-widget-assetfield"
                     data-rules="blank" data-root="${model.contentRoot}" data-filter="image">
                    <input name="example_image_path" value="${model.handle.property.example_image_path}"
                           type="text" placeholder="${model.handle.inherited.example_image_path}"
                           class="composum-assets-widget-assetfield_input form-control path-input"/>
                    <span class="composum-assets-widget-assetfield_popup-button input-group-btn"><button
                            class="composum-assets-widget-assetfield_select select btn btn-default fa fa-folder-open-o"
                            type="button"
                            title="${cpn:i18n(slingRequest,'Select the preview image path')}"></button></span>
                </div>
            </div>
        </div>
    </div>
</cpn:component>

