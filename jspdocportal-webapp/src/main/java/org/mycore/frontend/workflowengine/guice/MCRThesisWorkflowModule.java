package org.mycore.frontend.workflowengine.guice;

import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowManager;
import org.mycore.frontend.workflowengine.jbpm.thesis.MCRThesisDerivateStrategy;
import org.mycore.frontend.workflowengine.jbpm.thesis.MCRThesisMetadataStrategy;
import org.mycore.frontend.workflowengine.jbpm.thesis.MCRWorkflowManagerThesis;
import org.mycore.frontend.workflowengine.strategies.MCRAuthorStrategy;
import org.mycore.frontend.workflowengine.strategies.MCRDefaultAuthorStrategy;
import org.mycore.frontend.workflowengine.strategies.MCRDefaultPermissionStrategy;
import org.mycore.frontend.workflowengine.strategies.MCRDerivateStrategy;
import org.mycore.frontend.workflowengine.strategies.MCRIdentifierStrategy;
import org.mycore.frontend.workflowengine.strategies.MCRMetadataStrategy;
import org.mycore.frontend.workflowengine.strategies.MCRPermissionStrategy;
import org.mycore.frontend.workflowengine.strategies.MCRURNIdentifierStrategy;

import com.google.inject.AbstractModule;

public class MCRThesisWorkflowModule  extends AbstractModule {

	public void configure() {
		bind(MCRWorkflowManager.class).to(MCRWorkflowManagerThesis.class);
		
		bind(MCRPermissionStrategy.class).to(MCRDefaultPermissionStrategy.class);
		
		bind(MCRDerivateStrategy.class).to(MCRThesisDerivateStrategy.class);
		bind(MCRMetadataStrategy.class).to(MCRThesisMetadataStrategy.class);
	
		bind(MCRIdentifierStrategy.class).to(MCRURNIdentifierStrategy.class);
		bind(MCRAuthorStrategy.class).to(MCRDefaultAuthorStrategy.class);
	}

}
