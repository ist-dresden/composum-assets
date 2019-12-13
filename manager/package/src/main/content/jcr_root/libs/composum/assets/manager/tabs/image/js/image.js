/**
 *
 *
 */
'use strict';
(function (window) {

    window.composum = window.composum|| {};
    window.composum.assets = window.composum.assets || {};
    window.composum.assets.manager = window.composum.assets.manager || {};

    (function (manager, assets, core) {

        manager.ImageTab = manager.AbstractManagerTab.extend({

            initialize: function(options) {
                manager.AbstractManagerTab.prototype.initialize.apply(this, [options]);
                window.widgets.setUp(this.el);
                this.$detailActions.find('.transform').click(_.bind(this.toImageAsset, this));
                this.$('.detail-toolbar .reload').click(_.bind(this.reload, this));
                this.$('.detail-toolbar .delete').click(_.bind(assets.treeActions.deleteNode, assets.treeActions));
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

    })(window.composum.assets.manager, window.composum.assets, window.core);

})(window);
