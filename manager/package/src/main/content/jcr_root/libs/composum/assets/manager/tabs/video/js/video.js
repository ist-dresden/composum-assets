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

        manager.VideoTab = manager.AbstractManagerTab.extend({

            initialize: function(options) {
                manager.AbstractManagerTab.prototype.initialize.apply(this, [options]);
                this.$detailActions.find('.go-up').click(_.bind(this.goUp, this));
            }
        });

    })(window.composum.assets.manager, window.composum.assets, window.core);

})(window);
