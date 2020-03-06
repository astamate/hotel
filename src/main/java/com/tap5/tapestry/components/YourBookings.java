package com.tap5.tapestry.components;

import java.util.Collections;
import java.util.List;

import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import com.tap5.tapestry.dal.CrudServiceDAO;
import com.tap5.tapestry.dal.QueryParameters;
import com.tap5.tapestry.entities.Booking;
import com.tap5.tapestry.services.Authenticator;

/**
 * List all the bookings of the current user.
 * 
 * @author ccordenier
 */
public class YourBookings
{
    @Inject
    private CrudServiceDAO crudDao;

    @Inject
    private Authenticator authenticator;

    @Property
    private List<Booking> bookings;

    @Property
    private Booking current;

    /**
     * Prepare the list of booking to display, extract all the booking associated to the current
     * logged user.
     * 
     * @return
     */
    @SetupRender
    void buildBookingsList()
    {
        if (! authenticator.isLoggedIn()) 
        {
            bookings = Collections.emptyList();
            return;
        }
        bookings = crudDao.findWithNamedQuery(
        Booking.BY_USERNAME,
            QueryParameters.with("username", authenticator.getLoggedUser().getUsername())
                .parameters());
    }

    /**
     * Simply cancel the booking
     * 
     * @param booking
     */
    @OnEvent(value = "cancelBooking")
    void cancelBooking(Booking booking)
    {
        crudDao.delete(Booking.class, booking.getId());
    }

}
