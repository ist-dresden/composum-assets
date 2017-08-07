/**
 *
 *
 */
'use strict';
(function (window) {

    window.assets = window.assets || {};

    (function (assets, core) {

        /**
         * the 'accordion-widget' (window.core.components.AccordionPanel)
         * possible attributes:
         */
        assets.AssetPageView = Backbone.View.extend({

            initialize: function (options) {
                window.widgets.setUp();
            }
        });

        assets.pageView = core.getView('body.assets.page.view', assets.AssetPageView);

    })(window.assets, window.core);

})(window);
