/**
 *
 *
 */
(function () {
    'use strict';
    CPM.namespace('assets.dialogs');

    (function (dialogs, assets, core) {

        dialogs.getAssetUploadDialog = function () {
            return core.getView('#asset-upload-dialog', assets.AssetUploadDialog);
        };

        dialogs.getCreateFolderDialog = function () {
            return core.getView('#create-folder-dialog', assets.CreateFolderDialog);
        };

        dialogs.AssetUploadDialog = core.components.Dialog.extend({

            initialize: function (options) {
                core.components.Dialog.prototype.initialize.apply(this, [options]);
                this.form = core.getWidget(this.el, 'form.widget-form', core.components.FormWidget);
                this.type = core.getWidget(this.el, '.type-selection .select-buttons-widget',
                    core.components.SelectButtonsWidget);
                this.file = core.getWidget(this.el, '.file-upload-widget',
                    core.components.FileUploadWidget);
                this.$path = this.$('input[name="path"]');
                this.$variation = this.$('select[name="variation"]');
                this.$name = this.$('input[name="name"]');
                this.$file = this.$('input[name="file"]');
                this.$file.on('change.file', _.bind(this.fileChanged, this));
                this.$('button.upload').click(_.bind(this.uploadAsset, this));
            },

            initParentPath: function (path) {
                this.$path.val(path);
            },

            resetOnShown: function () {
                core.components.Dialog.prototype.resetOnShown.apply(this);
                this.type.setValue(core.console.getProfile().get('assets', 'createType', 'asset'));
                this.file.grabFocus();
            },

            uploadAsset: function (event) {
                event.preventDefault();
                if (this.form.isValid()) {
                    var parentPath = this.$path.val();
                    var newNodeName = this.$name.val();
                    var newNodePath = core.buildContentPath(parentPath, newNodeName);
                    var type = this.type.getValue();
                    core.console.getProfile().set('assets', 'createType', type);
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

        dialogs.CreateFolderDialog = core.components.Dialog.extend({

            initialize: function (options) {
                core.components.Dialog.prototype.initialize.apply(this, [options]);
                this.form = core.getWidget(this.el, 'form.widget-form', core.components.FormWidget);
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

    })(composum.assets.dialogs, composum.assets, core);

})();
