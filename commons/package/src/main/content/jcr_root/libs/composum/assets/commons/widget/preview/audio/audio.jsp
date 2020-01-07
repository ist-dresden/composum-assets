<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<sling:defineObjects/>
<cpn:component var="model" type="com.composum.assets.commons.widget.Thumbnail">
    <div class="thumbnail-file ${model.iconCss}">
        <audio class="composum-assets-widget-preview_file_audio" src="${model.url}" controls="controls"></audio>
    </div>
</cpn:component>
