/**
 *
 *
 */
(function () {
    'use strict';
    CPM.namespace('assets.manager.page');

    (function (page, assets, core) {

        page.const = _.extend(page.const || {}, {
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

        page.AssetOriginalsPageView = Backbone.View.extend({

            initialize: function (options) {
                var c = page.const.originals.css;
                var w = assets.widgets.const.preview.css;
                this.$('.' + c.base + c._view).each(function () {
                    core.getWidget(this, '.' + w.base + w._lightbox, assets.widgets.AssetPreviewWidget);
                });
                window.widgets.setUp();
            }
        });

        page.AssetRenditionsPageView = Backbone.View.extend({

            initialize: function (options) {
                var c = page.const.renditions.css;
                core.getWidget(this.$el, '.' + c.base + c._renditions, assets.widgets.AssetRenditions);
                window.widgets.setUp();
            }
        });

    })(composum.assets.manager.page, composum.assets, core);

})();
