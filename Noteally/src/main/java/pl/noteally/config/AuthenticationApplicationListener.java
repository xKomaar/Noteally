package pl.noteally.config;


import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.stereotype.Component;
import pl.noteally.data.User;

@Component
@AllArgsConstructor
public class AuthenticationApplicationListener {

    private final HttpSession session;
    @EventListener
    public void handleInteractiveAuthenticationSuccess(InteractiveAuthenticationSuccessEvent event) {
        User user = (User) event.getAuthentication().getPrincipal();
        session.setAttribute("userId", user.getId());
    }
}