package org.mycore.frontend.workflowengine.guice;

import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowManager;
import org.mycore.frontend.workflowengine.jbpm.series.MCRSeriesDerivateStrategy;
import org.mycore.frontend.workflowengine.jbpm.series.MCRSeriesMetadataStrategy;
import org.mycore.frontend.workflowengine.jbpm.series.MCRWorkflowManagerSeries;
import org.mycore.frontend.workflowengine.strategies.MCRAuthorStrategy;
import org.mycore.frontend.workflowengine.strategies.MCRDefaultAuthorStrategy;
import org.mycore.frontend.workflowengine.strategies.MCRDefaultPermissionStrategy;
import org.mycore.frontend.workflowengine.strategies.MCRDerivateStrategy;
import org.mycore.frontend.workflowengine.strategies.MCRIdentifierStrategy;
import org.mycore.frontend.workflowengine.strategies.MCRMetadataStrategy;
import org.mycore.frontend.workflowengine.strategies.MCRPermissionStrategy;
import org.mycore.frontend.workflowengine.strategies.MCRURNIdentifierStrategy;

import com.google.inject.AbstractModule;

public class MCRSeriesWorkflowModule  extends AbstractModule {

	public void configure() {
		bind(MCRWorkflowManager.class).to(MCRWorkflowManagerSeries.class);
		
		bind(MCRPermissionStrategy.class).to(MCRDefaultPermissionStrategy.class);
		
		bind(MCRDerivateStrategy.class).to(MCRSeriesDerivateStrategy.class);
		bind(MCRMetadataStrategy.class).to(MCRSeriesMetadataStrategy.class);
	
		bind(MCRIdentifierStrategy.class).to(MCRURNIdentifierStrategy.class);
		bind(MCRAuthorStrategy.class).to(MCRDefaultAuthorStrategy.class);
	}

}
