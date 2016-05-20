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

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.commands.MCommand;
import org.eclipse.e4.ui.model.application.commands.MParameter;
import org.eclipse.e4.ui.model.application.descriptor.basic.MPartDescriptor;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimBar;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimmedWindow;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledToolItem;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBar;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.osgi.framework.FrameworkUtil;

public class SpyHandler {
	private static final int DEFAULT_SPY_WINDOW_HEIGHT = 600;
	private static final int DEFAULT_SPY_WINDOW_WIDTH = 1000;
	private static final String E4_SPIES_WINDOW_LABEL = "E4 Spies window";
	private static final String E4_SPIES_WINDOW = "org.eclipse.e4.tools.spy.window";
	private static final String E4_SPIES_ICON = "platform:/plugin/org.eclipse.e4.tools.spy/icons/spyicon.png";
	private static final String E4_SPIES_TRIMBAR = "org.eclipse.e4.tools.spy.trimbar";
	private static final String E4_SPIES_TOOLBAR = "org.eclipse.e4.tools.spy.toolbar";
	private static final String E4_SPIES_PART_STACK = "org.eclipse.e4.tools.spy.partStack";

	/**
	 * This method open the spy view part at the correct location : - if it has
	 * never been opened, it add this spy in the E4 window - if it is already
	 * opened, just activate it - if it has already opened and closed, find it,
	 * and open it at the same location
	 *
	 * @param ps
	 *            part Service to manage parts elements
	 * @param viewID
	 *            the spy view to be displayed
	 * @param appli
	 *            current application
	 * @param modelService
	 *            model service to create elements
	 */
	@Execute
	public void run(EPartService ps, @Optional @Named(SpyProcessor.SPY_COMMAND_PARAM) String viewID, MApplication appli,
			EModelService modelService) {
		MWindow spyWindow = getOrCreateSpyWindow(appli, modelService);

		MPartStack partStack = (MPartStack) modelService.find(E4_SPIES_PART_STACK, spyWindow);

		MPart p = ps.findPart(viewID);
		if (p == null) {
			// Create the part in the spyWindow...
			p = ps.createPart(viewID);
			partStack.getChildren().add(p);
			partStack.setSelectedElement(p);
		}

		p.setVisible(true);

		// modelService.bringToTop(spyWindow);
		ps.activate(p, true);

	}

	private MWindow getOrCreateSpyWindow(MApplication appli, EModelService modelService) {
		List<MWindow> existingWindow = modelService.findElements(appli, E4_SPIES_WINDOW, MWindow.class, null);
		if (existingWindow.size() >= 1)
			return existingWindow.get(0);

		// Now spy window for the moment... add one...
		String contributorURI = "platform:/plugin/" + FrameworkUtil.getBundle(getClass()).getSymbolicName();

		MTrimmedWindow tw = modelService.createModelElement(MTrimmedWindow.class);
		tw.setElementId(E4_SPIES_WINDOW);
		tw.setLabel(E4_SPIES_WINDOW_LABEL);
		tw.setIconURI(E4_SPIES_ICON);
		tw.setContributorURI(contributorURI);

		MTrimBar trimBar = modelService.createModelElement(MTrimBar.class);
		trimBar.setElementId(E4_SPIES_TRIMBAR);
		trimBar.setContributorURI(contributorURI);
		tw.getTrimBars().add(trimBar);

		MToolBar toolbar = modelService.createModelElement(MToolBar.class);
		toolbar.setElementId(E4_SPIES_TOOLBAR);
		toolbar.setContributorURI(contributorURI);
		trimBar.getChildren().add(toolbar);

		// Create one toolbar element for each 'spy' tagged descriptor
		for (MPartDescriptor mp : appli.getDescriptors()) {
			if (mp.getTags().contains(SpyProcessor.SPY_TAG)) {
				// Create a toolitem bound to the command.
				MHandledToolItem toolItem = modelService.createModelElement(MHandledToolItem.class);
				toolItem.setContributorURI(contributorURI);

				// Search for spy command (added by processor)
				MCommand spyCmd = null;
				for (MCommand cmd : appli.getCommands()) {
					if (SpyProcessor.SPY_COMMAND.equals(cmd.getElementId())) {
						// Do nothing if command exists
						spyCmd = cmd;
						break;
					}
				}
				toolItem.setCommand(spyCmd);
				toolItem.setIconURI(mp.getIconURI());
				toolItem.setLabel(mp.getLabel());
				toolItem.setTooltip("Open the " + mp.getLabel());
				toolbar.getChildren().add(toolItem);

				// Add the parameter for the view to open
				MParameter p = modelService.createModelElement(MParameter.class);
				p.setName(SpyProcessor.SPY_COMMAND_PARAM);
				p.setValue(mp.getElementId());
				toolItem.getParameters().add(p);

			}
		}

		tw.setWidth(DEFAULT_SPY_WINDOW_WIDTH);
		tw.setHeight(DEFAULT_SPY_WINDOW_HEIGHT);
		appli.getChildren().get(0).getWindows().add(tw);

		MPartStack partStack = modelService.createModelElement(MPartStack.class);
		partStack.setElementId(E4_SPIES_PART_STACK);
		tw.getChildren().add(partStack);

		return tw;

	}
}
