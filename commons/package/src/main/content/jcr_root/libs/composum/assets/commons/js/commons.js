/**
 *
 *
 */
(function (window) {
    'use strict';

    window.composum = window.composum || {};
    window.composum.assets = window.composum.assets || {};

    (function (assets, core) {

        assets.const = _.extend(assets.const || {}, {
            commons: {
                css: {
                    dialog: {
                        base: 'assets-dialog-form',
                        _path: '_path',
                        _name: '_name',
                        _file: '_file'
                    }
                }
            }
        });

        assets.profile = assets.profile || new core.LocalProfile('composum.assets');

        /**
         * the dialog to upload asset originals
         */
        assets.AssetsFormDialog = core.components.FormDialog.extend({

            /**
             * the submit handler called after a successful validation
             */
            doSubmit: function () {
                this.submitForm(_.bind(this.triggerEvents, this));
            },

            /**
             * fire the appropriate events after submit...
             */
            triggerEvents: function (result, defaultEvents) {
            }
        });

        /**
         * the dialog to upload asset originals
         */
        assets.AssetUploadDialog = assets.AssetsFormDialog.extend({

            initView: function () {
                var c = assets.const.commons.css.dialog;
                assets.AssetsFormDialog.prototype.initView.apply(this);
                this.$path = this.$('.' + c.base + c._path + ' input');
                this.$name = this.$('.' + c.base + c._name + ' input');
                this.$file = this.$('.' + c.base + c._file + ' input');
            },

            setPath: function (path) {
                this.$path.val(path);
            }
        });

        /**
         * the dialog to create a new asset
         */
        assets.AssetCreateDialog = assets.AssetUploadDialog.extend({});

        /**
         * the dialog to delete an asset
         */
        assets.AssetDeleteDialog = assets.AssetsFormDialog.extend({

            initView: function () {
                var c = assets.const.commons.css.dialog;
                assets.AssetsFormDialog.prototype.initView.apply(this);
                this.$path = this.$('.' + c.base + c._path + ' input');
            },

            setPath: function (path) {
                this.$path.val(path);
            }
        });

        /**
         * the dialog to manage an assets configuration
         */
        assets.AssetConfigDialog = assets.AssetsFormDialog.extend({

            initView: function () {
                assets.AssetsFormDialog.prototype.initView.apply(this);
            }
        });

        /**
         * the dialog to manage an assets configuration folder node
         */
        assets.AssetsConfigurationDialog = assets.AssetsFormDialog.extend({

            initView: function () {
                assets.AssetsFormDialog.prototype.initView.apply(this);
            }
        });

    })(window.composum.assets, window.core);

})(window);
