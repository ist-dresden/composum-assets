/**
 *
 *
 */
(function (window) {
    'use strict';

    window.composum = window.composum|| {};
    window.composum.assets = window.composum.assets || {};

    (function (assets, core) {

        assets.AssetConfigTab = assets.ConfigTab.extend({

            initialize: function (options) {
                assets.ConfigTab.prototype.initialize.apply(this, [options]);
            }
        });

    })(window.composum.assets, window.core);

})(window);
