/**
 *
 *
 */
(function (window) {
    'use strict';

    window.assets = window.assets || {};

    (function (assets, core) {

        assets.AbstractManagerTab = core.console.DetailTab.extend({

            initialize: function (options) {
                this.data = {
                    path: this.$el.data('path'),
                    type: this.$el.data('type')
                };
                this.$detailPanel = this.$el.closest('.detail-panel');
                this.initContent();
                this.$detailActions = this.$detailPanel.find('.action-bar');
                this.$detailActions.find('.go-up').click(_.bind(this.goUp, this));
            },

            initContent: function (element) {
                var $element = element ? $(element) : this.$el;
                window.widgets.setUp($element[0]);
                $element.find('.drill-down').click(_.bind(this.drillDown, this));
            },

            goUp: function (event) {
                if (event) {
                    event.preventDefault();
                }
                var parentPath = core.getParentPath(this.data.path);
                $(document).trigger("path:selected", [parentPath]);
                return false;
            },

            drillDown: function (event) {
                if (event) {
                    event.preventDefault();
                }
                var folderPath = $(event.currentTarget).data('path');
                $(document).trigger("path:selected", [folderPath]);
                return false;
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

    })(window.assets, window.core);

})(window);
