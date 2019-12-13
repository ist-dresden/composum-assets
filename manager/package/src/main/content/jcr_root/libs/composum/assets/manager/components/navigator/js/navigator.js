/**
 *
 *
 */
(function (window) {
    'use strict';
    window.composum = window.composum || {};
    window.composum.assets = window.composum.assets || {};
    window.composum.assets.navigator = window.composum.assets.navigator || {};

    (function (navigator, assets, widgets, core) {

        navigator.const = _.extend(navigator.const || {}, {
            navigation: {
                css: {
                    base: 'composum-assets-navigator',
                    _content: '_content',
                    _goUp: '_go-up',
                    _drillDown: '_drill-down',
                    _link: '_asset-link',
                    _type: '_view-type'
                },
                url: {
                    base: '/bin/cpm/assets/assets',
                    _reload: '.reload.html'
                },
                browse: {
                    css: {
                        base: 'composum-assets-navigator-browse'
                    },
                    type: 'composum/assets/manager/components/navigator/browse'
                },
                search: {
                    css: {
                        base: 'composum-assets-navigator-search'
                    },
                    type: 'composum/assets/manager/components/navigator/search'
                },
                tree: {
                    css: {
                        base: 'composum-assets-navigator-tree'
                    },
                    type: 'composum/assets/manager/components/navigator/tree'
                }
            }
        });

        assets.profile = assets.profile || new core.LocalProfile('composum.assets');

        navigator.AbstractNavWidget = widgets.Widget.extend({

            initialize: function (options) {
                widgets.Widget.prototype.initialize.call(this, options);
                this.data = {
                    path: this.$el.data('path')
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

            getValue: function () {
                return this.data.path;
            },

            setValue: function (targetPath, triggerChange, suppressReload) {
                if (this.data.path !== targetPath) {
                    this.data.path = targetPath || '/content';
                    this.valueChanged(suppressReload);
                    if (triggerChange) {
                        this.$el.trigger('change');
                    }
                }
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
                this.data = {
                    path: this.$el.data('path')
                };
                this.viewType = assets.profile.get(this.getWidgetKey(), 'viewType', 'small');
                var eventId = 'Navigation' + this.getWidgetKey();
                $(document)
                    .off('path:selected.' + eventId).on('path:selected.' + eventId, _.bind(this.pathSelected, this));
                this.reload(); // the initial element is empty to load with view type from profile
            },

            initContent: function (element) {
                var c = navigator.const.navigation.css;
                var $element = element ? $(element) : this.$el;
                window.widgets.setUp($element[0]);
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
                var c = navigator.const.navigation.css;
                this.$('.' + c.base + c._link).removeClass('selected');
                this.$('.' + c.base + c._link + '[data-path="' + this.data.path + '"]').addClass('selected');
            },

            /**
             * load the current 'this.data.path'...
             */
            reload: function () {
                var u = navigator.const.navigation.url;
                var url = u.base + u._reload + this.data.path;
                var resourceType = this.$el.data('resource-type') || this.getResourceType();
                core.ajaxGet(url, {
                    dataType: 'html',
                    data: {
                        resourceType: resourceType,
                        selectors: 'reload.' + this.viewType
                    }
                }, _.bind(function (content) {
                    this.$el.html(content);
                    this.initContent();
                }, this));
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

            initContent: function (element) {
                var $element = navigator.AbstractNavPanel.prototype.initContent.call(this, element);
                var c = navigator.const.navigation.css;
                $element.find('.' + c.base + c._goUp).click(_.bind(this.goUp, this));
                $element.find('.' + c.base + c._drillDown).click(_.bind(this.drillDown, this));
            },

            getWidgetKey: function (event) {
                return 'browse';
            },

            getResourceType: function () {
                return navigator.const.navigation.browse.type;
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

        widgets.register('.' + navigator.const.navigation.browse.css.base, navigator.BrowseWidget);

        navigator.SearchWidget = navigator.AbstractNavPanel.extend({

            initialize: function (options) {
                navigator.AbstractNavPanel.prototype.initialize.call(this, options);
                this.data.root = this.$el.data('root') || '/content';
            },

            initContent: function (element) {
                var $element = navigator.AbstractNavPanel.prototype.initContent.call(this, element);
            },

            getWidgetKey: function (event) {
                return 'search';
            },

            getResourceType: function () {
                return navigator.const.navigation.search.type;
            },

            setRootPath: function (rootPath) {
                if (this.data.root !== rootPath) {
                    this.data.root = rootPath;
                    this.reload();
                }
            },

            setSearchTerm: function (searchTerm) {
                if (this.data.term !== searchTerm) {
                    this.data.term = searchTerm;
                    this.reload();
                }
            }
        });

        widgets.register('.' + navigator.const.navigation.search.css.base, navigator.SearchWidget);

        navigator.TreeWidget = navigator.AbstractNavWidget.extend({

            initialize: function (options) {
                navigator.AbstractNavWidget.prototype.initialize.call(this, options);
            },

            getWidgetKey: function (event) {
                return 'tree';
            },

            /**
             * @override
             */
            valueChanged: function () {
            }
        });

        widgets.register('.' + navigator.const.navigation.tree.css.base, navigator.TreeWidget);

        navigator.NavigatorWidget = navigator.AbstractNavWidget.extend({

            initialize: function (options) {
                navigator.AbstractNavWidget.prototype.initialize.call(this, options);
                this.browse = core.getWidget(this.$el,
                    '.' + navigator.const.navigation.browse.css.base, navigator.BrowseWidget);
                this.search = core.getWidget(this.$el,
                    '.' + navigator.const.navigation.search.css.base, navigator.SearchWidget);
                this.tree = core.getWidget(this.$el,
                    '.' + navigator.const.navigation.tree.css.base, navigator.TreeWidget);
                var eventId = 'asset_nav_' + this.getWidgetKey();
                if (this.browse) {
                    this.browse.$el.off('change.' + eventId).on('change.' + eventId, _.bind(function () {
                    }, this));
                }
                if (this.search) {
                    this.search.$el.off('change.' + eventId).on('change.' + eventId, _.bind(function () {
                    }, this));
                }
                if (this.tree) {
                    this.tree.$el.off('change.' + eventId).on('change.' + eventId, _.bind(function () {
                    }, this));
                }
            },

            getWidgetKey: function (event) {
                return 'widget';
            },

            setValue: function (targetPath, triggerChange, suppressReload) {
                navigator.AbstractNavWidget.prototype.setValue().call(this, targetPath, triggerChange, suppressReload);
                if (this.browse) {
                    this.browse.setValue(targetPath);
                }
                if (this.search) {
                    this.search.setValue(targetPath);
                }
                if (this.tree) {
                    this.tree.setValue(targetPath);
                }
            }
        });

        widgets.register('.' + navigator.const.navigation.css.base, navigator.NavigatorWidget);

    })(window.composum.assets.navigator, window.composum.assets, window.widgets, window.core);

})(window);
