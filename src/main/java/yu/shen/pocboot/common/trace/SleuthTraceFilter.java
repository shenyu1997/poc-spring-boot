package yu.shen.pocboot.common.trace;

import brave.Tracer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class SleuthTraceFilter extends OncePerRequestFilter {

    private static final String TRACE_HEADER_NAME = "X-B3-TraceId";

    @Autowired
    private Tracer tracer;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        httpServletResponse.addHeader(TRACE_HEADER_NAME, tracer.currentSpan().context().traceIdString());
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
