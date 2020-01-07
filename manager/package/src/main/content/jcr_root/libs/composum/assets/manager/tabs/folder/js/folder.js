/**
 *
 *
 */
'use strict';
(function (window) {

    window.composum = window.composum || {};
    window.composum.assets = window.composum.assets || {};
    window.composum.assets.manager = window.composum.assets.manager || {};

    (function (manager, assets, core) {

        manager.FolderTab = manager.AbstractManagerTab.extend({

            initialize: function (options) {
                this.$content = this.$('.folder-content');
                manager.AbstractManagerTab.prototype.initialize.apply(this, [options]);
                this.initContent();
                this.$detailActions.find('.config').click(_.bind(this.createConfig, this));
                this.$('.detail-toolbar .create-folder').click(_.bind(assets.treeActions.createFolder, assets.treeActions));
                this.$('.detail-toolbar .create-asset').click(_.bind(assets.treeActions.createAsset, assets.treeActions));
                this.$('.detail-toolbar .meta').click(_.bind(this.refreshMetaData, this));
                this.$('.detail-toolbar .reload').click(_.bind(this.refresh, this));
                this.$('.detail-toolbar .delete').click(_.bind(assets.treeActions.deleteNode, assets.treeActions));
                $(document).off('path:selected.Browser').on('path:selected.Browser', _.bind(this.onSelected, this));
                $(document).off('filter:changed.AssetsManagerBrowse')
                    .on('filter:changed.AssetsManagerBrowse', _.bind(function (event, filter) {
                        if (this.browser) {
                            this.browser.setFilter(filter);
                        }
                    }, this));
            },

            initContent: function () {
                this.browser = core.getWidget(this.$content,
                    '.' + assets.navigator.const.browse.css.base, assets.navigator.BrowseWidget, {
                        filter: core.console.getProfile().get('assets', 'filter', undefined)
                    });
                manager.AbstractManagerTab.prototype.initContent.apply(this, [this.$content]);
                this.browser.$el.off('change.Manager').on('change.Manager', _.bind(this.onSelect, this));
            },

            onSelect: function (event) {
                if (event) {
                    event.preventDefault();
                }
                var path = this.browser.getValue();
                $(document).trigger("path:selected", [path]);
                return false;
            },

            onSelected: function (event, path) {
                if (event) {
                    event.preventDefault();
                }
                this.browser.setValue(path);
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
                this.browser.reload();
                return false;
            }
        });

    })(window.composum.assets.manager, window.composum.assets, window.core);

})(window);
