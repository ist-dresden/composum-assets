(function () {
    'use strict';
    CPM.namespace('assets.nodes.view');
    (function (assets, browser, core) {

        assets.ImageView = browser.AbstractFileTab.extend({

            initialize: function (options) {
                options = _.extend(options, {
                    displayKey: 'assetView',
                    loadContent: _.bind(function (url) {
                        this.$image.attr('src', '');
                        this.$image.attr('src', core.getContextUrl(url));
                    }, this)
                });
                this.$image = this.$('.image-frame img');
                browser.AbstractFileTab.prototype.initialize.call(this, options);
            },

            getSelectors: function () {
                var selectors = this.$selectors.val();
                return selectors || 'asset';
            }
        });

        browser.registerGenericTab('cpa-asset-image', assets.ImageView);

    })(CPM.assets.nodes.view, CPM.nodes.browser, CPM.core);
})();