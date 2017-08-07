<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<sling:defineObjects/>
<div class="btn-group btn-group-sm" role="group">
    <a class="small view-mode fa fa-th btn btn-default" href="#small" data-group="general"
       title="${cpn:i18n(slingRequest,'Small Thumbnails')}"><cpn:text
            value="small" tagName="span" tagClass="label" i18n="true"/></a>
    <a class="large view-mode fa fa-th-large btn btn-default" href="#large" data-group="large"
       title="${cpn:i18n(slingRequest,'Large Thumbnails')}"><cpn:text
            value="large" tagName="span" tagClass="label" i18n="true"/></a>
    <a class="list view-mode fa fa-th-list btn btn-default" href="#list" data-group="list"
       title="${cpn:i18n(slingRequest,'Assets List')}"><cpn:text
            value="List" tagName="span" tagClass="label" i18n="true"/></a>
</div>
