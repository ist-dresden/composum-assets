/**
 *
 *
 */
'use strict';
(function (window) {

    window.composum = window.composum|| {};
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

        manager.ConfigTab = manager.FolderTab.extend({

            initialize: function (options) {
                var c = manager.const.config.edit;
                manager.FolderTab.prototype.initialize.apply(this, [options]);
                this.$(c.css.action.edit).click(_.bind(this.edit, this));
                this.$(c.css.action.copy).click(_.bind(this.copy, this));
                this.$(c.css.action.paste).click(_.bind(this.paste, this));
                this.$(c.css.action.add).click(_.bind(this.add, this));
                this.$(c.css.action.remove).click(_.bind(this.remove, this));
            },

            initContent: function () {
                var c = manager.const.config.edit;
                manager.FolderTab.prototype.initContent.apply(this, [this.$content]);
                this.initSelectors();
                this.initDefaultHandles();
                this.$content.find(c.css.action.select).click(_.bind(this.selectAsset, this));
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
            },

            select: function (event) {
                if (event) {
                    event.preventDefault();
                }
                if (!this.busy) {
                    var c = manager.const.config.edit;
                    this.busy = true;
                    if (this.$selectors) {
                        if (event) {
                            var $selector = $(event.currentTarget);
                            var path = this.getConfigPath($selector);
                            this.selectedPath = (path === this.selectedPath ? undefined : path);
                        }
                        this.$selectors.prop(c.prop.checked, false);
                        if (this.selectedPath) {
                            this.$(c.css.el.header + '[data-path="' + this.selectedPath + '"] ' + c.css.el.selector)
                                .prop(c.prop.checked, true);
                        }
                    }
                    this.busy = false;
                }
                return false;
            },

            initSelectors: function () {
                var c = manager.const.config.edit;
                if (this.$selectors) {
                    this.$selectors.off(c.event.change);
                }
                this.$selectors = this.$(c.css.el.selector);
                this.select();
                this.$selectors.on(c.event.change, _.bind(this.select, this));
            },

            selectDefault: function (event) {
                if (event) {
                    event.preventDefault();
                    var c = manager.const.config.edit;
                    if (this.$defaultHandles) {
                        var $defaultHandle = $(event.currentTarget);
                        var path = this.getConfigPath($defaultHandle);
                        core.ajaxPost(c.uri.change.default + path,
                            undefined, undefined, _.bind(function (result) {
                                var $container = $defaultHandle.closest('.panel-group');
                                for (var i = 0; i < result.length; i++) {
                                    var $handle = $container.find('> .accordion-item > ' + c.css.el.header
                                        + '[data-name="' + result[i].name + '"] > .panel-title > ' + c.css.el.def.sel);
                                    if (result[i].isDefault) {
                                        $handle.find(c.css.el.def.cbox)
                                            .prop(c.prop.checked, true)
                                            .prop(c.prop.disabled, true);
                                        $handle.find(c.css.el.def.label).addClass(c.css.class.checked);
                                    } else {
                                        $handle.find(c.css.el.def.cbox)
                                            .prop(c.prop.checked, false)
                                            .prop(c.prop.disabled, false);
                                        $handle.find(c.css.el.def.label).removeClass(c.css.class.checked);
                                    }
                                }
                            }, this));
                    }
                }
                return false;
            },

            initDefaultHandles: function () {
                var c = manager.const.config.edit;
                if (this.$defaultHandles) {
                    this.$defaultHandles.off(c.event.change);
                }
                this.$defaultHandles = this.$(c.css.el.header + ' ' + c.css.el.def.sel + ' ' + c.css.el.def.cbox);
                this.$defaultHandles.on(c.event.change, _.bind(this.selectDefault, this));
            }
        });

    })(window.composum.assets.manager, window.composum.assets, window.core);

})(window);
