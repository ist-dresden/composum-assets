/**
 *
 *
 */
(function (window) {
    'use strict';

    window.composum = window.composum|| {};
    window.composum.assets = window.composum.assets || {};

    (function (assets, widgets, core) {

        assets.SelectWidget = widgets.Widget.extend({

            initialize: function (options) {
                widgets.Widget.prototype.initialize.apply(this, [options]);
                this.pathWidget = core.getWidget(this.el, ".path-input", core.components.PathWidget);
                this.$tabsNav = this.$('.asset-select-widget_tabs-nav');
                this.tabWidgets = {};
                this.tabWidgets['finder'] = core.getWidget(this.el, ".asset-finder-widget", assets.FinderWidget);
                this.tabWidgets['browser'] = core.getWidget(this.el, ".asset-browser-widget", assets.BrowserWidget);
                //this.tabWidgets['tree'] = core.getWidget(this.el, ".asset-tree-widget", assets.TreeWidget);
                this.tabWidgets['finder'].setPathWidget(this.pathWidget);
                this.tabWidgets['browser'].setPathWidget(this.pathWidget);
                this.$tabsNav.find('a[data-toggle="tab"]').on('shown.bs.tab', _.bind(this.tabSelected, this));
                this.setUp();
            },

            setUp: function () {
                this.selectTab(assets.profile.get('select', 'tab', 'finder'));
            },

            selectTab: function (tabName) {
                this.$tabsNav.find('[data-name="' + tabName + '"]').tab('show');
            },

            tabSelected: function (event) {
                var $oldTab = $(event.relatedTarget);
                var oldTabName = $oldTab.data('name');
                var oldWidget = this.tabWidgets[oldTabName];
                if (oldWidget && _.isFunction(oldWidget.deactivate)) {
                    oldWidget.deactivate();
                }
                var $newTab = $(event.target);
                var newTabName = $newTab.data('name');
                var newWidget = this.tabWidgets[newTabName];
                if (newWidget && _.isFunction(newWidget.activate)) {
                    newWidget.activate();
                }
                assets.profile.set('select', 'tab', newTabName);
            },

            getValue: function () {
                return this.pathWidget.getValue();
            },

            setValue: function (value, triggerChange) {
                this.pathWidget.setValue(value, triggerChange);
            }
        });

        widgets.register('.widget.asset-select-widget', assets.SelectWidget);

    })(window.composum.assets, window.widgets, window.core);

})(window);
