<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<sling:defineObjects/>
<cpn:component id="finder" type="com.composum.assets.commons.widget.Finder" scope="request">
    <div class="finder-content list-panel"
         data-type="composum/assets/commons/widgets/asset/finder/list">
        <div class="list-content">
            <sling:call script="content.jsp"/>
        </div>
    </div>
</cpn:component>