package cn.fh.component.security.test;

import cn.fh.security.utils.ResponseUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PageProtectionFilterTest {
    @Mock
	private HttpServletRequest req;
    @Mock
	private HttpServletResponse resp;
    @Mock
	private FilterChain chain;
    @Mock
    private ResponseUtils utils;
	

	@Before
	public void initMockitoObjects() {
		when(req.getContextPath())
			    .thenReturn("/");
        when(req.getRequestURI())
                .thenReturn("/admin/post");
        when(req.getSession())
                .thenReturn(null);

	}
	
	@Test
	public void testRedirect() throws IOException, ServletException {
        /*PageProtectionFilter filter = new PageProtectionFilter();
        filter.doFilter(req, resp, chain);*/
	}

}
