/**
 *
 *
 */
(function () {
    'use strict';
    CPM.namespace('assets.dialogs');

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
                    _upload: '/upload.html',
                    _delete: '/delete.html',
                    _config: '/config.html'
                }
            },
            folder: {
                url: {
                    base: '/libs/composum/assets/commons/dialogs/folder',
                    _create: '/create.html',
                    _delete: '/delete.html',
                    _config: '/config.html'
                }
            }
        });

        assets.profile = assets.profile || new CPM.core.LocalProfile('composum.assets');

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
        dialogs.AssetCreateDialog = dialogs.AssetUploadDialog.extend({

            initView: function () {
                dialogs.AssetUploadDialog.prototype.initView.call(this);
                this.type = core.getWidget(this.el, '.type-selection .select-buttons-widget',
                    core.components.SelectButtonsWidget);
                this.file = core.getWidget(this.el, '.file-upload-widget',
                    core.components.FileUploadWidget);
                this.$path = this.$('input[name="path"]');
                this.$variation = this.$('select[name="variation"]');
                this.$name = this.$('input[name="name"]');
                this.$file = this.$('input[name="file"]');
                this.type.setValue(assets.profile.get('asset', 'createType', 'asset'));
                this.$file.on('change.file', _.bind(this.fileChanged, this));
                this.$('button.upload').click(_.bind(this.uploadAsset, this));
                this.$el.on('shown.bs.modal', _.bind(function () {
                    this.$file.focus();
                }, this));
            },

            initParentPath: function (path) {
                this.setPath(path);
            },

            uploadAsset: function (event) {
                event.preventDefault();
                if (this.form.isValid()) {
                    var parentPath = this.$path.val();
                    var newNodeName = this.$name.val();
                    var newNodePath = core.buildContentPath(parentPath, newNodeName);
                    var type = this.type.getValue();
                    assets.profile.set('asset', 'createType', type);
                    this.form.$el.attr('action', this.form.$el.data(type + '-action'));
                    this.submitForm(function (result) {
                        $(document).trigger("path:inserted", [parentPath, newNodeName]);
                        $(document).trigger("path:select", [newNodePath]);
                    });
                } else {
                    this.alert('danger', 'a parent path and name must be specified');
                }
                return false;
            },

            fileChanged: function () {
                var fileWidget = this.widgetOf(this.$file);
                var nameWidget = this.widgetOf(this.$name);
                var value = fileWidget.getValue();
                if (value) {
                    var name = nameWidget.getValue();
                    if (!name) {
                        var match = /^(.*[\\\/])?([^\\\/]+)$/.exec(value);
                        nameWidget.setValue([match[2]]);
                    }
                }
            }
        });

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
        dialogs.FolderConfigDialog = dialogs.AssetsFormDialog.extend({

            initView: function () {
                dialogs.AssetsFormDialog.prototype.initView.apply(this);
            }
        });

        dialogs.FolderCreateDialog = dialogs.AssetsFormDialog.extend({

            initView: function () {
                dialogs.AssetsFormDialog.prototype.initView.call(this);
                this.ordered = core.getWidget(this.el, 'input[name="ordered"]', core.components.SelectWidget);
                this.$path = this.$('input[name="path"]');
                this.$type = this.$('input[name="type"]');
                this.$name = this.$('input[name="name"]');
                this.form.onsubmit = _.bind(this.createFolder, this);
                this.$('button.create').click(_.bind(this.createFolder, this));
                this.$el.on('shown.bs.modal', _.bind(function () {
                    this.$name.focus();
                }, this));
            },

            initParentPath: function (path) {
                this.$path.val(path);
            },

            createFolder: function (event) {
                event.preventDefault();
                if (this.form.isValid()) {
                    this.$type.val(this.ordered.getValue() ? 'sling:OrderedFolder' : 'sling:Folder');
                    var parentPath = this.$path.val();
                    var newNodeName = this.$name.val();
                    var newNodePath = core.buildContentPath(parentPath, newNodeName);
                    this.submitForm(function (result) {
                        $(document).trigger("path:inserted", [parentPath, newNodeName]);
                        $(document).trigger("path:select", [newNodePath]);
                    });
                } else {
                    this.alert('danger', 'a parent path, type and name must be specified');
                }
                return false;
            }
        });

        dialogs.FolderDeleteDialog = dialogs.AssetsFormDialog.extend({

            initView: function () {
                dialogs.AssetsFormDialog.prototype.initView.apply(this);
            }
        });

    })(CPM.assets.dialogs, CPM.assets, CPM.core.components);

})();
