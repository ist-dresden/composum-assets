/**
 * the 'assetfield' widget behaviour impelementation
 */
(function (window) {
    'use strict';
    window.composum = window.composum || {};
    window.composum.assets = window.composum.assets || {};
    window.composum.assets.widgets = window.composum.assets.widgets || {};

    (function (widgets, assets, core) {

        widgets.const = _.extend(widgets.const || {}, {
            assetfilter: {
                css: {
                    base: 'composum-assets-widget-filter',
                    _dropdown: '_dropdown',
                    _menu: '_menu'
                }
            }
        });

        /**
         * the 'hybrid' filter key select widget which can be also a dropdown menu (<a ... data-value="key">...</a>)
         */
        widgets.AssetFilterWidget = core.components.SelectWidget.extend({

            /**
             * @extends core.components.PathWidget
             */
            initialize: function (options) {
                var c = widgets.const.assetfilter.css;
                core.components.SelectWidget.prototype.initialize.call(this, options);
                if (this.$el.is('.' + c.base + c._dropdown)) {
                    this.$menu = this.$('.' + c.base + c._menu);
                    this.$menu.find('a').click(_.bind(function (event) {
                        this.setValue($(event.currentTarget).data('value'), true);
                    }, this));
                }
            },

            /**
             * @extends core.components.SelectWidget
             */
            setValue: function (value, triggerChange) {
                core.components.SelectWidget.prototype.setValue.call(this, value, triggerChange);
                if (this.$menu) {
                    this.$menu.find('li').removeClass('active');
                    this.$menu.find('a[data-value="' + value + '"]').parent().addClass('active');
                }
            }
        });

        window.widgets.register('.' + widgets.const.assetfilter.css.base, widgets.AssetFilterWidget);

    })(window.composum.assets.widgets, window.composum.assets, window.core);

})(window);
