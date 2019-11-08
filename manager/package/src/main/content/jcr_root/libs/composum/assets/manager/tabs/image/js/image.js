/**
 *
 *
 */
'use strict';
(function (window) {

    window.composum = window.composum|| {};
    window.composum.assets = window.composum.assets || {};

    (function (assets, core) {
        
        assets.ImageTab = assets.AbstractManagerTab.extend({

            initialize: function(options) {
                assets.AbstractManagerTab.prototype.initialize.apply(this, [options]);
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

    })(window.composum.assets, window.core);

})(window);
