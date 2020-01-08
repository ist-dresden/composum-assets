<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<cpn:component id="configBean" type="com.composum.assets.manager.config.RenditionConfigBean">
    <div class="rendition config panel panel-default accordion-item widget">
        <div class="panel-heading config-edit_header" data-name="${configBean.name}"
             data-path="${configBean.path}" data-type="rendition">
            <h2 class="panel-title">
                <input class="config-edit_selector" type="checkbox"/>
                <a class="config-edit_title" data-toggle="collapse" data-parent="#${configBean.config.variation.path}"
                   data-child="#${configBean.config.path}">${configBean.config.name}
                    <em>${configBean.config.nonDefaultCategories}</em></a>
                <div class="default-checkbox">
                    <label class="${configBean.config.defaultConfig ? 'checked' : ''}">default<input
                            class="smart"
                            type="checkbox"${configBean.config.defaultConfig ? ' checked="true" disabled="true"' : ''}/></label>
                </div>
            </h2>
        </div>
        <div id="${configBean.config.path}" class="panel-collapse collapse ${configBean.tabCssClass}">
            <div class="panel-body">
                <sling:call script="overview.jsp"/>
            </div>
        </div>
    </div>
</cpn:component>
