/**
 *
 *
 */
'use strict';
(function (window) {

    window.assets = window.assets || {};

    (function (assets, core) {
        
        assets.VideoTab = assets.AbstractManagerTab.extend({

            initialize: function(options) {
                assets.AbstractManagerTab.prototype.initialize.apply(this, [options]);
                window.widgets.setUp(this.el);
            }
        });

    })(window.assets, window.core);

})(window);
