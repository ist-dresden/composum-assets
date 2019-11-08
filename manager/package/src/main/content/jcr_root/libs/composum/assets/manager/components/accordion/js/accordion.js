/**
 * Accordion component behaviour
 */
(function(window) {
    'use strict';

    window.composum = window.composum|| {};
    window.composum.assets = window.composum.assets || {};
    window.composum.assets.widgets = window.composum.assets.widgets || {};

    (function (widgets, assets, core) {

        widgets.const = _.extend(widgets.const || {}, {
            accordion: {
                css: {
                    selector: '.accordion-item.widget'
                }
            }
        });

        /**
         * the 'accordion-widget' (window.core.components.AccordionPanel)
         * possible attributes:
         */
        widgets.AccordionItem = Backbone.View.extend({

            initialize: function (options) {
                this.$handle = this.$('> .panel-heading a');
                this.$collapsible = this.$('> .collapse');
                this.$handle.click(_.bind(this.toggle, this));
            },

            toggle: function (panel) {
                this.$collapsible.collapse('toggle');
            },

            show: function (panel) {
                this.$collapsible.collapse('show');
            },

            hide: function (panel) {
                this.$collapsible.collapse('hide');
            }
        });

        window.widgets.register(widgets.const.accordion.css.selector, widgets.AccordionItem, {
        });

    })(window.composum.assets.widgets, window.composum.assets, window.core);

})(window);
