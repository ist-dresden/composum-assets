<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<cpn:component id="finder" type="com.composum.assets.commons.widget.FolderModel">
    <div class="subfolder-panel">
        <c:forEach items="${finder.subfolders}" var="subfolder">
            <div class="panel panel-default accordion-item widget">
                <div class="panel-heading">
                    <div class="head-actions btn-toolbar">
                        <div class="btn-group btn-group-sm" role="group">
                            <button type="button" class="drill-down fa fa-folder-open-o text-muted btn btn-default"
                                    title="${cpn:i18n(slingRequest,'Drill Down')}" data-path="${subfolder.path}"><i
                                    class="fa fa-chevron-down fa-stack-1x"></i><cpn:text
                                    value="Drill Down" tagName="span" class="label" i18n="true"/></button>
                        </div>
                    </div>
                    <h2 class="panel-title">
                        <a data-toggle="collapse" data-parent="#folder-${finder.name}"
                           data-child="#subfolder-${subfolder.name}">${subfolder.name}</a>
                    </h2>
                </div>
                <div id="subfolder-${subfolder.name}" class="panel-collapse collapse in">
                    <div class="panel-body">
                        <div class="panel-group" id="${subfolder.path}">
                            <sling:include path="${subfolder.path}"
                                           resourceType="composum/assets/commons/widgets/asset/browser/subfolder"/>
                        </div>
                    </div>
                </div>
            </div>
        </c:forEach>
    </div>
</cpn:component>