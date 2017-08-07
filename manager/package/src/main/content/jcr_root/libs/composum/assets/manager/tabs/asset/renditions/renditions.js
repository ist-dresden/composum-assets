/**
 *
 *
 */
(function (window) {
    'use strict';

    window.assets = window.assets || {};

    (function (assets, core) {

        assets.AssetConfigTab = assets.ConfigTab.extend({

            initialize: function (options) {
                assets.ConfigTab.prototype.initialize.apply(this, [options]);
            }
        });

    })(window.assets, window.core);

})(window);
