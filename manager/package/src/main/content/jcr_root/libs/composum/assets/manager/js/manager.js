/**
 *
 *
 */
(function () {
    'use strict';
    core.namespace('composum.assets.manager');

    (function (manager, assets, core) {

        manager.const = _.extend(manager.const || {}, {
            general: {
                rootPath: '/content'
            }
        });

        assets.current = {};

        assets.getCurrentPath = function () {
            return assets.current ? assets.current.path : undefined;
        };

        assets.setCurrentPath = function (path, callback) {
            if (!assets.current || assets.current.path !== path) {
                if (path) {
                    core.getJson(assets.buildUrl('/bin/cpm/assets/assets.tree.json', core.encodePath(path)),
                        undefined, undefined,
                        _.bind(function (result) {
                            assets.current = {
                                path: path,
                                node: result.responseJSON,
                                viewUrl: assets.buildUrl('/bin/assets.view.html', core.encodePath(path)),
                                nodeUrl: assets.buildUrl('/bin/assets.html', core.encodePath(path))
                            };
                            core.console.getProfile().set('assets', 'current', path);
                            if (history.replaceState) {
                                history.replaceState(assets.current.path, name, assets.current.nodeUrl);
                            }
                            $(document).trigger("path:selected", [path]);
                            if (_.isFunction(callback)) {
                                callback();
                            }
                        }, this));
                } else {
                    assets.current = undefined;
                    $(document).trigger("path:selected", [path]);
                }
            }
        };

        assets.buildUrl = function (uri, suffix) {
            var url = new core.SlingUrl(uri);
            if (suffix) {
                url.suffix = suffix;
            }
            var filter = core.console.getProfile().get('assets', 'filter', undefined);
            if (filter) {
                url.parameters['filter'] = filter;
            }
            return url.build();
        };

        assets.Manager = core.components.SplitView.extend({

            initialize: function (options) {
                core.components.SplitView.prototype.initialize.apply(this, [options]);
                $(document).on('path:select', _.bind(this.onPathSelect, this));
                $(document).on('path:selected', _.bind(this.onPathSelected, this));
                $(document).on('path:changed', _.bind(this.onPathChanged, this));
                core.unauthorizedDelegate = core.console.authorize;
            },

            onPathSelect: function (event, path) {
                if (!path) {
                    path = event.data.path;
                }
                assets.setCurrentPath(path);
            },

            onPathSelected: function (event, path) {
                assets.tree.selectNode(path, _.bind(function (path) {
                    assets.treeActions.refreshNodeState();
                }, this));
            },

            onPathChanged: function (event, path) {
                assets.current = undefined;
                this.onPathSelected(event, path);
            }
        });

        assets.manager = core.getView('#assets', assets.Manager);

        assets.Tree = core.console.Tree.extend({

            nodeIdPrefix: 'AM_',

            getProfileId: function () {
                return 'assets'
            },

            initializeFilter: function () {
                this.rootPath = manager.const.general.rootPath;
                this.filter = core.console.getProfile().get('assets', 'filter', undefined);
                $(document).on('filter:changed.AssetsManagerTree', _.bind(function (event, filter) {
                    this.setFilter(filter);
                }, this));
            },

            dataUrlForPath: function (path) {
                return assets.buildUrl('/bin/cpm/assets/assets.tree.json', path);
            },

            refreshNodeState: function ($node, node) {
                var result = core.console.Tree.prototype.refreshNodeState.apply(this, [$node, node]);
                if (node.original.contentType === 'assetconfig') {
                    $node.removeClass('intermediate').addClass('assetconfig');
                }
                return result;
            }
        });

        assets.tree = core.getView('#assets-tree', assets.Tree);

        assets.TreeActions = core.console.TreeActions.extend({

            initialize: function (options) {
                this.tree = assets.tree;
                core.console.TreeActions.prototype.initialize.apply(this, [options]);
                this.$browserLink = this.$('a.browser');
                this.setBrowserLink();
                this.$('button.create-asset').on('click', _.bind(this.createAsset, this));
                this.$('button.create-folder').on('click', _.bind(this.createFolder, this));
                this.filter = core.getWidget(this.$el,
                    '.composum-assets-widget-filter', assets.widgets.AssetFilterWidget);
                if (this.filter) {
                    this.filter.$el.on('change.AssetsManager', _.bind(function () {
                        var filter = this.filter.getValue();
                        core.console.getProfile().set('assets', 'filter', filter);
                        $(document).trigger("filter:changed", [filter]);
                    }, this));
                    this.filter.setValue(core.console.getProfile().get('assets', 'filter', undefined));
                }
                $(document).on('path:selected', _.bind(this.setBrowserLink, this));
            },

            // @Override
            getCurrent: function () {
                return assets.current;
            },

            // @Override
            getCurrentPath: function () {
                return assets.getCurrentPath();
            },

            /**
             * @override core.console.TreeActions unused - disabled
             */
            setFilter: function () {
            },

            setBrowserLink: function () {
                this.$browserLink.attr('href', core.getContextUrl('/bin/browser.html' + this.getCurrentPath()));
            },

            createAsset: function (event, path, callback) {
                if (event) {
                    event.preventDefault();
                }
                if (!path) {
                    path = this.getCurrentPath();
                }
                if (path) {
                    core.getJson('/bin/cpm/assets/assets.tree.json' + path, undefined, undefined,
                        _.bind(function (result) {
                            var data = result.responseJSON;
                            if (data.type.indexOf('folder') < 0) {
                                path = core.getParentPath(data.path);
                            }
                            this.openCreateAssetDialog(path, callback);
                        }, this));
                } else {
                    this.openCreateAssetDialog(undefined, callback)
                }
                return false;
            },

            openCreateAssetDialog: function (parentPath, callback) {
                var dialog = assets.getAssetUploadDialog();
                dialog.show(_.bind(function () {
                    if (parentPath) {
                        dialog.initParentPath(parentPath);
                    }
                }, this), _.bind(function () {
                    if (_.isFunction(callback)) {
                        callback.call(this, parentPath);
                    }
                }, this));
            },

            createFolder: function (event, path, callback) {
                if (event) {
                    event.preventDefault();
                }
                if (!path) {
                    path = this.getCurrentPath();
                }
                var dialog = assets.getCreateFolderDialog();
                dialog.show(_.bind(function () {
                    if (path) {
                        dialog.initParentPath(path);
                    }
                }, this), _.bind(function () {
                    if (_.isFunction(callback)) {
                        callback.call(this, path);
                    }
                }, this));
                return false;
            }
        });

        assets.treeActions = core.getView('.tree-actions', assets.TreeActions);

        //
        // detail view (console)
        //

        assets.detailViewTabTypes = [{
            selector: '> .config-detail',
            tabType: manager.ConfigTab
        }, {
            selector: '> .folder-detail',
            tabType: manager.FolderTab
        }, {
            selector: '> .asset-original',
            tabType: manager.AssetOriginalsTab
        }, {
            selector: '> .asset-renditions',
            tabType: manager.AssetRenditionsTab
        }, {
            selector: '> .image-detail',
            tabType: manager.ImageTab
        }, {
            selector: '> .preview-detail',
            tabType: manager.PreviewTab
        }, {
            // the fallback to the basic implementation as a default rule
            selector: '> div',
            tabType: core.console.DetailTab
        }];

        /**
         * the node view (node detail) which controls the node view tabs
         */
        assets.DetailView = core.console.DetailView.extend({

            getProfileId: function () {
                return 'assets';
            },

            getCurrentPath: function () {
                return assets.current ? assets.current.path : undefined;
            },

            getViewUri: function () {
                return assets.current.viewUrl;
            },

            getTabUri: function (name) {
                return '/bin/assets.tab.' + name + '.html';
            },

            getTabTypes: function () {
                return assets.detailViewTabTypes;
            },

            initialize: function (options) {
                core.console.DetailView.prototype.initialize.apply(this, [options]);
            }
        });

        assets.detailView = core.getView('#assets-view', assets.DetailView);

        /**
         * the assets query view which is embedding a navigator search widget
         */
        assets.QueryView = Backbone.View.extend({

            initialize: function (options) {
                this.search = core.getWidget(this.$el,
                    '.' + assets.navigator.const.search.css.base, assets.navigator.SearchWidget, {
                        rootPath: manager.const.general.rootPath,
                        filter: core.console.getProfile().get('assets', 'filter', undefined)
                    });
                this.search.navigator = this;
                $(document).on('filter:changed.AssetsManagerQuery', _.bind(function (event, filter) {
                    this.search.setFilter(filter);
                }, this));
                this.search.$el.off('change.QueryView').on('change.QueryView', _.bind(this.onSelect, this));
            },

            onSelect: function (event) {
                if (event) {
                    event.preventDefault();
                }
                var path = this.search.getValue();
                $(document).trigger("path:selected", [path]);
                return false;
            },

            getCurrentFolder: function (callback) {
                var u = assets.navigator.const.url;
                core.getJson(u.commons + u._folder + assets.getCurrentPath(), _.bind(function (data) {
                    if (_.isFunction(callback)) {
                        callback(data.result.folder);
                    }
                }, this));
            }
        });

        assets.queryView = core.getView('#assets-query', assets.QueryView);

    })(composum.assets.manager, composum.assets, core);

})();
