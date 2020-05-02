/*
 * Jitsi, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Copyright @ 2015 Atlassian Pty Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.java.sip.communicator.plugin.defaultresourcepack;

import java.lang.reflect.*;
import java.net.*;
import java.util.*;
import net.java.sip.communicator.service.resources.*;
import net.java.sip.communicator.util.*;

/**
 * @author Damian Minkov
 */
public class DefaultLanguagePackImpl
    extends AbstractResourcePack
    implements LanguagePack
{
    private static final String DEFAULT_RESOURCE_PATH
        = "resources.languages.resources";

    /**
     * The locale used for the last resources request
     */
    private Locale localeInBuffer = null;

    /**
     * The result of the last resources request
     */
    private Map<String, String> lastResourcesAsked = null;

    /**
     * All language resource locales.
     */
    private final Vector<Locale> availableLocales = new Vector<>();

    /**
     * Constructor.
     */
    public DefaultLanguagePackImpl()
    {
        // Finds all the files *.properties in the path : /resources/languages.
        Enumeration<?> fsEnum =
            DefaultResourcePackActivator.bundleContext.getBundle().
                findEntries("/resources/languages", "*.properties", false);

        if (fsEnum != null)
        {
            while (fsEnum.hasMoreElements())
            {
                String fileName = ((URL) fsEnum.nextElement()).getFile();
                int localeIndex = fileName.indexOf('_');

                if (localeIndex != -1)
                {
                    String localeId =
                        fileName.substring(
                            localeIndex + 1,
                            fileName.indexOf('.', localeIndex));

                    availableLocales.add(LocaleUtil.getLocale(localeId));
                }
            }
        }
    }

    /**
     * Returns a <tt>Map</tt>, containing all [key, value] pairs for this
     * resource pack.
     *
     * @return a <tt>Map</tt>, containing all [key, value] pairs for this
     * resource pack.
     */
    public Map<String, String> getResources()
    {
        return getResources(Locale.getDefault());
    }

    /**
     * Returns a <tt>Map</tt>, containing all [key, value] pairs for the given
     * locale.
     *
     * @param locale The <tt>Locale</tt> we're looking for.
     * @return a <tt>Map</tt>, containing all [key, value] pairs for the given
     * locale.
     */
    public Map<String, String> getResources(Locale locale)
    {
        // check if we didn't computed it at the previous call
        if (locale.equals(localeInBuffer) && lastResourcesAsked != null)
        {
            return lastResourcesAsked;
        }

        ResourceBundle resourceBundle
            = ResourceBundle.getBundle(DEFAULT_RESOURCE_PATH, locale,
            new Utf8ResourceBundleControl());

        Map<String, String> resources = new Hashtable<String, String>();

        this.initResources(resourceBundle, resources);

        this.initPluginResources(resources, locale);

        // keep it just in case of...
        localeInBuffer = locale;
        lastResourcesAsked = resources;

        return resources;
    }

    /**
     * Returns a Set of the keys contained only in the ResourceBundle for
     * locale.
     *
     * @param locale the locale for which the keys are requested
     * @return a Set of the keys contained only in the ResourceBundle for locale
     */
    @Override
    @SuppressWarnings("unchecked")
    public Set<String> getResourceKeys(Locale locale)
    {
        try
        {
            Method handleKeySet = ResourceBundle.class
                .getDeclaredMethod("handleKeySet");
            handleKeySet.setAccessible(true);
            return (Set<String>) handleKeySet.invoke(
                ResourceBundle.getBundle(DEFAULT_RESOURCE_PATH, locale).keySet());
        }
        catch (Exception e)
        {
        }

        return Collections.emptySet();
    }

    /**
     * Finds all plugin color resources, matching the "images-*.properties"
     * pattern and adds them to this resource pack.
     */
    private void initPluginResources(Map<String, String> resources,
        Locale locale)
    {
        List<String> pluginProperties = DefaultResourcePackActivator
            .findResourcePaths("resources/languages",
                "strings-*.properties");

        for (String rbName : pluginProperties)
        {
            if (rbName.indexOf('_') == -1)
            {
                ResourceBundle rb = ResourceBundle.getBundle(
                    rbName.substring(0, rbName.indexOf(".properties")), locale);

                initResources(rb, resources);
            }
        }
    }

    /**
     * All the locales in the language pack.
     *
     * @return all the locales this Language pack contains.
     */
    public Iterator<Locale> getAvailableLocales()
    {
        return availableLocales.iterator();
    }

}
