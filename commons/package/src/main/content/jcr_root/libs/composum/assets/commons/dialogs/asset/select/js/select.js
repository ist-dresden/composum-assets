/**
 *
 *
 */
(function () {
    'use strict';
    core.namespace('composum.assets.dialogs');

    (function (dialogs, assets, components) {

        dialogs.const = _.extend(dialogs.const || {}, {
            select: {
                css: {
                    base: 'composum-assets-dialogs-asset-select',
                    assetfield: 'composum-assets-widget-assetfield',
                    navigator: 'composum-assets-widget-navigator'
                }
            }
        });

        dialogs.AssetSelectDialog = components.AbstractPathSelectDialog.extend({

            initialize: function (options) {
                var c = dialogs.const.select.css;
                components.AbstractPathSelectDialog.prototype.initialize.call(this,
                    _.extend(options, {
                        // init input field as an asset field instead of a path field
                        inputSelector: '.' + c.assetfield, inputType: assets.widgets.AssetFieldWidget
                    }));
                this.navigator = core.getWidget(this.$el, '.' + c.navigator,
                    assets.navigator.NavigatorWidget, options);
                this.navigator.$el.on('change', _.bind(this.onNavigatorChange, this));
                this.navigator.$el.on('treepanel', _.bind(this.onTreePanelChange, this));
                this.$('button.select').click(_.bind(function () {
                    if (_.isFunction(this.callback)) {
                        this.callback(this.getValue());
                    }
                    this.hide();
                }, this));
                this.onTreePanelChange(); // init dialog according to the initial tree view mode of the navigator
            },

            /**
             * @extends core.components.AbstractPathSelectDialog
             */
            setFilter: function (filter) {
                components.AbstractPathSelectDialog.prototype.setFilter.call(this, filter);
                this.navigator.setFilter(filter);
                if (filter) {
                    this.navigator.disableFilterWidget();
                }
            },

            /**
             * @override the callback on each change in the input field
             */
            inputChanged: function () {
                this.navigator.setValue(this.getValue(), false);
            },

            onNavigatorChange: function () {
                this.setValue(this.navigator.getValue());
            },

            onTreePanelChange: function () {
                if (this.navigator.treeMode === 'panel') {
                    this.$('.modal-dialog').addClass('modal-lg');
                } else {
                    this.$('.modal-dialog').removeClass('modal-lg');
                }
            }
        });

    })(composum.assets.dialogs, composum.assets, core.components);

})();
