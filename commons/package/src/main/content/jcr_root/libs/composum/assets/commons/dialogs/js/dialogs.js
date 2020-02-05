/**
 *
 *
 */
(function () {
    'use strict';
    core.namespace('composum.assets.dialogs');

    (function (dialogs, assets, components) {

        dialogs.const = _.extend(dialogs.const || {}, {
            asset: {
                css: {
                    base: 'assets-dialog-form',
                    _path: '_path',
                    _name: '_name',
                    _file: '_file'
                },
                url: {
                    base: '/libs/composum/assets/commons/dialogs/asset',
                    _create: '/create.html',
                    _delete: '/delete.html',
                    _upload: '/upload.html'
                }
            },
            folder: {
                url: {
                    base: '/libs/composum/assets/commons/dialogs/folder',
                    _create: '/create.html'
                }
            }
        });

        /**
         * the dialog to upload asset originals
         */
        dialogs.AssetsFormDialog = components.FormDialog.extend({

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
        dialogs.AssetUploadDialog = dialogs.AssetsFormDialog.extend({

            initView: function () {
                var c = dialogs.const.asset.css;
                dialogs.AssetsFormDialog.prototype.initView.apply(this);
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
        dialogs.AssetCreateDialog = dialogs.AssetUploadDialog.extend({});

        /**
         * the dialog to delete an asset
         */
        dialogs.AssetDeleteDialog = dialogs.AssetsFormDialog.extend({

            initView: function () {
                var c = dialogs.const.asset.css;
                dialogs.AssetsFormDialog.prototype.initView.apply(this);
                this.$path = this.$('.' + c.base + c._path + ' input');
            },

            setPath: function (path) {
                this.$path.val(path);
            }
        });

        /**
         * the dialog to manage an assets configuration
         */
        dialogs.AssetConfigDialog = dialogs.AssetsFormDialog.extend({

            initView: function () {
                dialogs.AssetsFormDialog.prototype.initView.apply(this);
            }
        });

        /**
         * the dialog to manage an assets configuration folder node
         */
        dialogs.AssetsConfigurationDialog = dialogs.AssetsFormDialog.extend({

            initView: function () {
                dialogs.AssetsFormDialog.prototype.initView.apply(this);
            }
        });

    })(composum.assets.dialogs, composum.assets, core.components);

})();
