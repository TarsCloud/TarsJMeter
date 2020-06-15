package com.tencent.tars.jmeter.gui;

import org.apache.jmeter.util.JMeterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * 这里可以继续优化和改进的是，根据 JmeterUtils 来实现多语种支持
 * @author brookechen
 */
public class CustomResUtils {
    public static final String PROP_BASENAME =
            "com.tencent.tars"; //$NON-NLS-1$

    private static final Logger log = LoggerFactory.getLogger(CustomResUtils.class);
    private static final long serialVersionUID = 1L;
    private static ResourceBundle resources;

    static {
        Locale loc = JMeterUtils.getLocale();
        resources = ResourceBundle.getBundle(PROP_BASENAME + ".messages", loc);
    }

    /**
     * Gets the resource string for this key.
     * <p>
     * If the resource is not found, a warning is logged
     *
     * @param key the key in the resource file
     * @return the resource string if the key is found; otherwise, return
     * "[res_key="+key+"]"
     */
    public static String getResString(String key) {
        return getResStringDefault(key, RES_KEY_PFX + key + "]"); //$NON-NLS-1$
    }

    public static final String RES_KEY_PFX = "[res_key="; //$NON-NLS-1$

    /*
     * Helper method to do the actual work of fetching resources; allows
     * getResString(S,S) to be deprecated without affecting getResString(S);
     */
    private static String getResStringDefault(String key, String defaultValue) {
        if (key == null) {
            return null;
        }
        // Resource keys cannot contain spaces
        key = key.replace(' ', '_'); // $NON-NLS-1$ // $NON-NLS-2$
        key = key.toLowerCase(Locale.ENGLISH);
        String resString = null;
        try {
            resString = resources.getString(key);
        } catch (MissingResourceException mre) {
            resString = defaultValue;
        }
        return resString;
    }
}
