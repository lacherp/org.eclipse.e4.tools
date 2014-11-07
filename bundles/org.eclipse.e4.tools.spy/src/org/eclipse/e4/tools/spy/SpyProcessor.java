/*******************************************************************************
 * Copyright (c) 2014 OPCoach.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Olivier Prouvost <olivier.prouvost@opcoach.com> - initial API and implementation
 *     Olivier Prouvost <olivier.prouvost@opcoach.com> - Bug 428903 - Having a common 'debug' window for all spies 
 *******************************************************************************/
package org.eclipse.e4.tools.spy;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.commands.MBindingContext;
import org.eclipse.e4.ui.model.application.commands.MBindingTable;
import org.eclipse.e4.ui.model.application.commands.MCommand;
import org.eclipse.e4.ui.model.application.commands.MCommandParameter;
import org.eclipse.e4.ui.model.application.commands.MHandler;
import org.eclipse.e4.ui.model.application.commands.MKeyBinding;
import org.eclipse.e4.ui.model.application.commands.MParameter;
import org.eclipse.e4.ui.model.application.descriptor.basic.MPartDescriptor;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/** A base class for all spies processors */
public class SpyProcessor
{
	static final String SPY_TAG = "Spy";

	public static final String SPY_COMMAND = "org.eclipse.e4.tools.spy.command";
	public static final String SPY_COMMAND_PARAM = "org.eclipse.e4.tools.spy.command.partID";

	private static final String SPY_HANDLER = "org.eclipse.e4.tools.spy.handler";
	private static final String E4_SPIES_BINDING_TABLE = "org.eclipse.e4.tools.spy.bindings";

	@Inject
	MApplication application;
	@Inject
	EModelService modelService;
	@Inject
	Logger log;

	@Execute
	public void process(IExtensionRegistry extRegistry)
	{
		// This processor will read all spy extensions and automatically create
		// the command, handler and binding
		// to open this spy in the dedicated spy window.

		// First of all, it creates the spyCommand having one parameter (Id of
		// the part to display) and defaut handler for this command.
		MCommand command = getOrCreateSpyCommand();

		for (IConfigurationElement e : extRegistry.getConfigurationElementsFor("org.eclipse.e4.tools.spy.spyPart"))
		{
			String partName = e.getAttribute("name");
			String shortCut = e.getAttribute("shortcut");
			String iconPath = e.getAttribute("icon");
			String desc = e.getAttribute("description");

			Bundle b = Platform.getBundle(e.getNamespaceIdentifier());
			String partID = e.getAttribute("part");
			try
			{
				Class<?> partClass = b.loadClass(partID);
				// Bind the command with the binding, and add the view ID as
				// parameter.
				// The part class name will be the ID of the part descriptor
				bindSpyKeyBinding(shortCut, command, partID);

				// Add the descriptor in application
				addSpyPartDescriptor(partID, partName, iconPath, partClass);

			} catch (InvalidRegistryObjectException e1)
			{
				e1.printStackTrace();
			} catch (ClassNotFoundException e1)
			{
				log.error("The class '" + partID + "' can not be instantiated. Check name or launch config");
				e1.printStackTrace();
			}

			// Can create the command in model

		}

	}

	public MCommand getOrCreateSpyCommand()
	{
		// DO NOT USE findElement on ModelService (it searches only in MUIElements)
		for (MCommand cmd : application.getCommands())
		{
			if (SPY_COMMAND.equals(cmd.getElementId()))
			{
				// Do nothing if command exists
				return cmd;
			}
		}

		MCommand command = modelService.createModelElement(MCommand.class);
		command.setElementId(SPY_COMMAND);
		command.setCommandName("Open a spy");
		String contributorURI = "platform:/plugin/" + FrameworkUtil.getBundle(getClass()).getSymbolicName();
		command.setContributorURI(contributorURI);
		command.setDescription("Open a spy in the E4 spy window");

		// Parameter (will be the ID of the part descriptor (ie, the full
		// qualified class name))
		// It will be received in the Handler and send by keybinding
		MCommandParameter cp = modelService.createModelElement(MCommandParameter.class);
		cp.setElementId(SPY_COMMAND_PARAM);
		cp.setName("viewPart");
		cp.setContributorURI(contributorURI);
		command.getParameters().add(cp);

		application.getCommands().add(command);

		// Create the default handler for this command
		// (will receive the parameter)
		for (MHandler hdl : application.getHandlers())
		{
			if (SPY_HANDLER.equals(hdl.getElementId()))
			{
				// Do nothing if handler exists, return the command
				return command;
			}
		}

		// Create the handler for this command.
		MHandler handler = modelService.createModelElement(MHandler.class);
		handler.setElementId(SPY_HANDLER);
		handler.setContributionURI("bundleclass://org.eclipse.e4.tools.spy/org.eclipse.e4.tools.spy.SpyHandler");
		handler.setContributorURI("platform:/plugin/org.eclipse.e4.tools.spy");
		application.getHandlers().add(handler);

		handler.setCommand(command);

		return command;

	}

	/**
	 * Helper method to get or create the binding table for all spies (where
	 * spies will add their key binding). Bind this table with the
	 * org.eclipse.ui.contexts.dialogAndWindow binding context which should be
	 * present (create it if not)
	 * 
	 * This method will probably move to the common spy plugin providing common
	 * spy stuff (see bug #428903)
	 * 
	 * @param application
	 * @return
	 */
	public void bindSpyKeyBinding(String keySequence, MCommand cmd, String paramViewId)
	{
		// This method must :
		// search for a binding table having the binding context 'dialog and
		// window'
		// If none found, create it and also the binding context
		// Then can add the KeyBinding if not already added

		MBindingTable spyBindingTable = null;
		for (MBindingTable bt : application.getBindingTables())
			if (E4_SPIES_BINDING_TABLE.equals(bt.getElementId()))
			{
				spyBindingTable = bt;
			}

		// Binding table has not been yet added... Create it and bind it to
		// org.eclipse.ui.contexts.dialogAndWindow binding context
		// If this context does not yet exist, create it also.
		if (spyBindingTable == null)
		{

			MBindingContext bc = null;
			final List<MBindingContext> bindingContexts = application.getBindingContexts();
			if (bindingContexts.size() == 0)
			{
				bc = modelService.createModelElement(MBindingContext.class);
				bc.setElementId("org.eclipse.ui.contexts.window");
			} else
			{
				// Prefer org.eclipse.ui.contexts.dialogAndWindow but randomly
				// select another one
				// if org.eclipse.ui.contexts.dialogAndWindow cannot be found
				for (MBindingContext aBindingContext : bindingContexts)
				{
					bc = aBindingContext;
					if ("org.eclipse.ui.contexts.dialogAndWindow".equals(aBindingContext.getElementId()))
					{
						break;
					}
				}
			}

			// Can now create the binding table and bind it to this
			// context...
			spyBindingTable = modelService.createModelElement(MBindingTable.class);
			spyBindingTable.setElementId(E4_SPIES_BINDING_TABLE);
			spyBindingTable.setBindingContext(bc);
			application.getBindingTables().add(spyBindingTable);

		}

		// Search for the key binding if already present
		for (MKeyBinding kb : spyBindingTable.getBindings())
			if (keySequence.equals(kb.getKeySequence()))
			{
				// A binding with this key sequence is already present. Check if
				// command is the same
				if (kb.getCommand().getElementId().equals(cmd.getElementId()))
					return;
				else
				{
					// Must log an error : key binding already exists in this
					// table but with another command
					System.out.println("WARNING : Cannot bind the command '" + cmd.getElementId() + "' to the keySequence : "
							+ keySequence + " because the command " + kb.getCommand().getElementId() + " is already bound !");
					return;
				}
			}

		// Key binding is not yet in table... can add it now.
		MKeyBinding binding = modelService.createModelElement(MKeyBinding.class);
		binding.setElementId(cmd.getElementId() + ".binding");
		binding.setContributorURI(cmd.getContributorURI());
		binding.setKeySequence(keySequence);

		MParameter p = modelService.createModelElement(MParameter.class);
		p.setName(SPY_COMMAND_PARAM);
		p.setValue(paramViewId);
		binding.getParameters().add(p);

		spyBindingTable.getBindings().add(binding);
		binding.setCommand(cmd);

	}

	public void addSpyPartDescriptor(String partId, String partLabel, String iconPath, Class<?> spyPartClass)
	{
		for (MPartDescriptor mp : application.getDescriptors())
		{
			if (partId.equals(mp.getElementId()))
			{
				// Already added, do nothing
				return;
			}
		}

		// If descriptor not yet in descriptor list, add it now
		MPartDescriptor descriptor = modelService.createModelElement(MPartDescriptor.class);
		descriptor.setCategory("Eclipse runtime spies");
		descriptor.setElementId(partId);
		descriptor.getTags().add("View");
		descriptor.getTags().add(SPY_TAG);
		descriptor.getTags().add("categoryTag:Eclipse runtime spies");

		descriptor.setLabel(partLabel);
		descriptor.setCloseable(true);
		String bundleId = FrameworkUtil.getBundle(spyPartClass).getSymbolicName();
		descriptor.setContributionURI("bundleclass://" + bundleId + "/" + spyPartClass.getCanonicalName());
		String contributorURI = "platform:/plugin/" + bundleId;
		descriptor.setContributorURI(contributorURI);
		descriptor.setIconURI(contributorURI + "/" + iconPath);
		application.getDescriptors().add(descriptor);

	}
}
