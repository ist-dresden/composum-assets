/**
 *
 *
 */
(function () {
    'use strict';
    CPM.namespace('assets.widgets');

    (function (widgets, assets) {

        widgets.const = _.extend(widgets.const || {}, {
            renditions: {
                css: {
                    base: 'composum-assets-manager-image',
                    preview: {
                        base: 'composum-assets-widget-preview',
                        _rendition: '_rendition',
                        _frame: '_frame'
                    },
                    _variation: '_variation-panel',
                    _rendition: '_rendition-panel'
                }
            }
        });

        widgets.AssetRenditions = window.widgets.Widget.extend({

            initialize: function (options) {
                var c = widgets.const.renditions.css;
                var p = widgets.const.renditions.css.preview;
                window.widgets.Widget.prototype.initialize.call(this, options);
                this.$('.' + c.base + c._variation).each(function () {
                    var $variation = $(this);
                    $variation.on('shown.bs.collapse', function (event) {
                        var $target = $(event.target);
                        if ($target.is('.' + c.base + c._variation)) { // is triggered on rendition open also...
                            assets.profile.set('variation', $target.data('key'), 'open');
                            $target.find('.' + c.base + c._rendition).each(function () {
                                var $rendition = $(this);
                                $rendition.on('shown.bs.collapse', function (event) {
                                    var $rendition = $(event.target);
                                    assets.profile.set('rendition', $rendition.data('key'), 'open');
                                    $rendition.find('.' + p.base + p._frame).click(assets.commons.togglePreviewMode);
                                    var $image = $rendition.find('.' + p.base + p._rendition);
                                    $image.attr('src', $image.data('src'));
                                });
                                $rendition.on('hidden.bs.collapse', function (event) {
                                    assets.profile.set('rendition', $(event.target).data('key'), 'closed');
                                });
                                if (assets.profile.get('rendition', $rendition.data('key'), 'closed') === 'open') {
                                    $rendition.collapse('show');
                                }
                            });
                        }
                    });
                    $variation.on('hidden.bs.collapse', function (event) {
                        var $target = $(event.target);
                        if ($target.is('.' + c.base + c._variation)) { // is triggered on rendition close also...
                            assets.profile.set('variation', $target.data('key'), 'closed');
                            $variation.find('.' + c.base + c._rendition).each(function () {
                                var $rendition = $(this);
                                $rendition.off('shown.bs.collapse');
                                $rendition.off('hidden.bs.collapse');
                            });
                        }
                    });
                    if (assets.profile.get('variation', $variation.data('key'), 'open') === 'open') {
                        $variation.collapse('show');
                    }
                });
            }
        });

    })(composum.assets.widgets, composum.assets);

})();
