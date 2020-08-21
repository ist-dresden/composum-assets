<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<cpn:component var="model" type="com.composum.assets.commons.widget.FilterModel">
    <div class="composum-assets-widget-filter composum-assets-widget-filter_select">
        <select class="composum-assets-widget-filter_input input form-control">
            <c:forEach items="${model.filterSet}" var="filter">
                <option value="${cpn:value(filter.key)}"
                        title="${cpn:i18n(slingRequest,filter.hint)}">${cpn:i18n(slingRequest,filter.label)}</option>
            </c:forEach>
        </select>
    </div>
</cpn:component>
