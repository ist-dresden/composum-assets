/**
 *
 *
 */
(function () {
    'use strict';
    core.namespace('composum.assets.manager');

    (function (manager, assets, core) {

        manager.AbstractManagerTab = core.console.DetailTab.extend({

            initialize: function (options) {
                this.data = {
                    path: this.$el.data('path'),
                    type: this.$el.data('type')
                };
                this.$detailPanel = this.$el.closest('.detail-panel');
                this.$detailActions = this.$detailPanel.find('.action-bar');
                this.$detailActions.find('.go-up').click(_.bind(this.goUp, this));
                this.$('.detail-toolbar .reload').click(_.bind(this.refresh, this));
                this.$('.detail-toolbar .open').click(_.bind(this.open, this));
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
            },

            goUp: function (event) {
                var path = $(event.currentTarget).data('path');
                path = core.getParentPath(path ? path : this.data.path);
                if (path) {
                    $(document).trigger("path:selected", [path]);
                }
            },

            refresh: function (event) {
                if (event) {
                    event.preventDefault();
                }
                assets.detailView.reload();
                return false;
            },

            open: function (event) {
                if (event) {
                    event.preventDefault();
                }
                var openUrl = this.$el.data('open');
                if (openUrl) {
                    window.open(openUrl, '_blank');
                }
                return false;
            }
        });

    })(composum.assets.manager, composum.assets, core);

})();
