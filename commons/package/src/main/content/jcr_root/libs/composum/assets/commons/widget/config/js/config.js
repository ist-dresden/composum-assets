/**
 * the 'config' widget behaviour impelementation
 */
(function () {
    'use strict';
    core.namespace('composum.assets.config');

    (function (config, assets, components) {

        config.const = _.extend(config.const || {}, {
            general: {
                css: {
                    base: 'composum-assets-widget-config',
                    _preview: '_preview',
                    _form: '_form',
                    _tabs: '_form-tabs',
                    _tab: '_tab',
                    _panel: '_form-panel',
                    _select: '_select',
                    _variation: '-variation',
                    _rendition: '-rendition',
                    tabs: {
                        _valid: '_valid',
                        _base: '_base',
                        _node: '_node',
                        _variation: '_variation',
                        _rendition: '_rendition'
                    }
                },
                url: {
                    config: {
                        base: '/bin/cpm/assets/config',
                        _variations: '.variations.json',
                        _renditions: '.renditions.json'
                    },
                    form: {
                        base: '/libs/composum/assets/commons/widget/config/form'
                    }
                }
            },
            preview: {
                css: {
                    base: 'composum-assets-widget-preview',
                    config: 'composum-assets-widget-config_preview',
                    _image: '_image',
                    _path: '_path',
                    _clear: '_path-clear',
                    _refresh: '_refresh'
                }
            },
            form: {
                css: {
                    base: 'composum-assets-widget-config-form',
                    _form: '_form',
                    _content: '_content',
                    _tabs: '_tabs',
                    _panels: '_panels',
                    action: {
                        base: 'composum-assets-widget-config-form_action',
                        _edit: '-edit'
                    },
                    field: {
                        _extension: '_extension',
                        _default: '_default',
                        _category: '_category'
                    }
                }
            }
        });

        config.FormWidget = components.FormWidget.extend({

            initialize: function (options) {
                var c = config.const.form.css;
                components.FormWidget.prototype.initialize.call(this, options);
                this.data = {
                    holder: options.holder,
                    scope: options.scope,
                    variation: options.variation,
                    rendition: options.rendition,
                    path: this.$el.data('config'),
                    valid: this.$el.data('valid')
                };
                this.tabbed.$nav.find('a[data-key="' + assets.profile.get('config', 'formTab', 'general') + '"]').tab('show');
                this.tabbed.$nav.find('a').on('shown.bs.tab.FormTabs', _.bind(function (event) {
                    var $tab = $(event.target);
                    assets.profile.set('config', 'formTab', $tab.data('key'));
                }, this));
                if (this.data.valid) {
                    var f = config.const.form.css.field;
                    this.fields = {
                        extension: core.getWidget(this.$el, '.' + c.base + f._extension, components.CheckboxWidget),
                        isDefault: core.getWidget(this.$el, '.' + c.base + f._default, components.CheckboxWidget),
                        category: core.getWidget(this.$el, '.' + c.base + f._category, components.MultiFormWidget)
                    };
                    if (this.fields.isDefault) {
                        this.fields.isDefault.changed('ConfigForm', _.bind(this.defaultChanged, this));
                    }
                    this.fields.category.changed('ConfigForm', _.bind(function (event, value) {
                        if (this.fields.isDefault) {
                            this.fields.isDefault.setValue(_.indexOf(value, 'default') >= 0);
                        }
                    }, this));
                    var a = config.const.form.css.action;
                    var u = config.const.dialog.url;
                    this.$('.' + a.base + a._edit).click(_.bind(function () {
                        var url = new core.SlingUrl(u.base + u._change + this.data.scope + '.html');
                        url.suffix = this.data.path;
                        // noinspection FallThroughInSwitchStatementJS
                        switch (this.data.scope) {
                            case 'rendition':
                                url.parameters.rendition = this.data.rendition;
                            case 'variation':
                                url.parameters.variation = this.data.variation;
                                break;
                        }
                        core.openFormDialog(url.build(), config.ChangeDialog, {}, undefined, _.bind(function () {
                            this.data.holder.reload();
                        }, this));
                    }, this));
                }
            },

            defaultChanged: function (event, value) {
                var categorySet = this.fields.category.getValue();
                if (value) {
                    if (_.indexOf(categorySet, 'default') < 0) {
                        this.fields.category.setValue(_.union(['default'], categorySet));
                    }
                } else {
                    if (_.indexOf(categorySet, 'default') >= 0) {
                        this.fields.category.setValue(_.without(categorySet, 'default'));
                    }
                }
            },

            validate: function (alertMethod) {
                var valid = false;
                if (this.data.valid) {
                    valid = components.FormWidget.prototype.validate.call(this, alertMethod);
                }
                return valid;
            },

            doFormSubmit: function (alertMethod, onSuccess, onError) {
                if (this.data.valid) {
                    components.FormWidget.prototype.doFormSubmit.call(this, alertMethod, onSuccess, onError);
                }
            }
        });

        config.ConfigForm = Backbone.View.extend({

            initialize: function (options) {
                var c = config.const.general.css;
                this.editor = options.editor;
                this.data = {};
                this.$tabs = this.$('.' + c.base + c._tabs);
                this.$panel = this.$('.' + c.base + c._panel);
                if (!this.editor.data.base) {
                    this.$tabs.find('.' + c.base + c._tab + c.tabs._base).addClass('disabled');
                }
                if (this.editor.data.valid) {
                    this.$tabs.find('.' + c.base + c._tab + c.tabs._node + ' .delete').click(_.bind(this.deleteConfig, this));
                    this.$tabs.find('.' + c.base + c._tab + c.tabs._variation + ' .add').click(_.bind(this.addVariation, this));
                    this.$tabs.find('.' + c.base + c._tab + c.tabs._variation + ' .remove').click(_.bind(this.removeVariation, this));
                    this.$tabs.find('.' + c.base + c._tab + c.tabs._rendition + ' .add').click(_.bind(this.addRendition, this));
                    this.$tabs.find('.' + c.base + c._tab + c.tabs._rendition + ' .remove').click(_.bind(this.removeRendition, this));
                } else {
                    this.$tabs.find('.' + c.base + c._tab + c.tabs._valid).addClass('disabled');
                }
                if (this.editor.data.base || this.editor.data.valid) {
                    this.variationSelect = core.getWidget(this.$el, '.' + c.base + c._select + c._variation, components.SelectWidget);
                    this.renditionSelect = core.getWidget(this.$el, '.' + c.base + c._select + c._rendition, components.SelectWidget);
                    this.loadVariations();
                    this.variationSelect.changed('ConfigForm', _.bind(this.selectVariation, this));
                    this.renditionSelect.changed('ConfigForm', _.bind(this.selectRendition, this));
                    this.$tabs.find('a').click(_.bind(this.selectTab, this));
                }
            },

            selectVariation: function (event, key) {
                if (event) {
                    if (!key) {
                        key = this.variationSelect.getValue();
                    }
                    assets.profile.set('config', 'variation', key);
                }
                if (this.data.variation !== key) {
                    this.data.variation = key;
                    this.data.rendition = undefined;
                    this.loadRenditions();
                }
            },

            selectRendition: function (event, key) {
                if (event) {
                    if (!key) {
                        key = this.renditionSelect.getValue();
                    }
                    assets.profile.set('config', 'rendition', key);
                }
                if (this.data.rendition !== key) {
                    this.data.rendition = key;
                    this.selectTab(undefined, this.adjustTab(), true);
                }
            },

            adjustTab: function (tab) {
                if (!tab) {
                    tab = assets.profile.get('config', 'configTab', 'node');
                }
                if (!this.editor.data.base && tab === 'base') {
                    tab = 'node'
                }
                if (!this.editor.data.valid) {
                    tab = 'base';
                }
                return tab;
            },

            selectTab: function (event, tab, force) {
                var c = config.const.general.css;
                if (event) {
                    event.preventDefault();
                    if (!tab) {
                        var $tab = $(event.currentTarget).closest('.' + c.base + c._tab);
                        tab = $tab.data('key');
                    }
                    assets.profile.set('config', 'configTab', tab);
                }
                tab = this.adjustTab(tab);
                if ((tab && this.data.tab !== tab) || force) {
                    this.loadForm(tab || this.data.tab);
                }
                return false;
            },

            loadUrl: function (uri) {
                var url = new core.SlingUrl(uri);
                if (this.data.variation) {
                    url.parameters.variation = encodeURIComponent(this.data.variation);
                }
                if (this.data.rendition) {
                    url.parameters.rendition = encodeURIComponent(this.data.rendition);
                }
                url.parameters.cumulated = 'true';
                url.suffix = core.encodePath(this.editor.data.config || this.editor.data.base);
                return url.build();
            },

            loadVariations: function () {
                var u = config.const.general.url.config;
                var url = this.loadUrl(u.base + u._variations);
                core.getJson(url, _.bind(function (result) {
                    this.variationSelect.setOptions(result.list.variations);
                    var value = result.data.variations[assets.profile.get('config', 'variation')];
                    this.data.variation = value ? value.name || value : result.data.configuration.defaultVariation;
                    this.variationSelect.setValue(this.data.variation, false);
                    this.loadRenditions();
                }, this));
            },

            loadRenditions: function () {
                var u = config.const.general.url.config;
                var url = this.loadUrl(u.base + u._renditions);
                core.getJson(url, _.bind(function (result) {
                    this.renditionSelect.setOptions(result.list.renditions);
                    var value = result.data.renditions[assets.profile.get('config', 'rendition')];
                    this.data.rendition = value ? value.name || value
                        : result.data.variation ? result.data.variation.defaultRendition : undefined;
                    this.renditionSelect.setValue(this.data.rendition, false);
                    this.selectTab(undefined, this.adjustTab(), true);
                }, this));
            },

            loadForm: function (tab, forceReload) {
                var url = this.loadUrl(config.const.general.url.form.base + '.readonly.' + tab + '.html');
                core.getHtml(url, _.bind(function (content) {
                    var c = config.const.general.css;
                    var f = assets.widgets.const.assetfield.css;
                    this.data.tab = tab;
                    this.$tabs.find('.' + c.base + c._tab).removeClass('active');
                    this.$tabs.find('.' + c.base + c._tab + '[data-key="' + tab + '"]').addClass('active');
                    c = config.const.form.css;
                    this.$panel.html(content);
                    this.form = core.getWidget(this.$panel, '.' + c.base + c._form, config.FormWidget, {
                        holder: this.editor,
                        scope: tab,
                        variation: this.data.variation,
                        rendition: this.data.rendition
                    });
                    window.widgets.setUp(this.$panel);
                    this.data.config = this.form.$el.data('config');
                    this.editor.setExample(this.form.$el.data('example'), forceReload);
                }, this));
            },

            deleteConfig: function (event, path) {
                if (event) {
                    event.preventDefault();
                }
                var u = config.const.dialog.url;
                core.openFormDialog(u.base + u._delete + core.encodePath(this.editor.data.config),
                    components.FormDialog, {}, undefined, _.bind(function () {
                        this.editor.reload();
                    }, this));
                return false;
            },

            addVariation: function (event, path) {
                if (event) {
                    event.preventDefault();
                }
                var u = config.const.dialog.url;
                core.openFormDialog(u.base + u._create + u._variation + core.encodePath(this.editor.data.config),
                    components.FormDialog, {}, undefined, _.bind(function () {
                        this.editor.reload();
                    }, this));
                return false;
            },

            removeVariation: function (event, path) {
                if (event) {
                    event.preventDefault();
                }
                var u = config.const.dialog.url;
                core.openFormDialog(u.base + u._remove + u._variation + core.encodePath(this.editor.data.config)
                    + '?variation=' + encodeURIComponent(this.data.variation),
                    components.FormDialog, {}, undefined, _.bind(function () {
                        this.editor.reload();
                    }, this));
                return false;
            },

            addRendition: function (event, path) {
                if (event) {
                    event.preventDefault();
                }
                var u = config.const.dialog.url;
                var url = u.base + u._create + u._rendition + core.encodePath(this.editor.data.config)
                    + '?variation=' + encodeURIComponent(this.data.variation);
                core.openFormDialog(url, config.AddRenditionDialog, {
                    config: this.editor.data.config
                }, undefined, _.bind(function () {
                    this.editor.reload();
                }, this));
                return false;
            },

            removeRendition: function (event, path) {
                if (event) {
                    event.preventDefault();
                }
                var u = config.const.dialog.url;
                var url = u.base + u._remove + u._rendition + core.encodePath(this.editor.data.config)
                    + '?variation=' + encodeURIComponent(this.data.variation)
                    + '&rendition=' + encodeURIComponent(this.data.rendition);
                core.openFormDialog(url, config.RemoveRenditionDialog, {
                    config: this.editor.data.config
                }, undefined, _.bind(function () {
                    this.editor.reload();
                }, this));
                return false;
            }
        });

        config.ConfigPreview = Backbone.View.extend({

            initialize: function (options) {
                var c = config.const.preview.css;
                var f = assets.widgets.const.assetfield.css;
                this.editor = options.editor;
                this.data = {
                    timestamp: new Date().getTime()
                };
                this.image = core.getWidget(this.$el, '.' + c.config + c._image, assets.widgets.AssetPreviewWidget);
                this.path = core.getWidget(this.$el, '.' + f.base, assets.widgets.AssetFieldWidget);
                if (this.editor.data.type === 'imageconfig') {
                    this.path.setDisabled(true);
                } else {
                    this.path.setValue(assets.profile.get('config', 'example'));
                }
                this.path.changed('ConfigPreview', _.bind(this.adjustPreview, this));
                this.$('.' + c.config + c._clear).click(_.bind(function () {
                    this.path.setValue('', true);
                }, this));
                this.$('.' + c.config + c._refresh).click(_.bind(function () {
                    this.adjustPreview(undefined, true);
                }, this));
            },

            setExample: function (path, forceReload) {
                if (this.editor.data.type === 'imageconfig') {
                    path = this.editor.data.path; // ignore each example if an image is shown
                }
                this.data.example = path;
                this.path.$input.attr('placeholder', path);
                this.adjustPreview(undefined, forceReload);
            },

            adjustPreview: function (event, forceReload) {
                var src = '';
                if (event) {
                    assets.profile.set('config', 'example', this.path.getValue());
                }
                if (forceReload) {
                    this.data.timestamp = new Date().getTime();
                }
                var path = this.path.getValue() || this.data.example;
                if (path) {
                    var url = new core.SlingUrl(core.encodePath(path));
                    url.selectors = ['asset',
                        this.editor.getVariation() || 'default',
                        this.editor.getRendition() || 'default'];
                    if (this.editor.data.path) {
                        url.suffix = core.encodePath(this.editor.data.path);
                    }
                    url.parameters.ts = this.data.timestamp;
                    src = url.build();
                }
                this.image.adjustImage(src);
            }
        });

        config.ConfigEditor = Backbone.View.extend({

            initialize: function (options) {
                var c = config.const.general.css;
                this.data = {
                    holder: options.holder,
                    path: this.$el.data('path'),
                    config: this.$el.data('config'),
                    base: this.$el.data('base'),
                    scope: this.$el.data('scope'),
                    type: this.$el.data('type'),
                    valid: this.$el.data('valid')
                };
                this.configPreview = core.getWidget(this.$el, '.' + c.base + c._preview, config.ConfigPreview, {
                    editor: this
                });
                this.configForm = core.getWidget(this.$el, '.' + c.base + c._form, config.ConfigForm,
                    {
                        editor: this
                    });
            },

            reload: function () {
                if (this.data.holder && _.isFunction(this.data.holder.refresh)) {
                    this.data.holder.refresh();
                } else {
                    window.location.reload();
                }
            },

            getVariation: function () {
                return this.configForm.data.variation;
            },

            getRendition: function () {
                return this.configForm.data.rendition;
            },

            setExample: function (path, forceReload) {
                this.configPreview.setExample(path, forceReload);
            },

            adjustPreview: function () {
                this.configPreview.adjustPreview();
            }
        });

    })(composum.assets.config, composum.assets, core.components);

})();
