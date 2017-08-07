/**
 *
 *
 */
(function (window) {
    'use strict';

    window.assets = window.assets || {};

    (function (assets, core) {

        assets.const = assets.const || {};
        assets.const.config = assets.const.config || {};
        assets.const.config.dialog = {
            key: {
                file: {
                    jpg: {
                        quality: 'file.jpg.quality'
                    }
                },
                size: {
                    width: 'size.width',
                    height: 'size.height',
                    aspectRatio: 'size.aspectRatio'
                },
                crop: {
                    horizontal: 'crop.horizontal',
                    vertical: 'crop.vertical',
                    scale: 'crop.scale'
                },
                watermark: {
                    text: '"watermark.text',
                    font: {
                        family: 'watermark.font.family',
                        size: 'watermark.font.size',
                        bold: 'watermark.font.bold',
                        italic: 'watermark.font.italic'
                    },
                    horizontal: 'watermark.horizontal',
                    vertical: 'watermark.vertical',
                    color: 'watermark.color',
                    alpha: 'watermark.alpha'
                },
                transformation: {
                    blur: {
                        factor: 'transformation.blur.factor'
                    }
                },
                example: {
                    image: {
                        path: 'example.image.path'
                    }
                }
            },
            css: {
                title: 'h4.modal-title',
                subtitle: 'h5.modal-subtitle',
                general: {
                    extension: '.extension-checkbox.widget',
                    categories: '.multi-form-widget[data-name="categories"]'
                },
                file: {
                    jpg: {
                        quality: '[name="file.jpg.quality"]'
                    }
                },
                size: {
                    width: '[name="size.width"]',
                    height: '[name="size.height"]',
                    aspectRatio: '[name="size.aspectRatio"]'
                },
                crop: {
                    horizontal: '[name="crop.horizontal"]',
                    vertical: '[name="crop.vertical"]',
                    scale: '[name="crop.scale"]'
                },
                watermark: {
                    text: '[name="watermark.text"]',
                    font: {
                        family: '[name="watermark.font.family"]',
                        size: '[name="watermark.font.size"]',
                        bold: '[name="watermark.font.bold"]',
                        italic: '[name="watermark.font.italic"]'
                    },
                    horizontal: '[name="watermark.horizontal"]',
                    vertical: '[name="watermark.vertical"]',
                    color: '[name="watermark.color"]',
                    alpha: '[name="watermark.alpha"]'
                },
                transformation: {
                    blur: {
                        factor: '[name="transformation.blur.factor"]'
                    }
                },
                example: {
                    image: {
                        path: '[name="example.image.path"]'
                    }
                },
                form: {
                    group: '.form-group',
                    path: '[name="path"]',
                    parent: '.config-path',
                    context: '.config-context',
                    name: '.config-name'
                },
                el: {
                    path: '.config-path'
                },
                class: {
                    inherited: 'inherited',
                    type: {
                        asset: 'type-asset',
                        variation: 'type-variation',
                        rendition: 'type-rendition'
                    }
                }
            },
            pattern: {
                path: new RegExp('^((.+)/jcr:content/)(.+)$')
            }
        };

        assets.getAssetConfigDialog = function () {
            return core.getView('#asset-config-dialog', assets.AssetConfigDialog);
        };

        assets.openAssetConfigDialog = function (path, callback) {
            var dialog = assets.getAssetConfigDialog();
            dialog.show(_.bind(function () {
                dialog.callback = callback;
                if (path) {
                    dialog.initPath(path);
                }
            }, this));
        };

        assets.AssetConfigDialog = core.components.Dialog.extend({

            initialize: function (options) {
                core.components.Dialog.prototype.initialize.apply(this, [options]);
                var c = assets.const.config.dialog;
                this.form = core.getWidget(this.el, 'form.widget-form', core.components.FormWidget);
                this.$title = this.$(c.css.title);
                this.$subtitle = this.$(c.css.subtitle);
                this.general = {
                    extension: core.getWidget(this.el, c.css.general.extension, core.components.CheckboxWidget),
                    categories: core.getWidget(this.el, c.css.general.categories, core.components.MultiFormWidget)
                };
                this.file = {
                    quality: core.getWidget(this.el, c.css.file.jpg.quality, core.components.TextFieldWidget)
                };
                this.size = {
                    width: core.getWidget(this.el, c.css.size.width, core.components.TextFieldWidget),
                    height: core.getWidget(this.el, c.css.size.height, core.components.TextFieldWidget),
                    aspectRatio: core.getWidget(this.el, c.css.size.aspectRatio, core.components.TextFieldWidget)
                };
                this.crop = {
                    horizontal: core.getWidget(this.el, c.css.crop.horizontal, core.components.TextFieldWidget),
                    vertical: core.getWidget(this.el, c.css.crop.vertical, core.components.TextFieldWidget),
                    scale: core.getWidget(this.el, c.css.crop.scale, core.components.TextFieldWidget)
                };
                this.watermark = {
                    text: core.getWidget(this.el, c.css.watermark.text, core.components.TextFieldWidget),
                    font: {
                        family: core.getWidget(this.el, c.css.watermark.font.family, core.components.TextFieldWidget),
                        size: core.getWidget(this.el, c.css.watermark.font.size, core.components.TextFieldWidget),
                        bold: core.getWidget(this.el, c.css.watermark.font.bold, core.components.CheckboxWidget),
                        italic: core.getWidget(this.el, c.css.watermark.font.italic, core.components.CheckboxWidget)
                    },
                    horizontal: core.getWidget(this.el, c.css.watermark.horizontal, core.components.TextFieldWidget),
                    vertical: core.getWidget(this.el, c.css.watermark.vertical, core.components.TextFieldWidget),
                    color: core.getWidget(this.el, c.css.watermark.color, core.components.ColorpickerWidget),
                    alpha: core.getWidget(this.el, c.css.watermark.alpha, core.components.TextFieldWidget)
                };
                this.transformation = {
                    blur: {
                        factor: core.getWidget(this.el, c.css.transformation.blur.factor, core.components.TextFieldWidget)
                    }
                };
                this.example = {
                    image: {
                        path: core.getWidget(this.el, c.css.example.image.path, core.components.PathWidget)
                    }
                };
                this.$('button.save').click(_.bind(this.save, this));
            },

            initPath: function (path) {
                if (path) {
                    var c = assets.const.config.dialog;
                    this.form.$el.attr('action', path);
                    core.getJson('/bin/cpm/assets/assets.config.json' + path,
                        _.bind(function (result) {
                            this.data = result;
                            this.data.base = undefined;
                            var matcher = c.pattern.path.exec(this.data.path);
                            if (matcher) {
                                this.data.base = matcher[1];
                                this.$title.text(matcher[3].replace(/\//g, ' / '));
                                this.$subtitle.text(matcher[2]);
                            } else {
                                this.$title.text('???');
                                this.$subtitle.text(this.data.path);
                            }
                            if (typeof this.data.extension !== 'undefined') {
                                this.general.extension.$el.removeClass('hidden');
                                this.general.extension.declareName('extension');
                                this.general.extension.setValue(this.data.extension);
                            } else {
                                this.general.extension.$el.addClass('hidden');
                                this.general.extension.declareName(undefined);
                            }
                            this.general.categories.setValue(this.data.categories);
                            this.setWidgetValue(this.file.quality, this.data.file, c.key.file.jpg.quality);
                            this.setWidgetValue(this.size.width, this.data.size, c.key.size.width);
                            this.setWidgetValue(this.size.height, this.data.size, c.key.size.height);
                            this.setWidgetValue(this.size.aspectRatio, this.data.size, c.key.size.aspectRatio);
                            this.setWidgetValue(this.crop.horizontal, this.data.crop, c.key.crop.horizontal);
                            this.setWidgetValue(this.crop.vertical, this.data.crop, c.key.crop.vertical);
                            this.setWidgetValue(this.crop.scale, this.data.crop, c.key.crop.scale);
                            this.setWidgetValue(this.watermark.text, this.data.watermark, c.key.watermark.text);
                            this.setWidgetValue(this.watermark.font.family, this.data.watermark, c.key.watermark.font.family);
                            this.setWidgetValue(this.watermark.font.size, this.data.watermark, c.key.watermark.font.size);
                            this.setWidgetValue(this.watermark.font.bold, this.data.watermark, c.key.watermark.font.bold);
                            this.setWidgetValue(this.watermark.font.italic, this.data.watermark, c.key.watermark.font.italic);
                            this.setWidgetValue(this.watermark.horizontal, this.data.watermark, c.key.watermark.horizontal);
                            this.setWidgetValue(this.watermark.vertical, this.data.watermark, c.key.watermark.vertical);
                            this.setWidgetValue(this.watermark.color, this.data.watermark, c.key.watermark.color);
                            this.setWidgetValue(this.watermark.alpha, this.data.watermark, c.key.watermark.alpha);
                            this.setWidgetValue(this.transformation.blur.factor, this.data.transformation, c.key.transformation.blur.factor);
                            this.setWidgetValue(this.example.image.path, this.data.example, c.key.example.image.path);
                        }, this));
                }
            },

            save: function (event) {
                event.preventDefault();
                if (this.form.isValid()) {
                    this.submitForm(_.bind(function () {
                        if (_.isFunction(this.callback)) {
                            this.callback(this.data);
                        }
                    }, this));
                }
                return false;
            },

            setWidgetValue: function (widget, data, key) {
                var c = assets.const.config.dialog;
                var $group = widget.$el.closest(c.css.form.group);
                var $path = $group.find(c.css.el.path);
                $path.text('');
                $group.removeClass(c.css.class.inherited);
                if (data && data[key]) {
                    var value = data[key].value;
                    if (data[key].inherited) {
                        widget.setValue(undefined);
                        widget.setDefaultValue(value);
                        $group.addClass(c.css.class.inherited);
                        $path.text(this.getRelativePath(data[key].path));
                    } else {
                        widget.setValue(value);
                        widget.setDefaultValue(undefined);
                    }
                } else {
                    widget.setValue(undefined);
                    widget.setDefaultValue(undefined);
                }
            },

            getRelativePath: function (path) {
                if (this.data && this.data.base) {
                    if (path.indexOf(this.data.base) === 0) {
                        return path.substring(this.data.base.length);
                    }
                }
                return path;
            }
        });

        assets.AssetConfigActionDialog = core.components.Dialog.extend({

            initialize: function (options) {
                core.components.Dialog.prototype.initialize.apply(this, [options]);
                var c = assets.const.config.dialog;
                this.form = core.getWidget(this.el, 'form.widget-form', core.components.FormWidget);
                this.fields = {
                    path: core.getWidget(this.el, c.css.form.path, core.components.TextFieldWidget),
                    parent: core.getWidget(this.el, c.css.form.parent, core.components.TextFieldWidget),
                    context: core.getWidget(this.el, c.css.form.context, core.components.TextFieldWidget),
                    name: core.getWidget(this.el, c.css.form.name, core.components.TextFieldWidget)
                };
            },

            setConfigType: function (type) {
                var c = assets.const.config.dialog;
                this.$el.removeClass(c.css.class.type.asset);
                this.$el.removeClass(c.css.class.type.variation);
                this.$el.removeClass(c.css.class.type.rendition);
                this.$el.addClass(c.css.class.type[type]);
            },

            initContext: function (path, type) {
                this.setConfigType(type);
                var aspects = new RegExp('^(/.*)/jcr:content(/(.*))?$').exec(path);
                this.fields.parent.setValue(aspects[1]);
                this.fields.context.setValue(aspects[3]);
            },

            submit: function (event) {
                event.preventDefault();
                if (this.form.isValid()) {
                    this.submitForm(_.bind(function () {
                        if (_.isFunction(this.callback)) {
                            this.callback(this.data);
                        }
                    }, this));
                }
                return false;
            }
        });

        assets.AssetConfigCreateDialog = assets.AssetConfigActionDialog.extend({

            initialize: function (options) {
                assets.AssetConfigActionDialog.prototype.initialize.apply(this, [options]);
                this.$('button.create').click(_.bind(this.submit, this));
            },

            initPath: function (path, type) {
                this.fields.path.setValue(path);
                this.initContext(path, type);
                this.fields.name.setValue('');
            }
        });

        assets.AssetConfigDeleteDialog = assets.AssetConfigActionDialog.extend({

            initialize: function (options) {
                var c = assets.const.config.dialog;
                assets.AssetConfigActionDialog.prototype.initialize.apply(this, [options]);
                this.$('button.delete').click(_.bind(this.submit, this));
            },

            initPath: function (path, type) {
                var parentAndName = core.getParentAndName(path);
                this.fields.path.setValue(path);
                this.initContext(parentAndName.path, type);
                this.fields.name.setValue(parentAndName.name);
            }
        });

        assets.getAssetConfigCreateDialog = function () {
            return core.getView('#create-asset-config-dialog', assets.AssetConfigCreateDialog);
        };

        assets.openAssetConfigCreateDialog = function (path, type, callback) {
            var dialog = assets.getAssetConfigCreateDialog();
            dialog.show(_.bind(function () {
                dialog.callback = callback;
                dialog.initPath(path, type);
            }, this));
        };

        assets.getAssetConfigDeleteDialog = function () {
            return core.getView('#delete-asset-config-dialog', assets.AssetConfigDeleteDialog);
        };

        assets.openAssetConfigDeleteDialog = function (path, type, callback) {
            var dialog = assets.getAssetConfigDeleteDialog();
            dialog.show(_.bind(function () {
                dialog.callback = callback;
                dialog.initPath(path, type);
            }, this));
        };

    })(window.assets, window.core);

})(window);
