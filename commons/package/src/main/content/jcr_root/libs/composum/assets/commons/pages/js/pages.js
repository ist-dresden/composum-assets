/**
 *
 *
 */
(function () {
    'use strict';
    CPM.namespace('assets.pages');

    (function (pages, assets, dialogs) {

        pages.const = {

            uri: {
                dialog: {
                    commons: '/libs/composum/assets/commons/dialogs',
                    asset: {
                        _create: '/asset/create.html',
                        _upload: '/asset/upload.html',
                        _delete: '/asset/delete.html',
                        _config: '/asset/config.html'
                    },
                    folder: {
                        _create: '/folder/create.html',
                        _delete: '/folder/delete.html',
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

                delete: function (event, name, path, type) {
                    pages.openAssetDeleteDialog(name, path, type);
                },

                config: function (event, name, path, type) {
                    pages.openAssetConfigDialog(name, path, type);
                }
            },

            folder: {

                create: function (event, name, path, type) {
                    pages.openFolderCreateDialog(name, path, type);
                },

                delete: function (event, name, path, type) {
                    pages.openFolderDeleteDialog(name, path, type);
                },

                config: function (event, name, path, type) {
                    pages.openFolderConfigDialog(name, path, type);
                }
            }
        };

        pages.AssetUploadDialog = dialogs.AssetUploadDialog.extend({

            triggerEvents: function (result, defaultEvents) {
                CPM.pages.actions.dialog.triggerEvents(this, result, defaultEvents);
            },

            getDefaultSuccessEvents: function () {
                return CPM.pages.const.event.content.changed;
            }
        });

        pages.AssetCreateDialog = dialogs.AssetCreateDialog.extend({

            getDefaultSuccessEvents: function () {
                return CPM.pages.const.event.content.inserted;
            }
        });

        pages.AssetDeleteDialog = dialogs.AssetDeleteDialog.extend({

            triggerEvents: function (result, defaultEvents) {
                CPM.pages.actions.dialog.triggerEvents(this, result, defaultEvents);
            },

            getDefaultSuccessEvents: function () {
                return CPM.pages.const.event.content.deleted;
            }
        });

        pages.openAssetCreateDialog = function (name, path, type, setupDialog) {
            var u = pages.const.uri.dialog;
            CPM.pages.dialogHandler.openEditDialog(u.commons + u.asset._create,
                pages.AssetCreateDialog, name, path, type, undefined/*context*/, function (dialog) {
                    dialog.setPath(path);
                    if (_.isFunction(setupDialog)) {
                        setupDialog(dialog);
                    }
                });
        };

        pages.openAssetUploadDialog = function (name, path, type, setupDialog) {
            var u = pages.const.uri.dialog;
            CPM.pages.dialogHandler.openEditDialog(u.commons + u.asset._upload,
                pages.AssetUploadDialog, name, path, type, undefined/*context*/, function (dialog) {
                    dialog.setPath(path);
                    if (_.isFunction(setupDialog)) {
                        setupDialog(dialog);
                    }
                });
        };

        pages.openAssetDeleteDialog = function (name, path, type, setupDialog) {
            var u = pages.const.uri.dialog;
            CPM.pages.dialogHandler.openEditDialog(u.commons + u.asset._delete,
                pages.AssetDeleteDialog, name, path, type, undefined/*context*/, function (dialog) {
                    dialog.setPath(path);
                    if (_.isFunction(setupDialog)) {
                        setupDialog(dialog);
                    }
                });
        };

        pages.openAssetConfigDialog = function (name, path, type, setupDialog) {
            var u = pages.const.uri.dialog;
            CPM.pages.dialogHandler.openEditDialog(u.commons + u.asset._config,
                dialogs.AssetConfigDialog, name, path, type, undefined/*context*/, setupDialog);
        };

        pages.openFolderCreateDialog = function (name, path, type, setupDialog) {
            var u = pages.const.uri.dialog;
            CPM.pages.dialogHandler.openEditDialog(u.commons + u.folder._create,
                dialogs.FolderCreateDialog, name, path, type, undefined/*context*/, setupDialog);
        };

        pages.openFolderDeleteDialog = function (name, path, type, setupDialog) {
            var u = pages.const.uri.dialog;
            CPM.pages.dialogHandler.openEditDialog(u.commons + u.folder._delete,
                dialogs.FolderDeleteDialog, name, path, type, undefined/*context*/, setupDialog);
        };

        pages.openFolderConfigDialog = function (name, path, type, setupDialog) {
            var u = pages.const.uri.dialog;
            CPM.pages.dialogHandler.openEditDialog(u.commons + u.folder._config,
                dialogs.FolderConfigDialog, name, path, type, undefined/*context*/, setupDialog);
        };

    })(CPM.assets.pages, CPM.assets, CPM.assets.dialogs);

})();
