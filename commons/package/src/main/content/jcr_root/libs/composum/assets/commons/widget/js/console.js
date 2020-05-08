/**
 *
 *
 */
(function () {
    'use strict';
    CPM.namespace('assets.console');

    (function (console, assets) {

        console.const = _.extend(console.const || {}, {
            preview: {
                mode: {
                    base: 'assets-preview-mode',
                    _light: '-light',
                    _dark: '-dark'
                }
            }
        });

        console.togglePreviewMode = function () {
            var c = console.const.preview.mode;
            var $body = $('body');
            if ($body.is('.' + c.base + c._light)) {
                $body.removeClass(c.base + c._light).addClass(c.base + c._dark);
            } else if ($body.is('.' + c.base + c._dark)) {
                $body.removeClass(c.base + c._dark).addClass(c.base + c._light);
            }
        };

    })(CPM.assets.console, CPM.assets);

})();
