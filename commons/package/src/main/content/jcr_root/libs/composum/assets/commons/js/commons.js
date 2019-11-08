/**
 *
 *
 */
(function (window) {
    'use strict';

    window.composum = window.composum|| {};
    window.composum.assets = window.composum.assets || {};

    (function (assets, core) {

        assets.profile = new core.LocalProfile('composum.assets');

    })(window.composum.assets, window.core);

})(window);
