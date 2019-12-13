<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<cpn:component id="model" type="com.composum.assets.commons.widget.FolderModel">
    <div class="composum-assets-navigator-browse_subfolders">
        <c:forEach items="${model.subfolders}" var="subfolder">
            <div class="composum-assets-navigator-browse_subfolders-item panel panel-default accordion-item widget">
                <div class="panel-heading">
                    <div class="head-actions btn-toolbar">
                        <div class="btn-group btn-group-sm" role="group">
                            <button type="button" class="composum-assets-navigator_drill-down fa fa-folder-open-o text-muted btn btn-default"
                                    title="${cpn:i18n(slingRequest,'Drill Down')}" data-path="${subfolder.path}"><i
                                    class="fa fa-chevron-down fa-stack-1x"></i><cpn:text
                                    value="Drill Down" tagName="span" class="label" i18n="true"/></button>
                        </div>
                    </div>
                    <h4 class="panel-title">
                        <a data-toggle="collapse" href="#sub_${subfolder.domId}"
                           aria-controls="sub_${subfolder.domId}"
                           aria-expanded="true">${cpn:text(subfolder.name)}</a>
                    </h4>
                </div>
                <div id="sub_${subfolder.domId}" role="tabpanel"
                     class="composum-assets-navigator-browse_subfolder panel-collapse collapse in">
                    <div class="panel-body">
                        <div class="panel-group">
                            <c:set var="model" value="${subfolder}" scope="request"/>
                            <sling:include path="${cpn:path(subfolder.path)}"
                                           resourceType="composum/assets/manager/components/navigator/content"/>
                        </div>
                    </div>
                </div>
            </div>
        </c:forEach>
    </div>
</cpn:component>