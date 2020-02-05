/**
 *
 *
 */
(function () {
    'use strict';
    core.namespace('composum.assets.manager');

    (function (manager, assets, core) {

        manager.PreviewTab = manager.AbstractManagerTab.extend({

            initialize: function(options) {
                manager.AbstractManagerTab.prototype.initialize.apply(this, [options]);
                this.initContent();
                this.$detailActions.find('.go-up').click(_.bind(this.goUp, this));
                this.$('.detail-toolbar .reload').click(_.bind(this.refresh, this));
                this.$('.detail-toolbar .delete').click(_.bind(assets.treeActions.deleteNode, assets.treeActions));
            },

            initContent: function (element) {
                manager.AbstractManagerTab.prototype.initContent.apply(this, [element]);
                this.preview = core.getWidget(this.$el, '.composum-assets-widget-preview_lightbox',
                    assets.widgets.AssetPreviewWidget);
            }
        });

    })(composum.assets.manager, composum.assets, core);

})();
