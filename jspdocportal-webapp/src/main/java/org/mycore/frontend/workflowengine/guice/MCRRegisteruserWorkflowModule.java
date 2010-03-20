package org.mycore.frontend.workflowengine.guice;

import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowManager;
import org.mycore.frontend.workflowengine.jbpm.registeruser.MCRWorkflowManagerRegisteruser;
import org.mycore.frontend.workflowengine.strategies.MCRDefaultDerivateStrategy;
import org.mycore.frontend.workflowengine.strategies.MCRDefaultMetadataStrategy;
import org.mycore.frontend.workflowengine.strategies.MCRDefaultPermissionStrategy;
import org.mycore.frontend.workflowengine.strategies.MCRDefaultUserStrategy;
import org.mycore.frontend.workflowengine.strategies.MCRDerivateStrategy;
import org.mycore.frontend.workflowengine.strategies.MCRMetadataStrategy;
import org.mycore.frontend.workflowengine.strategies.MCRPermissionStrategy;
import org.mycore.frontend.workflowengine.strategies.MCRUserStrategy;

import com.google.inject.AbstractModule;

public class MCRRegisteruserWorkflowModule  extends AbstractModule {

	public void configure() {
		bind(MCRWorkflowManager.class).to(MCRWorkflowManagerRegisteruser.class);
		
		bind(MCRDerivateStrategy.class).to(MCRDefaultDerivateStrategy.class);
		bind(MCRPermissionStrategy.class).to(MCRDefaultPermissionStrategy.class);
		bind(MCRMetadataStrategy.class).to(MCRDefaultMetadataStrategy.class);
		
		bind(MCRUserStrategy.class).to(MCRDefaultUserStrategy.class);
		
	}

}
