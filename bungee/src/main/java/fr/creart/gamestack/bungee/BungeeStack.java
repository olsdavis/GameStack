/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package fr.creart.gamestack.bungee;

import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;

/**
 * @author Creart
 */
public class BungeeStack extends Plugin {

    private static BungeeStack instance;

    public BungeeStack()
    {
        instance = this;
    }

    @Override
    public void onEnable()
    {

    }

    @Override
    public void onDisable()
    {

    }

    private void registerCommands(Command... commands)
    {
        for (Command command : commands)
            getProxy().getPluginManager().registerCommand(this, command);
    }

    private void registerListeners(Listener... listeners)
    {
        for (Listener listener : listeners)
            getProxy().getPluginManager().registerListener(this, listener);
    }

    /**
     * Returns the single instance of the BungeeStack class
     *
     * @return the single instance of the BungeeStack class
     */
    public static BungeeStack getInstance()
    {
        return instance;
    }

}
