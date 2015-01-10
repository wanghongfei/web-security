package cn.fh.component.security.test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import cn.fh.security.servlet.PageProtectionContextListener;

public class PageProtectionContextListenerTest {
	private ServletContextEvent event;
	private ServletContext context;

	@Before
	public void initServletMockito() {
		this.event = mock(ServletContextEvent.class);
		this.context = mock(ServletContext.class);
		
		when(event.getServletContext())
			.thenReturn(this.context);
		
		when(context.getAttribute("STATIC_RESOURCE_PATH"))
			.thenReturn("/resources");
		
		when(context.getResourceAsStream("/WEB-INF/security-page.xml"))
			.thenReturn(this.getClass().getClassLoader().getResourceAsStream("WEB-INF/security-page.xml"));
	}
	
	@Test
	public void testLoadConfiguration() {
		Assert.assertNull(PageProtectionContextListener.rcm);

		new PageProtectionContextListener().contextInitialized(event);;
		
		Assert.assertNotNull(PageProtectionContextListener.rcm);
	}
}
