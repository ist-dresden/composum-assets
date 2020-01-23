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

        manager.const = manager.const || {};
        manager.const.config = manager.const.config || {};
        manager.const.config.edit = {
            data: {
                path: 'path',
                type: 'type'
            },
            type: {
                asset: 'asset',
                variation: 'variation',
                rendition: 'rendition'
            },
            prop: {
                checked: 'checked',
                disabled: 'disabled'
            },
            css: {
                el: {
                    header: '.config-edit_header',
                    selector: '.config-edit_selector',
                    def: {
                        sel: '.default-checkbox',
                        label: 'label',
                        cbox: '.smart'
                    }
                },
                action: {
                    select: '.asset-link',
                    edit: '.detail-toolbar .edit',
                    copy: '.detail-toolbar .copy',
                    paste: '.detail-toolbar .paste',
                    add: '.detail-toolbar .add',
                    remove: '.detail-toolbar .remove'
                },
                class: {
                    checked: 'checked'
                }
            },
            event: {
                change: 'change'
            },
            uri: {
                change: {
                    default: '/bin/cpm/assets/assets.configDefault.json'
                }
            }
        };

        manager.ConfigTab = manager.AbstractManagerTab.extend({

            initialize: function (options) {
                var c = manager.const.config.edit;
                manager.AbstractManagerTab.prototype.initialize.apply(this, [options]);
                this.initContent();
                this.$(c.css.action.edit).click(_.bind(this.edit, this));
                this.$(c.css.action.copy).click(_.bind(this.copy, this));
                this.$(c.css.action.paste).click(_.bind(this.paste, this));
                this.$(c.css.action.add).click(_.bind(this.add, this));
                this.$(c.css.action.remove).click(_.bind(this.remove, this));
            },

            initContent: function () {
                var c = assets.config.const.general.css;
                this.config = core.getWidget(this.$el, '.' + c.base, assets.config.ConfigEditor);
                manager.AbstractManagerTab.prototype.initContent.apply(this, [this.$content]);
            },

            getSelectedPath: function () {
                return this.selectedPath ? this.selectedPath : this.data.path;
            },

            getSelectedType: function () {
                var c = manager.const.config.edit;
                return this.selectedPath
                    ? this.$(c.css.el.header + '[data-path="' + this.selectedPath + '"]').data(c.data.type)
                    : c.type.asset;
            },

            getConfigPath: function ($el) {
                var c = manager.const.config.edit;
                return $el.closest(c.css.el.header).data(c.data.path);
            },

            edit: function (event) {
                if (event) {
                    event.preventDefault();
                }
                var path = this.getSelectedPath();
                assets.openAssetConfigDialog(path, _.bind(function () {
                    this.resetView();
                }, this));
                return false;
            },

            copy: function (event, path) {
                if (event) {
                    event.preventDefault();
                }
                core.console.getProfile().set('nodes', 'clipboard', {
                    path: this.getSelectedPath()
                });
                return false;
            },

            paste: function (event) {
                if (event) {
                    event.preventDefault();
                }
                var parentPath = this.getSelectedPath();
                var clipboard = core.console.getProfile().get('nodes', 'clipboard');
                if (parentPath && clipboard && clipboard.path) {
                    var name = core.getNameFromPath(clipboard.path);
                    core.ajaxPost("/bin/cpm/assets/assets.copyConfig.json" + clipboard.path, {
                        path: parentPath
                    }, {}, _.bind(function () {
                        this.resetView();
                    }, this));
                }
            },

            add: function (event) {
                if (event) {
                    event.preventDefault();
                }
                var path = this.getSelectedPath();
                var type = this.getSelectedType();
                assets.openAssetConfigCreateDialog(path, type, _.bind(function () {
                    this.resetView();
                }, this));
                return false;
            },

            remove: function (event) {
                if (event) {
                    event.preventDefault();
                }
                var path = this.getSelectedPath();
                var type = this.getSelectedType();
                assets.openAssetConfigDeleteDialog(path, type, _.bind(function () {
                    this.resetView();
                }, this));
                return false;
            }
        });

    })(window.composum.assets.manager, window.composum.assets, window.core);

})(window);
