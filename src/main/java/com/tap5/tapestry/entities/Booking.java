package com.tap5.tapestry.entities;

import static javax.persistence.TemporalType.DATE;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * <p>
 * <strong>Booking</strong> is the model/entity class that represents a hotel
 * booking (aka a "reservation").
 * </p>
 * 
 * @author Gavin King
 * @author Dan Allen
 */
@Entity
@NamedQueries(
{ @NamedQuery(name = Booking.BY_USERNAME, query = "Select b from Booking b where b.user.username = :username") })
@Table(name = "bookings")
public class Booking implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = -6176295317720795275L;

    public static final String BY_USERNAME = "Booking.byUsername";

    private Long id;

    private User user;

    private Hotel hotel;

    private Date checkInDate;

    private Date checkOutDate;

    private String creditCardNumber;

    private CreditCardType creditCardType;

    private PaymentType paymentType;

    private String creditCardName;

    private int creditCardExpiryMonth;

    private int creditCardExpiryYear;

    private boolean smoking;

    private int beds;

    private Date creationDate;

    @Transient
    private boolean status;

    public Booking()
    {
    }

    /**
     * Constructor for a new Booking object
     * @param hotel the hotel to book
     * @param user the user (may be null and filled in later)
     * @param daysFromNow the number of days from today when the stay starts
     * @param nights the number of nights
     */
    public Booking(Hotel hotel, User user, Date checkInDate, Date checkOutDate)
    {
        this.hotel = hotel;
        setUser(user);
        this.smoking = false;
        this.beds = 1;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        creditCardExpiryMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
        this.creationDate = new Date();
        this.status = false;
    }

    @Id
    @GeneratedValue
    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    @NotNull
    @Temporal(DATE)
    public Date getCheckinDate()
    {
        return checkInDate;
    }

    public void setCheckinDate(Date datetime)
    {
        this.checkInDate = datetime;
    }

    @NotNull
    @ManyToOne
    public Hotel getHotel()
    {
        return hotel;
    }

    public void setHotel(Hotel hotel)
    {
        this.hotel = hotel;
    }

    @NotNull
    @ManyToOne
    public User getUser()
    {
        return user;
    }

    /**
     * Set this booking's user to the given user (and set the credit
     * card name to the user's name, if it isn't already set to something
     * else)
     */
    public void setUser(User user)
    {
        this.user = user;
        if (hasUser() && creditCardName == null)
        {
            this.creditCardName = user.getFullname();
        }
    }

    @NotNull
    @Temporal(TemporalType.DATE)
    public Date getCheckoutDate()
    {
        return checkOutDate;
    }

    public void setCheckoutDate(Date checkoutDate)
    {
        this.checkOutDate = checkoutDate;
    }

    public boolean isSmoking()
    {
        return smoking;
    }

    public void setSmoking(boolean smoking)
    {
        this.smoking = smoking;
    }

    // @Size(min = 1, max = 3)
    public int getBeds()
    {
        return beds;
    }

    public void setBeds(int beds)
    {
        this.beds = beds;
    }

    @NotNull(message = "Credit card number is required")
    @Size(min = 16, max = 16, message = "Credit card number must 16 digits long")
    @Pattern(regexp = "^\\d*$", message = "Credit card number must be numeric")
    public String getCreditCardNumber()
    {
        return creditCardNumber;
    }

    public void setCreditCardNumber(String creditCardNumber)
    {
        this.creditCardNumber = creditCardNumber;
    }

    @NotNull(message = "Credit card type is required")
    @Enumerated(EnumType.STRING)
    public CreditCardType getCreditCardType()
    {
        return creditCardType;
    }

    public void setCreditCardType(CreditCardType creditCardType)
    {
        this.creditCardType = creditCardType;
    }

    @NotNull(message = "Payment type is required")
    @Enumerated(EnumType.STRING)
    public PaymentType getPaymentType()
    {
        return paymentType;
    }
    
    public void setPaymentType(PaymentType paymentType)
    {
        this.paymentType = paymentType;
    }

    @NotNull(message = "Credit card name is required")
    @Size(min = 3, max = 70, message = "Credit card name must be between 3 and 70 characters    ")
    public String getCreditCardName()
    {
        return creditCardName;
    }

    public void setCreditCardName(String creditCardName)
    {
        this.creditCardName = creditCardName;
    }

    /**
     * The credit card expiration month, represented using a 1-based numeric value (i.e., Jan = 1,
     * Feb = 2, ...).
     * 
     * @return 1-based numeric month value
     */
    public int getCreditCardExpiryMonth()
    {
        return creditCardExpiryMonth;
    }

    public void setCreditCardExpiryMonth(int creditCardExpiryMonth)
    {
        this.creditCardExpiryMonth = creditCardExpiryMonth;
    }

    /**
     * The credit card expiration year.
     * 
     * @return numberic year value
     */
    public int getCreditCardExpiryYear()
    {
        return creditCardExpiryYear;
    }

    public void setCreditCardExpiryYear(int creditCardExpiryYear)
    {
        this.creditCardExpiryYear = creditCardExpiryYear;
    }

    @Transient
    public String getDescription()
    {
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
        return hotel == null ? null : hotel.getName() + ", " + df.format(getCheckinDate()) + " to "
                + df.format(getCheckoutDate());
    }

    @Transient
    public BigDecimal getTotal()
    {
        return hotel.getPrice().multiply(new BigDecimal(getNights()));
    }

    @Transient
    public int getNights()
    {
        return (int) (checkOutDate.getTime() - checkInDate.getTime()) / 1000 / 60 / 60 / 24;
    }

    public Date getCreationDate()
    {
        return creationDate;
    }

    public void setCreationDate(Date creationDate)
    {
        this.creationDate = creationDate;
    }

    public Boolean getStatus()
    {
        return status;
    }

    public void setStatus(Boolean status)
    {
        this.status = status;
    }

    /**
     * Determine whether this booking has a known (logged in) user associated with it
     * @return true if there is a user, false otherwise
     */
    public boolean hasUser()
    {
        return getUser() != null;
    }

    @Override
    public String toString()
    {
        return "Booking(" + user + ", " + hotel + ")";
    }
}
