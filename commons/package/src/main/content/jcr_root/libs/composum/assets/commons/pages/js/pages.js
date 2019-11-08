/**
 *
 *
 */
(function (window) {
    'use strict';

    window.composum = window.composum || {};
    window.composum.assets = window.composum.assets || {};
    window.composum.assets.pages = window.composum.assets.pages || {};

    (function (pages, assets, core) {

        pages.const = {

            uri: {
                dialog: {
                    commons: '/libs/composum/assets/commons/dialogs',
                    asset: {
                        _create: '/asset/create.html',
                        _upload: '/asset/upload.html',
                        _config: '/asset/config.html'
                    },
                    folder: {
                        _config: '/folder/config.html'
                    }
                }
            }
        };

        pages.actions = {

            asset: {

                create: function (event, name, path, type) {
                    pages.openAssetCreateDialog(name, path, type);
                },

                upload: function (event, name, path, type) {
                    pages.openAssetUploadDialog(name, path, type);
                },

                config: function (event, name, path, type) {
                    pages.openAssetConfigDialog(name, path, type);
                }
            },

            folder: {

                config: function (event, name, path, type) {
                    pages.openAssetsConfigurationDialog(name, path, type);
                }
            }
        };

        pages.AssetUploadDialog = assets.AssetUploadDialog.extend({

            triggerEvents: function (result, defaultEvents) {
                window.composum.pages.actions.dialog.triggerEvents(this, result, defaultEvents);
            },

            getDefaultSuccessEvents: function () {
                return window.composum.pages.const.event.content.changed;
            }
        });

        pages.AssetCreateDialog = pages.AssetUploadDialog.extend({

            getDefaultSuccessEvents: function () {
                return window.composum.pages.const.event.content.inserted;
            }
        });

        pages.openAssetCreateDialog = function (name, path, type, setupDialog) {
            var u = pages.const.uri.dialog;
            window.composum.pages.dialogHandler.openEditDialog(u.commons + u.asset._create,
                pages.AssetCreateDialog, name, path, type, undefined/*context*/, function (dialog) {
                    dialog.setPath(path);
                });
        };

        pages.openAssetUploadDialog = function (name, path, type, setupDialog) {
            var u = pages.const.uri.dialog;
            window.composum.pages.dialogHandler.openEditDialog(u.commons + u.asset._upload,
                pages.AssetUploadDialog, name, path, type, undefined/*context*/, function (dialog) {
                    dialog.setPath(path);
                });
        };

        pages.openAssetConfigDialog = function (name, path, type, setupDialog) {
            var u = pages.const.uri.dialog;
            window.composum.pages.dialogHandler.openEditDialog(u.commons + u.asset._config,
                assets.AssetConfigDialog, name, path, type, undefined/*context*/, setupDialog);
        };

        pages.openAssetsConfigurationDialog = function (name, path, type, setupDialog) {
            var u = pages.const.uri.dialog;
            window.composum.pages.dialogHandler.openEditDialog(u.commons + u.folder._config,
                assets.AssetsConfigurationDialog, name, path, type, undefined/*context*/, setupDialog);
        };

    })(window.composum.assets.pages, window.composum.assets, window.core);

})(window);
