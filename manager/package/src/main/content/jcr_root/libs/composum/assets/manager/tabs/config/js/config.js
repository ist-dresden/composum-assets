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
                    create: '.detail-toolbar .create',
                    copy: '.detail-toolbar .copy',
                    paste: '.detail-toolbar .paste',
                    delete: '.detail-toolbar .delete',
                    reload: '.detail-toolbar .reload',
                    open: '.detail-toolbar .open'
                },
                class: {
                    checked: 'checked'
                },
                navigator: {
                    base: 'composum-assets-widget-navigator',
                    _goUp: '_go-up'
                }
            },
            event: {
                change: 'change'
            },
            uri: {
                dialog: {
                    config: '/libs/composum/assets/commons/dialogs/config',
                    _create: '/create.html',
                    _paste: '/paste.html',
                    _delete: '/delete.html'
                }
            }
        };

        manager.ConfigTab = manager.AbstractManagerTab.extend({

            initialize: function (options) {
                var c = manager.const.config.edit.css.action;
                var n = manager.const.config.edit.css.navigator;
                manager.AbstractManagerTab.prototype.initialize.apply(this, [options]);
                this.initContent();
                this.$('.' + n.base + n._goUp).click(_.bind(this.goUp, this));
                this.$(c.create).click(_.bind(this.create, this));
                this.$(c.copy).click(_.bind(this.copy, this));
                var clipboardPath = this.getClipboardPath();
                if (!clipboardPath) {
                    this.$(c.paste).prop('disabled', true);
                } else {
                    core.i18n.get('Paste Configuration', _.bind(function (text) {
                        this.$(c.paste).attr('title', text + ': ' + clipboardPath);
                    }, this));
                    this.$(c.paste).click(_.bind(this.paste, this));
                }
                this.$(c.delete).click(_.bind(this.delete, this));
                this.$(c.reload).click(_.bind(this.refresh, this));
                this.$(c.open).click(_.bind(this.open, this));
            },

            initContent: function () {
                var c = assets.config.const.general.css;
                this.config = core.getWidget(this.$el, '.' + c.base, assets.config.ConfigEditor, {
                    holder: this
                });
                manager.AbstractManagerTab.prototype.initContent.apply(this, [this.$content]);
            },

            getSelectedPath: function () {
                return this.selectedPath ? this.selectedPath : this.data.path;
            },

            open: function (event, path) {
                if (event) {
                    event.preventDefault();
                }
                window.open(this.$el.data('config') + '.page.html', '_blank');
                return false;
            },

            create: function (event, path) {
                if (event) {
                    event.preventDefault();
                }
                var u = manager.const.config.edit.uri.dialog;
                core.openFormDialog(u.config + u._create + this.getSelectedPath(),
                    core.components.FormDialog, {}, undefined, _.bind(function () {
                        this.refresh();
                    }, this));
                return false;
            },

            delete: function (event, path) {
                if (event) {
                    event.preventDefault();
                }
                var u = manager.const.config.edit.uri.dialog;
                core.openFormDialog(u.config + u._delete + this.getSelectedPath(),
                    core.components.FormDialog, {}, undefined, _.bind(function () {
                        this.refresh();
                    }, this));
                return false;
            },

            copy: function (event, path) {
                if (event) {
                    event.preventDefault();
                }
                core.console.getProfile().set('nodes', 'clipboard', {
                    path: this.getSelectedPath(),
                    time: new Date().getTime()
                });
                return false;
            },

            paste: function (event) {
                if (event) {
                    event.preventDefault();
                }
                var parentPath = this.getSelectedPath();
                var path = this.getClipboardPath();
                if (parentPath && path) {
                    core.ajaxPost("/bin/cpm/assets/config.copy.json" + path, {
                        path: parentPath
                    }, {}, _.bind(function () {
                        this.resetView();
                    }, this));
                }
                return false;
            },

            getClipboardPath: function () {
                var now = new Date().getTime();
                var clipboard = core.console.getProfile().get('nodes', 'clipboard');
                return clipboard && clipboard.path && clipboard.path !== this.getSelectedPath() &&
                clipboard.time && clipboard.time + 12000000 > now // 20 min
                    ? clipboard.path : undefined;
            }
        });

    })(window.composum.assets.manager, window.composum.assets, window.core);

})(window);
