/**
 *
 *
 */
(function (window) {
    'use strict';

    window.assets = window.assets || {};

    (function (assets, core) {

        assets.profile = new core.LocalProfile('composum.assets');

    })(window.assets, window.core);

})(window);
