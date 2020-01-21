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
                    _rendition: '-rendition'
                },
                url: {
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
                    _image: '_file_asset'
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

            onChange: function (event) {
                core.components.FormWidget.prototype.onChange.call(this, event);
            }
        });

        config.ConfigForm = Backbone.View.extend({

            initialize: function (options) {
                var c = config.const.general.css;
                this.editor = options.editor;
                this.$tabs = this.$('.' + c.base + c._tabs);
                this.$panel = this.$('.' + c.base + c._panel);
                this.variationSelect = core.getWidget(this.$el, '.' + c.base + c._select + c._variation, core.components.SelectWidget);
                this.renditionSelect = core.getWidget(this.$el, '.' + c.base + c._select + c._rendition, core.components.SelectWidget);
                this.variationSelect.setValue(assets.profile.get('config', 'variation', this.variationSelect.getValue()));
                this.renditionSelect.setValue(assets.profile.get('config', 'rendition', this.renditionSelect.getValue()));
                this.variationSelect.onChange('ConfigForm', _.bind(this.selectVariation, this));
                this.renditionSelect.onChange('ConfigForm', _.bind(this.selectRendition, this));
                this.selectTab(undefined, assets.profile.get('config', 'configTab', 'node'));
                this.$tabs.find('a').click(_.bind(this.selectTab, this));
            },

            selectVariation: function (event, key) {
                if (event) {
                    if (!key) {
                        key = this.variationSelect.getValue();
                    }
                }
                if (this.variation !== key) {
                    this.variation = key;
                    assets.profile.set('config', 'variation', key);
                }
            },

            selectRendition: function (event, key) {
                if (event) {
                    if (!key) {
                        key = this.renditionSelect.getValue();
                    }
                }
                if (this.rendition !== key) {
                    this.rendition = key;
                    assets.profile.set('config', 'rendition', key);
                }
            },

            selectTab: function (event, tab) {
                var c = config.const.general.css;
                if (event) {
                    event.preventDefault();
                    if (!tab) {
                        var $tab = $(event.currentTarget).closest('.' + c.base + c._tab);
                        tab = $tab.data('key');
                    }
                }
                if (tab && this.currentTab !== tab) {
                    if (this.form && this.form.isChanged()) {
                        this.saveForm(_.bind(function () {
                            this.loadForm(tab);
                        }, this));
                    } else {
                        this.loadForm(tab);
                    }
                }
                return false;
            },

            loadForm: function (tab) {
                var u = config.const.general.url.form;
                var path = this.editor.data.path;
                var url = new core.SlingUrl(u.base + '.' + tab + '.html');
                url.suffix = path;
                core.getHtml(url.build(), _.bind(function (content) {
                    var c = config.const.general.css;
                    this.currentTab = tab;
                    assets.profile.set('config', 'configTab', tab);
                    this.$tabs.find('.' + c.base + c._tab).removeClass('active');
                    this.$tabs.find('.' + c.base + c._tab + '[data-key="' + tab + '"]').addClass('active');
                    c = config.const.form.css;
                    this.$panel.html(content);
                    this.form = core.getWidget(this.$panel, '.' + c.base + c._form, config.FormWidget);
                    this.formTabs = core.getWidget(this.$panel, '.' + c.base + c._content, config.FormTabs);
                    window.widgets.setUp(this.$panel);
                }, this));
            },

            saveForm: function (onSuccess) {
                if (this.form) {
                    this.form.doFormSubmit(_.bind(function (type, label, message, hint) {
                        // TODO validation message...
                    }, this), onSuccess);
                }
            }
        });

        config.ConfigPreview = Backbone.View.extend({

            initialize: function (options) {
                var c = config.const.preview.css;
                this.$image = this.$('.' + c.base + c._image);
                this.adjustPreview();
            },

            adjustPreview: function () {
                var url = new core.SlingUrl(core.encodePath('/content/composum/prototype/assets/demo/site-3/extended/image-04.jpg'));
                url.selectors = ['asset', 'vertical', 'half-scale'];
                url.suffix = core.encodePath('/content/composum/prototype/assets/demo/site-1/jcr:content/assetconfig');
                this.$image.attr('src', url.build());
            }
        });

        config.ConfigEditor = Backbone.View.extend({

            initialize: function (options) {
                var c = config.const.general.css;
                this.data = {
                    path: this.$el.data('path')
                };
                this.configPreview = core.getWidget(this.$el, '.' + c.base + c._preview, config.ConfigPreview);
                this.configForm = core.getWidget(this.$el, '.' + c.base + c._form, config.ConfigForm,
                    {
                        editor: this
                    });
            }
        });

    })(window.composum.assets.config, window.composum.assets, window.core);

})(window);
