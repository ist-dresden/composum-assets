/**
 *
 *
 */
(function (window) {
    'use strict';

    window.composum = window.composum || {};
    window.composum.assets = window.composum.assets || {};
    window.composum.assets.manager = window.composum.assets.manager || {};

    (function (manager, assets, core) {

        manager.AssetTab = manager.AbstractManagerTab.extend({

            initialize: function (options) {
                manager.AbstractManagerTab.prototype.initialize.apply(this, [options]);
                this.initContent();
                this.$detailActions.find('.go-up').click(_.bind(this.goUp, this));
                this.$detailActions.find('.transform').click(_.bind(this.toSimpeImage, this));
                this.$('.detail-toolbar .add').click(_.bind(this.uploadOriginal, this));
                this.$('.detail-toolbar .remove').click(_.bind(this.removeOriginal, this));
                this.$('.detail-toolbar .delete').click(_.bind(this.deleteAsset, this));
                this.$('.detail-toolbar .reload').click(_.bind(this.refresh, this));
            },

            initContent: function () {
                var c = assets.widgets.const.preview.css;
                this.$('.accordion-item').each(function () {
                    core.getWidget(this, '.' + c.base + c._lightbox, assets.widgets.AssetPreviewWidget);
                });
                manager.AbstractManagerTab.prototype.initContent.apply(this, [this.$content]);
            },

            toSimpeImage: function (event) {
                if (event) {
                    event.preventDefault();
                }
                core.ajaxPost('/bin/cpm/assets/assets.toSimpleImage.json' + this.data.path, {}, {}, _.bind(function () {
                    this.resetView();
                }, this));
                return false;
            },

            uploadOriginal: function (event) {

            },

            removeOriginal: function (event) {
            },

            deleteAsset: function (event) {
                assets.treeActions.deleteNode(event);
            }
        });

    })(window.composum.assets.manager, window.composum.assets, window.core);

})(window);
