package com.tap5.tapestry.data;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.OptionGroupModel;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.internal.OptionModelImpl;
import org.apache.tapestry5.util.AbstractSelectModel;

/**
 * Represent bed types supported by the application. 
 *
 * @author ccordenier
 *
 */
public class BedType extends AbstractSelectModel
{

    private List<OptionModel> options = new ArrayList<OptionModel>();

    public BedType()
    {
        options.add(new OptionModelImpl("One king-sized bed", 1));
        options.add(new OptionModelImpl("Two queen beds", 2));
        options.add(new OptionModelImpl("Two double beds", 3));
        options.add(new OptionModelImpl("One queen beds", 2));
    }

    public List<OptionGroupModel> getOptionGroups()
    {
        return null;
    }

    public List<OptionModel> getOptions()
    {
        return options;
    }

}
