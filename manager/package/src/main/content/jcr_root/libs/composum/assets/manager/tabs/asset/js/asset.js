/**
 *
 *
 */
(function () {
    'use strict';
    CPM.namespace('assets.manager');

    (function (manager, assets, core) {

        manager.const = _.extend(manager.const || {}, {
            originals: {
                css: {
                    base: 'composum-assets-manager-image',
                    _original: '_original',
                    _view: '_original-view'
                }
            },
            renditions: {
                css: {
                    base: 'composum-assets-manager-image',
                    _renditions: '_renditions'
                }
            }
        });

        manager.AssetTab = manager.AbstractManagerTab.extend({

            initialize: function (options) {
                manager.AbstractManagerTab.prototype.initialize.call(this, options);
                this.initContent();
                this.$detailActions.find('.transform').click(_.bind(this.toSimpeImage, this));
                this.$('.detail-toolbar .add').click(_.bind(this.uploadOriginal, this));
                this.$('.detail-toolbar .remove').click(_.bind(this.removeOriginal, this));
                this.$('.detail-toolbar .delete').click(_.bind(this.deleteAsset, this));
            },

            initContent: function () {
                var c = assets.widgets.const.preview.css;
                this.$('.accordion-item').each(function () {
                    core.getWidget(this, '.' + c.base + c._lightbox, assets.widgets.AssetPreviewWidget);
                });
                manager.AbstractManagerTab.prototype.initContent.call(this);
            },

            toSimpeImage: function (event) {
                if (event) {
                    event.preventDefault();
                }
                core.ajaxPost('/bin/cpm/assets/assets.toSimpleImage.json' + this.data.path, {}, {}, _.bind(function () {
                    this.resetView();
                }, this));
                return false;
            },

            uploadOriginal: function (event) {

            },

            removeOriginal: function (event) {
            },

            deleteAsset: function (event) {
                assets.treeActions.deleteNode(event);
            }
        });

        manager.AssetOriginalsTab = manager.AssetTab.extend({

            initContent: function () {
                var c = manager.const.originals.css;
                var w = assets.widgets.const.preview.css;
                this.$('.' + c.base + c._view).each(function () {
                    core.getWidget(this, '.' + w.base + w._lightbox, assets.widgets.AssetPreviewWidget);
                });
                manager.AssetTab.prototype.initContent.call(this);
            }
        });

        manager.AssetRenditionsTab = manager.AssetTab.extend({

            initContent: function () {
                var c = manager.const.renditions.css;
                manager.AssetTab.prototype.initContent.call(this);
                core.getWidget(this.$el, '.' + c.base + c._renditions, assets.widgets.AssetRenditions);
            }
        });

    })(composum.assets.manager, composum.assets, core);

})();
