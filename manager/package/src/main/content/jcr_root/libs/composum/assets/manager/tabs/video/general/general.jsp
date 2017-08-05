<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<cpn:component id="video" type="com.composum.assets.commons.handle.VideoAsset">
    <div class="video-detail" data-path="${video.path}">
        <div class="video-toolbar detail-toolbar">
            <div class="btn-group btn-group-sm" role="group">
                <button class="delete fa fa-trash btn btn-default"
                        title="${cpn:i18n(slingRequest,'Delete Image')}"><cpn:text
                        value="Delete" tagName="span" tagClass="label" i18n="true"/></button>
            </div>
            <div class="btn-group btn-group-sm" role="group">
                <button class="reload fa fa-refresh btn btn-default"
                        title="${cpn:i18n(slingRequest,'Reload')}"><cpn:text
                        value="Reload" tagName="span" tagClass="label" i18n="true"/></button>
            </div>
        </div>
        <div class="video-frame ${video.videoCSS}">
            <div class="video-background">
                <video class="video-player" controls>
                    <source type="${video.mimeType}" src="${video.path}"/>
                </video>
            </div>
        </div>
    </div>
</cpn:component>