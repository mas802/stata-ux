package istata;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

/**
 * http://stackoverflow.com/questions/23901950/spring-security-ajax-session-
 * timeout-issue
 * 
 * @author stackoverflow
 *
 */
public class AjaxAwareAuthenticationEntryPoint extends
        LoginUrlAuthenticationEntryPoint {
    public AjaxAwareAuthenticationEntryPoint(String loginUrl) {
        super(loginUrl);
    }

    @Override
    public void commence(HttpServletRequest request,
            HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        String ajaxHeader = ((HttpServletRequest) request)
                .getHeader("X-Requested-With");
        boolean isAjax = "XMLHttpRequest".equals(ajaxHeader);
        // System.out.println("test: "+ajaxHeader + " -- " + isAjax);
        if (isAjax) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Ajax REquest Denied (Session Expired)");
        } else {
            super.commence(request, response, authException);
        }
    }
}