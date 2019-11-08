/**
 *
 *
 */
'use strict';
(function (window) {

    window.composum = window.composum|| {};
    window.composum.assets = window.composum.assets || {};

    (function (assets, core) {
        
        assets.VideoTab = assets.AbstractManagerTab.extend({

            initialize: function(options) {
                assets.AbstractManagerTab.prototype.initialize.apply(this, [options]);
                window.widgets.setUp(this.el);
            }
        });

    })(window.composum.assets, window.core);

})(window);
