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

        manager.AbstractManagerTab = core.console.DetailTab.extend({

            initialize: function (options) {
                this.data = {
                    path: this.$el.data('path'),
                    type: this.$el.data('type')
                };
                this.$detailPanel = this.$el.closest('.detail-panel');
                this.initContent();
                this.$detailActions = this.$detailPanel.find('.action-bar');
            },

            initContent: function (element) {
                var $element = element ? $(element) : this.$el;
                window.widgets.setUp($element[0]);
            },

            resetView: function (event) {
                if (event) {
                    event.preventDefault();
                }
                assets.current = undefined;
                assets.setCurrentPath(this.data.path);
                return false;
            }
        });

    })(window.composum.assets.manager, window.composum.assets, window.core);

})(window);
