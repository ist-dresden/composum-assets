/**
 *
 *
 */
(function (window) {
    'use strict';
    window.composum = window.composum || {};
    window.composum.assets = window.composum.assets || {};
    window.composum.assets.preview = window.composum.assets.preview || {};

    (function (preview, assets, widgets, core) {

        preview.const = _.extend(preview.const || {}, {
            css: {
                base: 'composum-assets-widget-preview'
            }
        });

    })(window.composum.assets.preview, window.composum.assets, window.widgets, window.core);

})(window);
