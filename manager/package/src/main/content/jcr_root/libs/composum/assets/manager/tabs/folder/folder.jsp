<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<sling:defineObjects/>
<cpn:component id="folder" type="com.composum.assets.commons.widget.FolderModel">
    <div class="detail-panel folder" data-path="${folder.path}">
        <div class="detail-tabs action-bar btn-toolbar" role="toolbar">
            <div class="btn-group btn-group-sm" role="group">
                <a class="browse fa fa-sitemap btn btn-default" href="#general" data-group="general"
                   title="${cpn:i18n(slingRequest,'Browse View')}"><cpn:text
                        value="small" tagName="span" class="label" i18n="true"/></a>
                <a class="config fa fa-sliders btn btn-default" href="#config" data-group="config"
                   title="${cpn:i18n(slingRequest,'Assets Configuration')}"><cpn:text
                        value="small" tagName="span" class="label" i18n="true"/></a>
             </div>
            <%--
            <div class="btn-group btn-group-sm" role="group">
                <button class="config transform fa fa-sliders btn btn-default"
                        title="${cpn:i18n(slingRequest,'Create Asset Configuration')}"><i
                        class="fa fa-magic fa-stack-1x text-danger"></i><cpn:text
                        value="Configuration" tagName="span" class="label" i18n="true"/></button>
            </div>
            --%>
        </div>
        <div class="detail-content">
        </div>
    </div>
</cpn:component>
