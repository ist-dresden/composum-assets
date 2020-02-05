/**
 *
 *
 */
(function () {
    'use strict';
    core.namespace('composum.assets.commons');

    (function (commons, assets) {

        assets.profile = assets.profile || new core.LocalProfile('composum.assets');

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

    })(composum.assets.commons, composum.assets);

})();
