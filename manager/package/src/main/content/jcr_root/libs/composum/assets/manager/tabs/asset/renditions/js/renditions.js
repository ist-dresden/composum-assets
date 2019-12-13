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

        manager.AssetConfigTab = manager.ConfigTab.extend({

            initialize: function (options) {
                manager.ConfigTab.prototype.initialize.apply(this, [options]);
            }
        });

    })(window.composum.assets.manager, window.composum.assets, window.core);

})(window);
