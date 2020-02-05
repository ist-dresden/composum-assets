/**
 *
 *
 */
(function () {
    'use strict';
    CPM.namespace('assets.manager');

    (function (manager, assets, core) {

        manager.ImageTab = manager.AbstractManagerTab.extend({

            initialize: function (options) {
                manager.AbstractManagerTab.prototype.initialize.apply(this, [options]);
                this.initContent();
                this.$detailActions.find('.transform').click(_.bind(this.toImageAsset, this));
                this.$('.detail-toolbar .delete').click(_.bind(assets.treeActions.deleteNode, assets.treeActions));
            },

            initContent: function (element) {
                manager.AbstractManagerTab.prototype.initContent.apply(this, [element]);
                this.preview = core.getWidget(this.$el, '.composum-assets-widget-preview_lightbox',
                    assets.widgets.AssetPreviewWidget);
            },

            toImageAsset: function (event) {
                if (event) {
                    event.preventDefault();
                }
                core.ajaxPost('/bin/cpm/assets/assets.toImageAsset.json' + this.data.path, {}, {}, _.bind(function () {
                    this.resetView();
                }, this));
                return false;
            }
        });

    })(composum.assets.manager, composum.assets, core);

})();
