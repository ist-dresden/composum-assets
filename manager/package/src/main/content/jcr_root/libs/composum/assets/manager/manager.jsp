<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<cpn:component id="manager" type="com.composum.assets.manager.view.ManagerBean" scope="request">
    <html data-context-path="${slingRequest.contextPath}">
    <head>
        <sling:call script="/libs/composum/nodes/console/page/head.jsp"/>
        <cpn:clientlib type="css" category="composum.assets.manager"/>
    </head>
    <body id="assets" class="console left-open top-open assets-preview-mode-dark">
    <div id="ui">
        <sling:include resourceType="composum/nodes/console/components/navbar"/>
        <div id="content-wrapper">
            <div id="split-view-horizontal-split" class="split-pane horizontal-split fixed-left">
                <div class="split-pane-component left-pane">
                    <div>
                        <div class="tree-panel">
                            <div id="assets-tree" data-selected="${manager.path}">
                            </div>
                        </div>
                        <div class="tree-actions action-bar btn-toolbar" role="toolbar">
                            <div class="align-left">
                                <div class="btn-group btn-group-sm" role="group">
                                    <a href="#" class="browser fa fa-wrench btn btn-default" title="Show in Browser"
                                       target="browser"><span class="label">Browser</span></a>
                                    <button type="button" class="refresh fa fa-refresh btn btn-default"
                                            title="Refresh tree view"><span class="label">Refresh</span></button>
                                </div>
                                <div class="btn-group btn-group-sm" role="group">
                                    <button type="button" class="create-folder fa fa-folder-o btn btn-default"
                                            title="Create a new Folder"><i
                                            class="fa fa-plus fa-stack-1x text-muted"></i><span class="label">Create Folder</span>
                                    </button>
                                    <button type="button" class="create-asset fa fa-plus btn btn-default"
                                            title="Create a new Asset"><span class="label">Create Asset</span></button>
                                    <button type="button" class="delete fa fa-minus btn btn-default"
                                            title="Delete selected node"><span class="label">Delete</span></button>
                                </div>
                                <div class="btn-group btn-group-sm" role="group">
                                    <button type="button" class="copy fa fa-copy btn btn-default"
                                            title="Copy selecto node to clipboard"><span class="label">Copy</span>
                                    </button>
                                    <button type="button" class="paste fa fa-paste btn btn-default"
                                            title="Paste node from clipboard into the selected node"><span
                                            class="label">Paste</span></button>
                                </div>
                            </div>
                            <div class="align-right">
                                <div class="btn-group btn-group-sm" role="group">
                                    <sling:include resourceType="composum/assets/commons/widget/filter/dropdown"
                                                   addSelectors="dropup"/>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="split-pane-divider split-pane-divider-main-horizontal"></div>
                <div class="split-pane-component right-pane">
                    <div id="split-view-vertical-split" class="split-pane vertical-split fixed-top">
                        <div class="split-pane-component top-pane">
                            <div id="assets-query">
                                <sling:include resourceType="composum/assets/manager/query"/>
                            </div>
                        </div>
                        <div class="split-pane-divider split-pane-divider-main-vertical"></div>
                        <div class="split-pane-component bottom-pane">
                            <div id="assets-view">
                                <sling:include resourceType="composum/assets/manager/view"/>
                            </div>
                            <div class="close-top"><a href="#" class="fa fa-angle-double-up"
                                                      title="Collapse top panel"></a></div>
                        </div>
                        <div class="open-top"><a href="#" class="fa fa-angle-double-down" title="Restore top panel"></a>
                        </div>
                    </div>
                    <div class="close-left"><a href="#" class="fa fa-angle-double-left" title="Collapse left panel"></a>
                    </div>
                </div>
                <div class="open-left"><a href="#" class="fa fa-angle-double-right" title="Restore left panel"></a>
                </div>
            </div>
        </div>
    </div>
    <cpn:clientlib type="js" category="composum.assets.manager"/>
    <sling:include resourceType="composum/nodes/console/components/tryLogin"/>
    </body>
    </html>
</cpn:component>
