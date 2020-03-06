package com.tap5.tapestry.pages;

import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.Secure;
import org.apache.tapestry5.beaneditor.Validate;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

import com.tap5.tapestry.annotations.AnonymousAccess;
import com.tap5.tapestry.dal.CrudServiceDAO;
import com.tap5.tapestry.dal.QueryParameters;
import com.tap5.tapestry.entities.User;
import com.tap5.tapestry.security.AuthenticationException;
import com.tap5.tapestry.services.Authenticator;

/**
 * This page the user can create an account
 * 
 * @author karesti
 */
@AnonymousAccess
@Secure
public class Signup
{

    @Property
    private String target; // target page name

    @Property
    @Validate("username")
    private String username;

    @Property
    @Validate("required, minlength=3, maxlength=50")
    private String fullName;

    @Property
    @Validate("required,email")
    private String email;

    @Property
    @Validate("password")
    private String password;

    @Property
    @Validate("password")
    private String verifyPassword;

    @Property
    private String kaptcha;

    @Inject
    private CrudServiceDAO crudServiceDAO;

    @Component
    private Form registerForm;

    @Inject
    private Messages messages;

    @Inject
    private Authenticator authenticator;

    @InjectPage
    private Signin signin;

    /**
     * Respond to page activation by capturing the "target" parameter as the
     * name of the target page (the page to return to after creating an account)
     * @param ctx the EventContext
     */
    public void onActivate(EventContext context)
    {
        if (context.getCount() > 0)
        {
            target = context.get(String.class, 0);
        }
        else
        {
            target = "Index";
        }
    }

    @OnEvent(value = EventConstants.VALIDATE, component = "RegisterForm")
    public void checkForm()
    {
        if (!verifyPassword.equals(password))
        {
            registerForm.recordError(messages.get("error.verifypassword"));
        }
    }

    @OnEvent(value = EventConstants.SUCCESS, component = "RegisterForm")
    public Object proceedSignup()
    {

        User userVerif = crudServiceDAO.findUniqueWithNamedQuery(
                User.BY_USERNAME_OR_EMAIL,
                QueryParameters.with("username", username).and("email", email).parameters());

        if (userVerif != null)
        {
            registerForm.recordError(messages.get("error.userexists"));

            return null;
        }

        User user;
        try
        {
            user = new User(fullName, username, email, authenticator.encryptPassword(password));
        }
        catch (AuthenticationException e)
        {
            registerForm.recordError("Authentication has failed");
            return this;
        }

        crudServiceDAO.create(user);

        try
        {
            authenticator.login(username, password);
        }
        catch (AuthenticationException ex)
        {
            registerForm.recordError("Authentication process has failed");
            return this;
        }

        return Index.class;
    }
}
