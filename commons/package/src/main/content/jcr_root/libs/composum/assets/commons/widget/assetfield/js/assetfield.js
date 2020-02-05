/**
 * the 'assetfield' widget behaviour impelementation
 */
(function () {
    'use strict';
    CPM.namespace('assets.widgets');

    (function (widgets, assets, components) {

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

        widgets.AssetFieldWidget = components.PathWidget.extend({

            /**
             * @extends components.PathWidget
             */
            initialize: function (options) {
                var c = widgets.const.assetfield.css;
                this.$preview = this.$('.' + c.base + c._preview);
                components.PathWidget.prototype.initialize.call(this, options);
                this.$el.on('change', _.bind(this.adjustPreview, this));
            },

            /**
             * the callback for the '.select' button opens the asset select dialog with the asset navigator
             * @override components.PathWidget
             */
            selectPath: function (event) {
                if (!this.isDisabled()) {
                    var u = widgets.const.assetfield.url.dialogs;
                    core.getHtml(u.base + u._select,
                        _.bind(function (content) {
                            var selectDialog = core.addLoadedDialog(assets.dialogs.AssetSelectDialog, content, {
                                rootPath: this.getRootPath(),
                                filter: this.getFilter()
                            });
                            this.openDialog(selectDialog);
                        }, this));
                }
            },

            /**
             * adjusts the preview image according to the value if change is not triggered
             * @extends components.PathWidget
             */
            setValue: function (value, triggerChange) {
                components.PathWidget.prototype.setValue.call(this, value, triggerChange);
                if (!triggerChange) { // otherwise the preview is adjuted during change handling
                    this.adjustPreview();
                }
            },

            /**
             * @extends Widget returns the asset validation (preview response) result
             */
            extValidate: function (value) {
                return this.validAsset === undefined || !!this.validAsset;
            },

            adjustPreview: function () {
                if (this.$preview && this.$preview.length > 0) {
                    this.validAsset = undefined;
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
                                    this.validAsset = false;
                                    this.clearPreview();
                                }
                            }, this), _.bind(function () {
                                this.validAsset = false;
                                this.clearPreview();
                            }, this));
                    } else {
                        this.clearPreview();
                    }
                }
            },

            clearPreview: function () {
                if (this.$preview && this.$preview.length > 0) {
                    this.$preview.addClass('empty-value');
                    this.$preview.html('');
                }
            }
        });

        window.widgets.register('.widget.assetfield-widget', widgets.AssetFieldWidget);

    })(composum.assets.widgets, composum.assets, core.components);

})();
