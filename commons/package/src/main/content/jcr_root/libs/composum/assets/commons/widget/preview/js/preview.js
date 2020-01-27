/**
 *
 *
 */
(function (window) {
    'use strict';
    window.composum = window.composum || {};
    window.composum.assets = window.composum.assets || {};
    window.composum.assets.widgets = window.composum.assets.widgets || {};

    (function (widgets, assets, core) {

        widgets.const = _.extend(widgets.const || {}, {
            preview: {
                css: {
                    base: 'composum-assets-widget-preview',
                    _lightbox: '_lightbox',
                    _wrapper: '_wrapper',
                    _frame: '_frame'
                },
                url: {
                    base: '/libs/composum/assets/commons/widget/preview',
                    _suffix: '.suffix.html'
                }
            }
        });

        widgets.AssetPreviewWidget = window.widgets.Widget.extend({

            initialize: function (options) {
                var c = widgets.const.preview.css;
                window.widgets.Widget.prototype.initialize.call(this, options);
                this.$image = this.$('img');
                this.$el.click(assets.commons.togglePreviewMode);
            },

            setValue: function (value, triggerChange) {
                var oldValue = this.getValue();
                core.components.PathWidget.prototype.setValue.call(this, value, triggerChange);
                if (triggerChange || oldValue !== this.getValue()) {
                    this.adjust();
                }
            },

            adjustImage: function (src) {
                if (this.$image && this.$image.length > 0) {
                    if (this.$image.attr('src') !== src) {
                        this.$el.parent().addClass('loading');
                        this.$image.attr('src', src);
                        this.$image.on('load.ConfigPreview', _.bind(function () {
                            this.$image.off('load.ConfigPreview');
                            this.$el.parent().removeClass('loading');
                        }, this));
                    }
                }
            },

            adjust: function () {
                var value = this.getValue();
                if (value) {
                    var u = widgets.const.assetfield.url.content;
                    core.getHtml(u.base + u._suffix + value,
                        _.bind(function (content, result, xhr) {
                            if (xhr.status === 200) {
                                this.$el.html(content);
                                this.$el.removeClass('empty-value');
                                this.validAsset = true;
                            } else {
                                this.clear();
                            }
                        }, this), _.bind(function () {
                            this.clear();
                        }, this));
                } else {
                    this.clear();
                }
            },

            clear: function () {
                this.setValue();
                this.validAsset = false;
                this.$el.addClass('empty-value');
                this.$el.html('');
            }
        });

        window.widgets.register('.widget.asset-preview-widget', widgets.AssetPreviewWidget);

    })(window.composum.assets.widgets, window.composum.assets, window.core);

})(window);
