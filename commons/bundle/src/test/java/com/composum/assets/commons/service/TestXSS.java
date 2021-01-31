package com.composum.assets.commons.service;

import com.composum.sling.core.util.ServiceHandle;
import com.composum.sling.core.util.XSS;
import org.apache.sling.xss.XSSFilter;
import org.mockito.stubbing.Answer;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestXSS extends XSS {

    public class TestXSSFilter extends ServiceHandle<XSSFilter> {

        public TestXSSFilter() {
            super(XSSFilter.class);
            service = mock(XSSFilter.class);
            when(service.filter(anyString())).then((Answer<String>) invocation -> invocation.getArgument(0));
        }
    }

    public TestXSS() {
        XSSFilter_HANDLE = new TestXSSFilter();
    }
}
