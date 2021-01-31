<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<cpn:component var="model" type="com.composum.assets.commons.widget.FilterModel">
    <div class="composum-assets-widget-filter composum-assets-widget-filter_dropdown ${model.dropdownType}">
        <button id="${model.domId}" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"
                class="composum-assets-widget-filter_button btn btn-default fa fa-filter">
            <select class="composum-assets-widget-filter_input input hidden">
                <c:forEach items="${model.filterSet}" var="filter">
                    <option value="${cpn:value(filter.key)}">${cpn:text(filter.label)}</option>
                </c:forEach>
            </select>
        </button>
        <ul class="composum-assets-widget-filter_menu dropdown-menu dropdown-menu-${model.alignment}"
            aria-labelledby="${model.domId}">
            <c:forEach items="${model.filterSet}" var="filter">
                <li><a href="#" data-value="${cpn:value(filter.key)}"
                       title="${cpn:i18n(slingRequest,filter.hint)}">${cpn:i18n(slingRequest,filter.label)}</a></li>
            </c:forEach>
        </ul>
    </div>
</cpn:component>
