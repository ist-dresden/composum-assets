/**
 *
 *
 */
(function (window) {
    'use strict';

    window.composum = window.composum || {};
    window.composum.assets = window.composum.assets || {};

    (function (assets, core) {

        assets.profile = assets.profile || new core.LocalProfile('composum.assets');

        assets.commons = assets.commons || {};

        (function (commons, assets, core) {

            commons.const = _.extend(commons.const || {}, {
                preview: {
                    mode: {
                        base: 'assets-preview-mode',
                        _light: '-light',
                        _dark: '-dark'
                    }
                }
            });

            commons.togglePreviewMode = function () {
                var c = commons.const.preview.mode;
                var $body = $('body');
                if ($body.is('.' + c.base + c._light)) {
                    $body.removeClass(c.base + c._light).addClass(c.base + c._dark);
                } else if ($body.is('.' + c.base + c._dark)) {
                    $body.removeClass(c.base + c._dark).addClass(c.base + c._light);
                }
            };

        })(assets.commons, assets, core);

    })(window.composum.assets, window.core);

})(window);
