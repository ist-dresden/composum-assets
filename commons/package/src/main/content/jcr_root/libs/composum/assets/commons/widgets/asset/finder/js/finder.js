/**
 *
 *
 */
(function (window) {
    'use strict';

    window.composum = window.composum|| {};
    window.composum.assets = window.composum.assets || {};

    (function (assets, widgets, core) {

        assets.FinderWidget = core.components.PathSelector.extend({

            initialize: function (options) {
                core.components.PathSelector.prototype.initialize.apply(this, [options]);
                this.$finderContent = this.$('.asset-finder');
            },

            /**
             * @override components.PathSelector.getEventId()
             */
            getEventId: function () {
                return this.$el.attr('id') || 'asset-finder';
            },

            /**
             * @override components.PathSelector.onPathChanged()
             */
            onPathChanged: function (path) {
            },

            /**
             * supports lazy loading
             */
            activate: function () {
                this.loadFinder();
            },

            /**
             * supports lazy loading
             */
            deactivate: function () {
                this.$finderContent.html('');
            },

            loadFinder: function () {
                core.ajaxGet('/bin/cpm/assets/assets.get.html', {
                    data: {
                        resourceType: 'composum/assets/commons/widgets/asset/finder',
                        selectors: 'detail'
                    }
                }, _.bind(function (data) {
                    this.$finderContent.html(data);
                    this.initFinder();
                }, this));
            },

            initFinderTools: function () {
            },

            initFinder: function () {
                this.$finderContent.find('.view-mode').click(_.bind(this.selectViewMode, this));
                this.$detailContent = this.$finderContent.find('.detail-content');
                this.initFinderTools();
                this.loadFinderView();
            },

            loadFinderHtml: function (viewMode, callback) {
                core.ajaxGet('/bin/cpm/assets/assets.get.html', {
                    data: {
                        resourceType: 'composum/assets/commons/widgets/asset/finder/' +
                        (viewMode === 'list' ? 'list' : 'thumbnails')
                    }
                }, callback);
            },

            loadFinderView: function () {
                this.$finderContent.find('.view-mode').removeClass('active');
                var viewMode = assets.profile.get('finder', 'mode', 'large');
                this.loadFinderHtml(viewMode, _.bind(function (data) {
                    this.$detailContent.html(data);
                    this.$finderContent.find('.view-mode[href="#' + viewMode + '"]').addClass('active');
                    this.initFinderView();
                }, this));
            },

            initFinderView: function () {
                this.$detailContent.find('.asset-link').click(_.bind(this.selectAsset, this));
            },

            selectViewMode: function (event) {
                if (event) {
                    event.preventDefault();
                }
                var $target = $(event.currentTarget);
                var viewMode = $target.attr('href').substring(1);
                assets.profile.set('finder', 'mode', viewMode);
                this.loadFinderView();
                return false;
            },

            selectAsset: function (event) {
                if (event) {
                    event.preventDefault();
                }
                var $target = $(event.currentTarget);
                var path = $target.data('path');
                if (path) {
                    this.pathWidget.setValue(path, false);
                }
                return false;
            }
        });

        widgets.register('.widget.asset-finder-widget', assets.FinderWidget);

    })(window.composum.assets, window.widgets, window.core);

})(window);
