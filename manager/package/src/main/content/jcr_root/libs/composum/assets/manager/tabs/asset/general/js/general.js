/**
 *
 *
 */
(function (window) {
    'use strict';

    window.composum = window.composum|| {};
    window.composum.assets = window.composum.assets || {};
    window.composum.assets.manager = window.composum.assets.manager || {};

    (function (manager, assets, core) {

        manager.AssetTab = manager.AbstractManagerTab.extend({

            initialize: function (options) {
                manager.AbstractManagerTab.prototype.initialize.apply(this, [options]);
                window.widgets.setUp(this.el);
                this.$detailActions.find('.transform').click(_.bind(this.toSimpeImage, this));
                this.$('.detail-toolbar .add').click(_.bind(this.uploadOriginal, this));
                this.$('.detail-toolbar .remove').click(_.bind(this.removeOriginal, this));
                this.$('.detail-toolbar .reload').click(_.bind(this.reload, this));
                this.$('.detail-toolbar .delete').click(_.bind(this.deleteAsset, this));
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
