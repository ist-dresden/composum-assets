/**
 *
 *
 */
(function (window) {
    'use strict';

    window.composum = window.composum|| {};
    window.composum.assets = window.composum.assets || {};

    (function (assets, widgets, core) {

        assets.BrowserWidget = assets.FinderWidget.extend({

            initialize: function (options) {
                assets.FinderWidget.prototype.initialize.apply(this, [options]);
            },

            /**
             * @override components.PathSelector.getEventId()
             */
            getEventId: function () {
                return this.$el.attr('id') || 'asset-browser';
            },

            /**
             * @override components.PathSelector.onPathChanged()
             */
            onPathChanged: function (path) {
                this.loadFinderView(path);
            },

            getBrowserPath: function (path) {
                if (!path) {
                    path = this.pathWidget.getValue();
                }
                if (!path) {
                    path = this.pathWidget.getRootPath();
                }
                return path;
            },

            /**
             * @override assets.FinderWidget.loadFinder()
             */
            loadFinder: function (path) {
                path = this.getBrowserPath(path);
                core.ajaxGet('/bin/cpm/assets/assets.get.html' + path, {
                    data: {
                        resourceType: 'composum/assets/commons/widgets/asset/browser',
                        selectors: 'detail'
                    }
                }, _.bind(function (data) {
                    this.$finderContent.html(data);
                    this.initFinder(path);
                }, this));
            },

            /**
             * @override assets.FinderWidget.initFinderTools()
             */
            initFinderTools: function () {
                this.$finderContent.find('.go-up').click(_.bind(this.goUp, this));
            },

            /**
             * @override assets.FinderWidget.loadFinderHtml()
             */
            loadFinderHtml: function (viewMode, callback) {
                var path = this.getBrowserPath(path);
                core.ajaxGet('/bin/cpm/assets/assets.get.html' + path, {
                    data: {
                        resourceType: 'composum/assets/commons/widgets/asset/browser/' + viewMode
                    }
                }, callback);
            },

            /**
             * @override assets.FinderWidget.initFinderView()
             */
            initFinderView: function (path) {
                this.$detailContent.find('[data-toggle="collapse"]').click(_.bind(function (event) {
                    if (event) {
                        event.preventDefault();
                    }
                    var $target = $(event.currentTarget);
                    var $collapsible = this.$($target.data('child'));
                    $collapsible.collapse('toggle');
                    return false;
                }, this));
                this.$detailContent.find('.drill-down').click(_.bind(this.drillDown, this));
                this.$detailContent.find('.asset-link').click(_.bind(this.selectAsset, this));
            },

            goUp: function (event) {
                if (event) {
                    event.preventDefault();
                }
                var parentPath = core.getParentPath(this.pathWidget.getPath());
                if (parentPath) {
                    this.pathWidget.setValue(parentPath, true);
                }
                return false;
            },

            drillDown: function (event) {
                if (event) {
                    event.preventDefault();
                }
                var folderPath = $(event.currentTarget).data('path');
                if (folderPath) {
                    this.pathWidget.setValue(folderPath, true);
                }
                return false;
            }
        });

        widgets.register('.widget.asset-browser-widget', assets.BrowserWidget);

    })(window.composum.assets, window.widgets, window.core);

})(window);
