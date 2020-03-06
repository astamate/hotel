package com.tap5.tapestry.pages;

import java.util.Calendar;
import java.util.Date;

import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.ioc.annotations.Inject;

import com.tap5.tapestry.annotations.AnonymousAccess;
import com.tap5.tapestry.dal.CrudServiceDAO;
import com.tap5.tapestry.data.UserWorkspace;
import com.tap5.tapestry.entities.Hotel;
import com.tap5.tapestry.entities.User;
import com.tap5.tapestry.services.Authenticator;

/**
 * This page displays hotel details, and provide access to booking.
 * 
 * @author ccordenier
 */
@AnonymousAccess
public class View
{
    @SessionState
    @Property
    private UserWorkspace userWorkspace;

    @Property
    @PageActivationContext
    private Hotel hotel;

    @Property
    private Date checkInDate, checkOutDate;

    @Inject
    private Authenticator authenticator;

    @Inject
    private CrudServiceDAO dao;

    @OnEvent(value = EventConstants.PREPARE_FOR_RENDER)
    void initializeForm()
    {
        if (checkInDate == null && checkOutDate == null)
        {
            setReservationDates(0, 1);
        }
    }


    /**
     * Initialize the check-in and check-out dates.
     * 
     * @param daysFromNow
     *            Number of days the stay will begin from now
     * @param nights
     *            Length of the stay in number of nights
     */
    public void setReservationDates(int daysFromNow, int nights)
    {
        Calendar refDate = Calendar.getInstance();
        refDate.set(refDate.get(Calendar.YEAR), refDate.get(Calendar.MONTH),
                refDate.get(Calendar.DAY_OF_MONTH) + daysFromNow, 0, 0, 0);
        checkInDate = refDate.getTime();
        refDate.add(Calendar.DAY_OF_MONTH, nights);
        checkOutDate = refDate.getTime();
    }

    /**
     * Start booking process.
     * 
     * @param hotel
     * @return link to the current hotel booking
     */
    @OnEvent(value = EventConstants.SUCCESS, component = "startBookingForm")
    Object startBooking(Hotel hotel)
    {
        User user = null;
        if (authenticator.isLoggedIn()) {
            user = (User) dao.find(User.class, authenticator.getLoggedUser().getId());
        }
        userWorkspace.startBooking(hotel, user, checkInDate, checkOutDate);
        return Book.class;
    }
}
