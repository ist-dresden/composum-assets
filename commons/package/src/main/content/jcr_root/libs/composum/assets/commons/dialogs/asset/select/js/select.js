/**
 *
 *
 */
(function (window) {
    'use strict';
    window.composum = window.composum || {};
    window.composum.assets = window.composum.assets || {};
    window.composum.assets.dialogs = window.composum.assets.dialogs || {};

    (function (dialogs, assets, core) {

        dialogs.const = _.extend(dialogs.const || {}, {
            select: {
                css: {
                    base: 'composum-assets-dialogs-asset-select',
                    assetfield: 'composum-assets-widget-assetfield',
                    navigator: 'composum-assets-widget-navigator'
                }
            }
        });

        dialogs.AssetSelectDialog = core.components.AbstractPathSelectDialog.extend({

            initialize: function (options) {
                var c = dialogs.const.select.css;
                core.components.AbstractPathSelectDialog.prototype.initialize.call(this,
                    _.extend(options, {
                        // init input field as an asset field instead of a path field
                        inputSelector: '.' + c.assetfield, inputType: assets.widgets.AssetFieldWidget
                    }));
                this.navigator = core.getWidget(this.$el, '.' + c.navigator, assets.navigator.NavigatorWidget);
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

    })(window.composum.assets.dialogs, window.composum.assets, window.core);

})(window);
