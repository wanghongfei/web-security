package cn.fh.component.security.test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import cn.fh.security.servlet.PageProtectionFilter;

public class PageProtectionFilterTest {
	private HttpServletRequest req;
	private HttpServletResponse resp;
	private FilterChain chain;
	
	private PageProtectionFilter testObj;
	
	@Before
	public void initMockitoObjects() {
		this.req = mock(HttpServletRequest.class);
		this.resp = mock(HttpServletResponse.class);
		this.chain = mock(FilterChain.class);
		this.testObj = mock(PageProtectionFilter.class);
		
		
		when(req.getContextPath())
			.thenReturn("/");
	}
	
	/*@Test
	public void test() throws IOException, ServletException {
		testObj.doFilter(req, resp, chain);
		
		Mockito.verify(chain).doFilter(req, resp);
	}*/

}
