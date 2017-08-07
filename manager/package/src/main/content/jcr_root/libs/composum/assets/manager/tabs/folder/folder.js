/**
 *
 *
 */
'use strict';
(function (window) {

    window.assets = window.assets || {};

    (function (assets, core) {

        assets.FolderTab = assets.AbstractManagerTab.extend({

            initialize: function (options) {
                this.$content = this.$('.folder-content');
                assets.AbstractManagerTab.prototype.initialize.apply(this, [options]);
                this.$detailActions.find('.config').click(_.bind(this.createConfig, this));
                this.$('.detail-toolbar .create-folder').click(_.bind(assets.treeActions.createFolder, assets.treeActions));
                this.$('.detail-toolbar .create-asset').click(_.bind(assets.treeActions.createAsset, assets.treeActions));
                this.$('.detail-toolbar .meta').click(_.bind(this.refreshMetaData, this));
                this.$('.detail-toolbar .reload').click(_.bind(this.resetView, this));
                this.$('.detail-toolbar .delete').click(_.bind(assets.treeActions.deleteNode, assets.treeActions));
            },

            initContent: function () {
                assets.AbstractManagerTab.prototype.initContent.apply(this, [this.$content]);
                this.$content.find('.asset-link').click(_.bind(this.selectAsset, this));
            },

            selectAsset: function (event) {
                if (event) {
                    event.preventDefault();
                }
                var $target = $(event.currentTarget);
                var path = $target.data('path');
                $(document).trigger("path:selected", [path]);
                return false;
            },

            createConfig: function (event) {
                if (event) {
                    event.preventDefault();
                }
                core.ajaxGet('/bin/cpm/assets/assets.createConfig.json' + this.data.path, {}, _.bind(function () {
                    core.console.getProfile().set('assets', 'detailTab', 'view'); // set 'config edit' tab group
                    this.resetView();
                }, this));
                return false;
            },

            refreshMetaData: function (event) {
                if (event) {
                    event.preventDefault();
                }
                core.ajaxGet('/bin/cpm/assets/assets.refreshMeta.json' + this.data.path, {}, _.bind(function () {
                    this.refresh();
                }, this));
                return false;
            },

            refresh: function (event) {
                if (event) {
                    event.preventDefault();
                }
                core.ajaxGet('/bin/cpm/assets/assets.reload.html' + this.data.path, {
                    data: {
                        resourceType: this.data.type
                    }
                }, _.bind(function (data) {
                    this.$content.html(data);
                    this.initContent();
                }, this));
                return false;
            }
        });

    })(window.assets, window.core);

})(window);
