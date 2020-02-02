/**
 *
 *
 */
(function (window) {
    'use strict';
    window.composum = window.composum || {};
    window.composum.assets = window.composum.assets || {};
    window.composum.assets.config = window.composum.assets.config || {};

    (function (config, assets, core) {

        config.const = _.extend(config.const || {}, {
            dialog: {
                css: {
                    title: 'h4.modal-title',
                    subtitle: 'h5.modal-subtitle',
                    base: 'composum-assets-dialogs-config',
                    _content: '_content',
                    _variation: '_variation'
                },
                pattern: {
                    path: new RegExp('^((.+)/jcr:content/)(.+)$')
                },
                url: {
                    base: '/libs/composum/assets/commons/dialogs/config',
                    _change: '/change.',
                    _create: '/create',
                    _remove: '/delete',
                    _variation: '/variation.variation.html',
                    _rendition: '/rendition.rendition.html',
                    _reload: '/rendition.rendition.reload.html',
                    _delete: '/delete.html'
                }
            }
        });

        config.ChangeDialog = core.components.FormDialog.extend({

            initialize: function (options) {
                core.components.FormDialog.prototype.initialize.call(this, _.extend({
                    formType: config.FormWidget
                }, options));
            }
        });

        config.RenditionDialog = core.components.FormDialog.extend({

            initialize: function (options) {
                var c = config.const.dialog.css;
                core.components.FormDialog.prototype.initialize.call(this, options);
                this.data = {
                    config: options.config,
                    reload: options.reload
                };
                this.$content = this.$('.' + c.base + c._content);
                this.initContent();
            },

            initContent: function (event, variation) {
                var c = config.const.dialog.css;
                window.widgets.setUp(this.$content);
                this.variationSelect = core.getWidget(this.$el, '.' + c.base + c._variation, core.components.SelectWidget);
                this.variationSelect.changed('ConfigDialog', _.bind(this.onVariationSelect, this));
            },

            onVariationSelect: function (event, variation) {
                var url = this.data.reload + core.encodePath(this.data.config)
                    + '?variation=' + encodeURIComponent(variation);
                core.getHtml(url, _.bind(function (content) {
                    this.$content.html(content);
                    this.initContent();
                }, this));
            }
        });

        config.AddRenditionDialog = config.RenditionDialog.extend({

            initialize: function (options) {
                var u = config.const.dialog.url;
                config.RenditionDialog.prototype.initialize.call(this, _.extend({
                    reload: u.base + u._create + u._reload
                }, options));
            }
        });

        config.RemoveRenditionDialog = config.RenditionDialog.extend({

            initialize: function (options) {
                var u = config.const.dialog.url;
                config.RenditionDialog.prototype.initialize.call(this, _.extend({
                    reload: u.base + u._remove + u._reload
                }, options));
            }
        });

    })(window.composum.assets.config, window.composum.assets, window.core);

})(window);
