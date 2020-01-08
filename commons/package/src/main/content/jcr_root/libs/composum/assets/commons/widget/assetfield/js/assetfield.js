/**
 * the 'assetfield' widget behaviour impelementation
 */
(function (window) {
    'use strict';
    window.composum = window.composum || {};
    window.composum.assets = window.composum.assets || {};
    window.composum.assets.widgets = window.composum.assets.widgets || {};

    (function (widgets, assets, core) {

        widgets.const = _.extend(widgets.const || {}, {
            assetfield: {
                css: {
                    base: 'composum-assets-widget-assetfield',
                    _preview: '_preview'
                },
                url: {
                    content: {
                        base: '/libs/composum/assets/commons/widget/assetfield',
                        _preview: '.preview.html'
                    },
                    dialogs: {
                        base: '/libs/composum/assets/commons/dialogs',
                        _select: '/asset/select.html'
                    }
                }
            }
        });

        widgets.AssetFieldWidget = core.components.PathWidget.extend({

            /**
             * @extends core.components.PathWidget
             */
            initialize: function (options) {
                var c = widgets.const.assetfield.css;
                core.components.PathWidget.prototype.initialize.call(this, options);
                this.$preview = this.$('.' + c.base + c._preview);
                this.$el.on('change', _.bind(this.adjustPreview, this));
            },

            /**
             * the callback for the '.select' button opens the asset select dialog with the asset navigator
             * @override core.components.PathWidget
             */
            selectPath: function (event) {
                var u = widgets.const.assetfield.url.dialogs;
                core.getHtml(u.base + u._select,
                    _.bind(function (content) {
                        var selectDialog = core.addLoadedDialog(assets.dialogs.AssetSelectDialog, content, {
                            rootPath: this.getRootPath(),
                            filter: this.getFilter()
                        });
                        this.openDialog(selectDialog);
                    }, this));
            },

            /**
             * adjusts the preview image according to the value if change is not triggered
             * @extends core.components.PathWidget
             */
            setValue: function (value, triggerChange) {
                core.components.PathWidget.prototype.setValue.call(this, value, triggerChange);
                if (!triggerChange) { // otherwise the preview is adjuted during change handling
                    this.adjustPreview();
                }
            },

            /**
             * @extends Widget returns the asset validation (preview response) result
             */
            extValidate: function (value) {
                return this.validAsset;
            },

            adjustPreview: function () {
                var value = this.getValue();
                if (value) {
                    var u = widgets.const.assetfield.url.content;
                    core.getHtml(u.base + u._preview + value,
                        _.bind(function (content, result, xhr) {
                            if (xhr.status === 200) {
                                this.$preview.html(content);
                                this.$preview.removeClass('empty-value');
                                this.validAsset = true;
                            } else {
                                this.clearPreview();
                            }
                        }, this), _.bind(function () {
                            this.clearPreview();
                        }, this));
                } else {
                    this.clearPreview();
                }
            },

            clearPreview: function () {
                this.validAsset = false;
                this.$preview.addClass('empty-value');
                this.$preview.html('');
            }
        });

        window.widgets.register('.' + widgets.const.assetfield.css.base, widgets.AssetFieldWidget);

    })(window.composum.assets.widgets, window.composum.assets, window.core);

})(window);
