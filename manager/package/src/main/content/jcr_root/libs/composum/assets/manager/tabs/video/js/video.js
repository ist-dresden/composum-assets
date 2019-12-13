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
                window.widgets.setUp(this.el);
            }
        });

    })(window.composum.assets.manager, window.composum.assets, window.core);

})(window);
