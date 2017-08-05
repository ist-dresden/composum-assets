<%@page session="false" pageEncoding="utf-8"%>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2"%>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<sling:defineObjects />
<cpn:component id="navigator" type="com.composum.assets.manager.view.NavigatorBean" scope="request">
    <ol class="breadcrumbs">
        <c:forEach var="parent" items="${navigator.parents}">
            <li data-path="${parent.path}"><cpn:link href="/bin/assets.html${parent.pathEncoded}">${parent.nameEscaped}</cpn:link></li>
        </c:forEach>
        <li class="active" data-path="${navigator.current.path}"><cpn:link href="/bin/assets.html${navigator.current.pathEncoded}">${navigator.current.nameEscaped}</cpn:link></li>
    </ol>
</cpn:component>
