/**
 *
 *
 */
(function () {
    'use strict';
    CPM.namespace('assets.navigator');

    (function (navigator, assets, components) {

        navigator.const = _.extend(navigator.const || {}, {
            css: {
                base: 'composum-assets-widget-navigator',
                _toggleTree: '_toggle-tree',
                _treePanel: '_tree-panel',
                _tree: '_tree',
                _content: '_content',
                _goUp: '_go-up',
                _drillDown: '_drill-down',
                _link: '_asset-link',
                _type: '_view-type',
                _left: '_left',
                _right: '_right'
            },
            url: {
                commons: '/bin/cpm/assets/commons',
                base: '/bin/cpm/assets/assets',
                _reload: '.reload.html',
                _folder: '.folder.json'
            },
            browse: {
                css: {
                    base: 'composum-assets-widget-navigator-browse'
                },
                type: 'composum/assets/commons/widget/navigator/browse'
            },
            search: {
                css: {
                    base: 'composum-assets-widget-navigator-search',
                    _clear: '_clear',
                    _term: '_term',
                    _exec: '_exec',
                    _toggle: '_open-root',
                    _root: '_root',
                    _folder: '_folder-as-root'
                },
                type: 'composum/assets/commons/widget/navigator/search'
            },
            tree: {
                css: {
                    base: 'composum-assets-widget-navigator-tree',
                    _tree: '_tree'
                },
                type: 'composum/assets/commons/widget/navigator/tree'
            }
        });

        assets.profile = assets.profile || new CPM.core.LocalProfile('composum.assets');

        navigator.AbstractNavWidget = widgets.Widget.extend({

            initialize: function (options) {
                widgets.Widget.prototype.initialize.call(this, options);
                this.data = {
                    path: options.path || this.$el.data('path'),
                    rootPath: options.rootPath || this.$el.data('root'),
                    filter: options.filter || this.$el.data('filter')
                };
                var eventId = 'asset_nav_' + this.getWidgetKey();
                $(document)
                    .off('path:selected.' + eventId).on('path:selected.' + eventId, _.bind(this.pathSelected, this));
            },

            /**
             * @abstract
             */
            getWidgetKey: function () {
            },

            pathSelected: function (event, targetPath) {
                this.setValue(targetPath);
            },

            getRootPath: function () {
                return this.data.rootPath || '/content';
            },

            setRootPath: function (rootPath) {
                var currentRoot = this.data.rootPath;
                this.data.rootPath = rootPath;
                if (currentRoot !== rootPath) {
                    if (_.isFunction(this.reload)) {
                        this.reload();
                    }
                }
            },

            getFilter: function () {
                return this.data.filter;
            },

            setFilter: function (filter) {
                var currentFilter = this.data.filter;
                this.data.filter = filter;
                if (currentFilter !== filter) {
                    if (_.isFunction(this.reload)) {
                        this.reload();
                    }
                }
            },

            getValue: function () {
                return this.adjustValue(this.data.path);
            },

            setValue: function (targetPath, triggerChange, suppressReload) {
                if (this.data.path !== targetPath) {
                    this.data.path = this.adjustValue(targetPath);
                    this.valueChanged(suppressReload);
                    if (triggerChange) {
                        this.$el.trigger('change', [this.data.path]);
                    }
                }
            },

            adjustValue: function (value) {
                if (value && this.data.rootPath &&
                    value !== this.data.rootPath && value.indexOf(this.data.rootPath + '/') !== 0) {
                    value = this.data.rootPath;
                }
                return value;
            },

            /**
             * @default
             */
            valueChanged: function (suppressReload) {
            }
        });

        navigator.AbstractNavPanel = navigator.AbstractNavWidget.extend({

            initialize: function (options) {
                navigator.AbstractNavWidget.prototype.initialize.call(this, options);
                this.viewType = assets.profile.get(this.getWidgetKey(), 'viewType', 'small');
                var eventId = 'Navigation' + this.getWidgetKey();
                $(document)
                    .off('path:selected.' + eventId).on('path:selected.' + eventId, _.bind(this.pathSelected, this));
            },

            initContent: function (element) {
                var c = navigator.const.css;
                var $element = element ? $(element) : this.$el;
                widgets.setUp($element[0]);
                $element.find('.' + c.base + c._type).click(_.bind(this.changeViewType, this));
                $element.find('.' + c.base + c._link).click(_.bind(this.select, this));
                this.valueChanged(true);
                return $element;
            },

            /**
             * @abstract
             */
            getResourceType: function () {
            },

            /**
             * @override
             */
            valueChanged: function (suppressReload) {
                var c = navigator.const.css;
                this.$('.' + c.base + c._link).removeClass('selected');
                this.$('.' + c.base + c._link + '[data-path="' + this.data.path + '"]').addClass('selected');
            },

            /**
             * load the current 'this.data.path'...
             */
            reload: function () {
                var u = navigator.const.url;
                var path = this.getValue() || this.getRootPath();
                var url = u.base + u._reload + (path ? core.encodePath(path) : '');
                var resourceType = this.$el.data('resource-type') || this.getResourceType();
                var params = {
                    resourceType: resourceType,
                    selectors: 'reload.' + this.viewType
                };
                var filter = this.getFilter();
                if (filter) {
                    params['filter'] = filter;
                }
                core.ajaxGet(url, {
                    dataType: 'html',
                    data: _.extend(params, this.extendLoad())
                }, _.bind(function (content) {
                    this.$el.html(content);
                    this.initContent();
                }, this));
            },

            extendLoad: function () {
                return {};
            },

            changeViewType: function (event) {
                this.viewType = $(event.currentTarget).data('view-type');
                assets.profile.set(this.getWidgetKey(), 'viewType', this.viewType);
                return this.reload();
            },

            select: function (event) {
                return this.pathEvent(event, $(event.currentTarget).data('path'), true);
            },

            pathEvent: function (event, targetPath, suppressReload) {
                if (event) {
                    event.preventDefault();
                }
                if (targetPath && targetPath !== '/') {
                    this.setValue(targetPath, true, suppressReload);
                }
                return false;
            }
        });

        navigator.BrowseWidget = navigator.AbstractNavPanel.extend({

            initialize: function (options) {
                navigator.AbstractNavPanel.prototype.initialize.call(this, options);
                this.reload(); // the initial element is empty to load with view type from profile
            },

            initContent: function (element) {
                var $element = navigator.AbstractNavPanel.prototype.initContent.call(this, element);
                var c = navigator.const.css;
                $element.find('.' + c.base + c._goUp).click(_.bind(this.goUp, this));
                $element.find('.' + c.base + c._drillDown).click(_.bind(this.drillDown, this));
            },

            getWidgetKey: function (event) {
                return 'browse';
            },

            getResourceType: function () {
                return navigator.const.browse.type;
            },

            goUp: function (event) {
                var path = $(event.currentTarget).data('path');
                return this.pathEvent(event, core.getParentPath(path ? path : this.data.path));
            },

            drillDown: function (event) {
                return this.pathEvent(event, $(event.currentTarget).data('path'));
            },

            /**
             * @override
             */
            valueChanged: function (suppressReload) {
                if (!suppressReload) {
                    this.reload();
                } else {
                    navigator.AbstractNavPanel.prototype.valueChanged.call(this, suppressReload);
                }
            }
        });

        widgets.register('.widget.' + navigator.const.browse.css.base, navigator.BrowseWidget);

        navigator.SearchWidget = navigator.AbstractNavPanel.extend({

            initialize: function (options) {
                navigator.AbstractNavPanel.prototype.initialize.call(this, options);
                this.setSearchRoot(this.$el.data('search-root') ||
                    assets.profile.get(this.getWidgetKey(), 'root', undefined), true);
                this.data.term = assets.profile.get(this.getWidgetKey(), 'term', '');
                this.reload(); // the initial element is empty to load with view type from profile
            },

            initContent: function (element) {
                var $element = navigator.AbstractNavPanel.prototype.initContent.call(this, element);
                var c = navigator.const.search.css;
                this.$term = $element.find('.' + c.base + c._term);
                this.$term.keydown(_.bind(this.onSearchTermKey, this));
                $element.find('.' + c.base + c._exec).click(_.bind(this.execSearch, this));
                $element.find('.' + c.base + c._clear).click(_.bind(function () {
                    this.setSearchTerm('', true);
                }, this));
                if (assets.profile.get(this.getWidgetKey(), 'rootOpen', false)) {
                    this.$el.addClass('root-open');
                }
                $element.find('.' + c.base + c._toggle).click(_.bind(this.toggleRootInput, this));
                this.rootPath = core.getWidget(this.$el, '.' + c.base + c._root, components.PathWidget);
                if (this.data.rootPath) {
                    this.rootPath.setRootPath(this.data.rootPath);
                }
                this.rootPath.setValue(this.data.searchRoot);
                this.rootPath.$el.on('change.AssetSearch', _.bind(this.searchRootChanged, this));
                this.rootPath.$input.keydown(_.bind(this.onSearchRootKey, this));
                $element.find('.' + c.base + c._folder).click(_.bind(this.useCurrentFolder, this));
            },

            getWidgetKey: function (event) {
                return 'search';
            },

            getResourceType: function () {
                return navigator.const.search.type;
            },

            /**
             * @extends navigator.AbstractNavWidget
             */
            setRootPath: function (rootPath) {
                navigator.AbstractNavWidget.prototype.setRootPath.call(this, root);
                this.rootPath.setRootPath(rootPath);
            },

            searchRootChanged: function (event) {
                event.preventDefault();
                this.setSearchRoot(this.rootPath.getValue());
            },

            onSearchRootKey: function (event) {
                if (event.which === 13) {
                    this.searchRootChanged(event);
                    return false;
                }
            },

            getSearchRoot: function () {
                var searchRoot = this.data.searchRoot;
                if (this.data.rootPath) {
                    if (!searchRoot ||
                        (searchRoot !== this.data.rootPath && searchRoot.indexOf(this.data.rootPath + '/') !== 0)) {
                        searchRoot = this.data.rootPath;
                    }
                }
                return searchRoot;
            },

            setSearchRoot: function (rootPath, suppressReload) {
                if (this.data.searchRoot !== rootPath) {
                    this.data.searchRoot = rootPath;
                    assets.profile.set(this.getWidgetKey(), 'root', rootPath);
                    if (!suppressReload) {
                        this.reload();
                    }
                }
            },

            useCurrentFolder: function (event) {
                if (this.navigator) {
                    this.navigator.getCurrentFolder(_.bind(function (folder) {
                        if (folder) {
                            this.setSearchRoot(folder);
                        }
                    }, this));
                }
            },

            toggleRootInput: function () {
                this.$el.toggleClass('root-open');
                assets.profile.set(this.getWidgetKey(), 'rootOpen', this.$el.hasClass('root-open'));
            },

            setSearchTerm: function (searchTerm, forceReload) {
                if (forceReload || this.data.term !== searchTerm) {
                    this.data.term = searchTerm;
                    assets.profile.set(this.getWidgetKey(), 'term', searchTerm);
                    this.reload();
                }
            },

            onSearchTermKey: function (event) {
                if (event.which === 13) {
                    this.execSearch(event);
                    return false;
                }
            },

            execSearch: function (event) {
                event.preventDefault();
                this.setSearchTerm(this.$term.val(), true);
            },

            /**
             * @override
             */
            extendLoad: function () {
                var params = {
                    term: this.data.term
                };
                var searchRoot = this.getSearchRoot();
                if (searchRoot) {
                    params.root = searchRoot;
                }
                return params;
            }
        });

        widgets.register('.widget.' + navigator.const.search.css.base, navigator.SearchWidget);

        navigator.Tree = components.Tree.extend({

            nodeIdPrefix: 'AN_',

            getProfileId: function () {
                return 'assets'
            },

            initializeFilter: function () {
            },

            /**
             * @override components.Tree
             */
            dataUrlForPath: function (path) {
                var url = new core.SlingUrl('/bin/cpm/assets/assets.tree.json' + path);
                if (this.filter) {
                    url.parameters['filter'] = this.filter;
                }
                return url.build();
            },

            refreshNodeState: function ($node, node) {
                var result = components.Tree.prototype.refreshNodeState.apply(this, [$node, node]);
                if (node.original.contentType === 'assetconfig') {
                    $node.removeClass('intermediate').addClass('assetconfig');
                }
                return result;
            },

            onNodeSelected: function (path, node) {
                if (this.widget) {
                    this.widget.setValue(path, true);
                }
            }
        });

        navigator.TreeWidget = navigator.AbstractNavWidget.extend({

            initialize: function (options) {
                var c = navigator.const.tree.css;
                navigator.AbstractNavWidget.prototype.initialize.call(this, options);
                this.tree = core.getWidget(this.$el, '.' + c.base + c._tree, navigator.Tree);
                this.tree.widget = this;
                if (this.data.rootPath) {
                    this.tree.setRootPath(this.data.rootPath);
                }
            },

            getWidgetKey: function (event) {
                return 'tree';
            },

            /**
             * @extends navigator.AbstractNavWidget
             */
            setRootPath: function (rootPath) {
                navigator.AbstractNavWidget.prototype.setRootPath.call(this, root);
                if (this.tree) {
                    var currentRoot = this.tree.getRootPath();
                    this.tree.setRootPath(rootPath, rootPath !== currentRoot);
                }
            },

            /**
             * @extends navigator.AbstractNavWidget
             */
            setFilter: function (filter) {
                navigator.AbstractNavWidget.prototype.setFilter.call(this, filter);
                if (this.tree) {
                    this.tree.setFilter(filter);
                }
            },

            /**
             * @extends navigator.AbstractNavWidget
             */
            setValue: function (targetPath, triggerChange, suppressReload) {
                if (!this.busy) {
                    try {
                        this.busy = true;
                        if (this.data.path !== targetPath) {
                            navigator.AbstractNavWidget.prototype.setValue.call(this,
                                targetPath, triggerChange, suppressReload);
                            if (targetPath && this.tree.getSelectedPath() !== targetPath) {
                                this.tree.selectNode(targetPath, _.bind(function () {
                                }, this, true));
                            }
                        }
                    } finally {
                        this.busy = false;
                    }
                }
            }
        });

        widgets.register('.widget.' + navigator.const.tree.css.base, navigator.TreeWidget);

        navigator.NavigatorWidget = navigator.AbstractNavWidget.extend({

            initialize: function (options) {
                var c = navigator.const.css;
                navigator.AbstractNavWidget.prototype.initialize.call(this, options);
                options = _.extend({
                    path: this.data.path,
                    rootPath: this.data.rootPath,
                    filter: this.data.filter
                }, options);
                this.browse = core.getWidget(this.$el,
                    '.' + navigator.const.browse.css.base, navigator.BrowseWidget, options);
                this.search = core.getWidget(this.$el,
                    '.' + navigator.const.search.css.base, navigator.SearchWidget, options);
                this.tree = core.getWidget(this.$el,
                    '.' + navigator.const.tree.css.base, navigator.TreeWidget, options);
                this.setFilterWidget(core.getWidget(this.$el,
                    '.' + assets.widgets.const.assetfilter.css.base, assets.widgets.AssetFilterWidget));
                var eventId = 'asset_nav_' + this.getWidgetKey();
                if (this.browse) {
                    this.browse.navigator = this;
                    this.browse.$el.off('change.' + eventId).on('change.' + eventId, _.bind(function () {
                        this.setValue(this.browse.getValue(), true);
                    }, this));
                }
                if (this.search) {
                    this.search.navigator = this;
                    this.search.$el.off('change.' + eventId).on('change.' + eventId, _.bind(function () {
                        this.setValue(this.search.getValue(), true);
                    }, this));
                }
                if (this.tree) {
                    this.tree.navigator = this;
                    this.tree.$el.off('change.' + eventId).on('change.' + eventId, _.bind(function () {
                        this.setValue(this.tree.getValue(), true);
                    }, this));
                }
                this.$left = this.$('.' + c.base + c._left);
                this.$right = this.$('.' + c.base + c._right);
                this.$toggleTree = this.$('.' + c.base + c._toggleTree);
                this.$treePanel = this.$('.' + c.base + c._treePanel);
                this.$tree = this.$('.' + c.base + c._tree);
                this.$toggleTree.find('.toggle-handle').click(_.bind(this.toggleTreePanel, this));
                this.$('.tabbed-tab').on('shown.bs.tab', _.bind(function (event) {
                    this.currentTab = $(event.currentTarget).data('key');
                    assets.profile.set(this.getWidgetKey(), 'tab', this.currentTab);
                }, this));
                this.$('[data-key="' + assets.profile.get(this.getWidgetKey(), 'tab', 'browse') + '"]').tab('show');
                this.treeMode = assets.profile.get(this.getWidgetKey(), 'tree', 'tab');
                if (this.treeMode === 'panel') {
                    this.openTreePanel();
                }
            },

            getWidgetKey: function (event) {
                return 'widget';
            },

            getCurrentFolder: function (callback) {
                if (this.currentFolder) {
                    if (_.isFunction(callback)) {
                        callback(this.currentFolder);
                    }
                    return this.currentFolder;
                } else {
                    var u = navigator.const.url;
                    core.getJson(u.commons + u._folder + core.encodePath(this.data.path),
                        _.bind(function (data) {
                            this.currentFolder = data.result.folder;
                            if (_.isFunction(callback)) {
                                callback(this.currentFolder);
                            }
                        }, this));
                }
            },

            setFilterWidget: function (widget) {
                if (this.filter) {
                    this.filter.$el.off('change.AssetsNavigator');
                }
                this.filter = widget;
                if (this.filter) {
                    this.setFilter(this.data.filter || this.filter.getValue());
                    if (this.$el.data('filter') === this.getFilter()) {
                        this.disableFilterWidget();
                    } else {
                        this.filter.$el.on('change.AssetsNavigator', _.bind(function () {
                            this.setFilter(this.filter.getValue());
                        }, this));
                    }
                }
            },

            disableFilterWidget: function () {
                if (this.filter) {
                    this.filter.$input.prop('disabled', true);
                }
            },

            /**
             * @extends navigator.AbstractNavWidget
             */
            setRootPath: function (root) {
                navigator.AbstractNavWidget.prototype.setRootPath.call(this, root);
                if (this.browse) {
                    this.browse.setRootPath(root);
                }
                if (this.search) {
                    this.search.setRootPath(root);
                }
                if (this.tree) {
                    this.tree.setRootPath(root);
                }
            },

            /**
             * @extends navigator.AbstractNavWidget
             */
            setFilter: function (filter) {
                navigator.AbstractNavWidget.prototype.setFilter.call(this, filter);
                if (this.filter) {
                    this.filter.setValue(filter, false);
                }
                if (this.browse) {
                    this.browse.setFilter(filter);
                }
                if (this.search) {
                    this.search.setFilter(filter);
                }
                if (this.tree) {
                    this.tree.setFilter(filter);
                }
            },

            setValue: function (targetPath, triggerChange, suppressReload) {
                this.currentFolder = undefined;
                navigator.AbstractNavWidget.prototype.setValue.call(this, targetPath, triggerChange, suppressReload);
                if (this.browse) {
                    this.browse.setValue(targetPath);
                }
                if (this.search) {
                    this.search.setValue(targetPath);
                }
                if (this.tree) {
                    this.tree.setValue(targetPath);
                }
            },

            openTreePanel: function () {
                if (this.currentTab === 'tree') {
                    this.$('[data-key="browse"]').tab('show');
                }
                this.$treePanel.append(this.tree.$el);
                assets.profile.set(this.getWidgetKey(), 'tree', this.treeMode = 'panel');
                this.$el.addClass('tree-mode-panel');
                this.$el.trigger('treepanel', ['opened']);
            },

            closeTreePanel: function () {
                this.$el.removeClass('tree-mode-panel');
                this.$tree.append(this.tree.$el);
                assets.profile.set(this.getWidgetKey(), 'tree', this.treeMode = 'tab');
                this.$el.trigger('treepanel', ['closed']);
            },

            toggleTreePanel: function () {
                if (this.treeMode === 'panel') {
                    this.closeTreePanel();
                } else {
                    this.openTreePanel();
                }
            }
        });

        widgets.register('.widget.' + navigator.const.css.base, navigator.NavigatorWidget);

    })(CPM.assets.navigator, CPM.assets, CPM.core.components);

})();
