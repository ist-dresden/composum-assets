/**
 * the 'config' widget behaviour impelementation
 */
(function (window) {
    'use strict';
    window.composum = window.composum || {};
    window.composum.assets = window.composum.assets || {};
    window.composum.assets.config = window.composum.assets.config || {};

    (function (config, assets, core) {

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
                        base: '/libs/composum/assets/commons/widget/config'
                    },
                    form: {
                        base: '/libs/composum/assets/commons/widget/config/form'
                    },
                    dialogs: {
                        base: '/libs/composum/assets/commons/dialogs',
                        _delete: '/config/delete.html'
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
                    _panels: '_panels'
                }
            }
        });

        config.FormTabs = Backbone.View.extend({

            initialize: function (options) {
                var c = config.const.form.css;
                this.$tabs = this.$('.' + c.base + c._tabs);
                this.$panels = this.$('.' + c.base + c._panels);
                this.$tabs.find('a[data-key="' + assets.profile.get('config', 'formTab', 'general') + '"]').tab('show');
                this.$tabs.find('a').on('shown.bs.tab.FormTabs', _.bind(this.tabSelected, this));
            },

            tabSelected: function (event) {
                var $tab = $(event.target);
                assets.profile.set('config', 'formTab', $tab.data('key'));
            }
        });

        config.FormWidget = core.components.FormWidget.extend({

            onChanged: function (event) {
                core.components.FormWidget.prototype.onChanged.call(this, event);
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
                if (!this.editor.data.valid) {
                    this.$tabs.find('.' + c.base + c._tab + c.tabs._valid).addClass('disabled');
                }
                if (this.editor.data.base || this.editor.data.valid) {
                    this.variationSelect = core.getWidget(this.$el, '.' + c.base + c._select + c._variation, core.components.SelectWidget);
                    this.renditionSelect = core.getWidget(this.$el, '.' + c.base + c._select + c._rendition, core.components.SelectWidget);
                    this.variationSelect.changed('ConfigForm', _.bind(this.selectVariation, this));
                    this.renditionSelect.changed('ConfigForm', _.bind(this.selectRendition, this));
                    this.variationSelect.setValue(assets.profile.get('config', 'variation', this.variationSelect.getValue()), true);
                    this.$tabs.find('a').click(_.bind(this.selectTab, this));
                }
            },

            selectVariation: function (event, key) {
                if (event) {
                    if (!key) {
                        key = this.variationSelect.getValue();
                    }
                }
                if (this.data.variation !== key) {
                    this.data.variation = key;
                    assets.profile.set('config', 'variation', key);
                    this.data.rendition = undefined;
                    this.loadRenditions();
                }
            },

            selectRendition: function (event, key) {
                if (event) {
                    if (!key) {
                        key = this.renditionSelect.getValue();
                    }
                }
                if (this.data.rendition !== key) {
                    this.data.rendition = key;
                    assets.profile.set('config', 'rendition', key);
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
                        tab = this.adjustTab($tab.data('key'));
                    }
                }
                if ((tab && this.data.tab !== tab) || force) {
                    if (this.form && this.form.isChanged()) {
                        this.saveForm(_.bind(function () {
                            this.loadForm(tab || this.data.tab);
                        }, this));
                    } else {
                        this.loadForm(tab || this.data.tab);
                    }
                }
                return false;
            },

            loadUrl: function (uri) {
                var url = new core.SlingUrl(uri + '.html');
                if (this.data.variation) {
                    url.parameters.variation = this.data.variation;
                }
                if (this.data.rendition) {
                    url.parameters.rendition = this.data.rendition;
                }
                url.suffix = this.editor.data.path;
                return url.build();
            },

            loadRenditions: function () {
                core.getHtml(this.loadUrl(config.const.general.url.config.base + '.renditions'),
                    _.bind(function (content) {
                        this.renditionSelect.$el.html(content);
                        this.renditionSelect.setValue(
                            assets.profile.get('config', 'rendition', this.renditionSelect.getValue()), true);
                    }, this));
            },

            loadForm: function (tab) {
                core.getHtml(this.loadUrl(config.const.general.url.form.base + '.' + tab),
                    _.bind(function (content) {
                        var c = config.const.general.css;
                        var f = assets.widgets.const.assetfield.css;
                        this.data.tab = tab;
                        assets.profile.set('config', 'configTab', tab);
                        this.$tabs.find('.' + c.base + c._tab).removeClass('active');
                        this.$tabs.find('.' + c.base + c._tab + '[data-key="' + tab + '"]').addClass('active');
                        c = config.const.form.css;
                        this.$panel.html(content);
                        this.form = core.getWidget(this.$panel, '.' + c.base + c._form, config.FormWidget);
                        this.formTabs = core.getWidget(this.$panel, '.' + c.base + c._content, config.FormTabs);
                        window.widgets.setUp(this.$panel);
                        this.editor.setExample(this.form.$el.data('example'));
                    }, this));
            },

            saveForm: function (onSuccess) {
                if (false && this.form) { // FIXME
                    this.form.doFormSubmit(_.bind(function (type, label, message, hint) {
                        // TODO validation message...
                    }, this), onSuccess);
                } else {
                    if (_.isFunction(onSuccess)) {
                        onSuccess();
                    }
                }
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
                this.path.setValue(assets.profile.get('config', 'example'));
                this.path.changed('ConfigPreview', _.bind(this.adjustPreview, this));
                this.$('.' + c.config + c._clear).click(_.bind(function () {
                    this.path.setValue('', true);
                }, this));
                this.$('.' + c.config + c._refresh).click(_.bind(function () {
                    this.adjustPreview(undefined, true);
                }, this));
            },

            setExample: function (path) {
                this.data.example = path;
                this.path.$input.attr('placeholder', path);
                this.adjustPreview();
            },

            adjustPreview: function (event, force) {
                var src = '';
                if (event) {
                    assets.profile.set('config', 'example', this.path.getValue());
                }
                if (force) {
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
                    path: this.$el.data('path'),
                    base: this.$el.data('base'),
                    scope: this.$el.data('scope'),
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

            getVariation: function () {
                return this.configForm.data.variation;
            },

            getRendition: function () {
                return this.configForm.data.rendition;
            },

            setExample: function (path) {
                this.configPreview.setExample(path);
            },

            adjustPreview: function () {
                this.configPreview.adjustPreview();
            }
        });

    })(window.composum.assets.config, window.composum.assets, window.core);

})(window);
