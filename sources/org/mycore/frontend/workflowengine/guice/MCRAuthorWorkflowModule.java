package org.mycore.frontend.workflowengine.guice;

import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowManager;
import org.mycore.frontend.workflowengine.jbpm.author.MCRAuthorMetadataStrategy;
import org.mycore.frontend.workflowengine.jbpm.author.MCRWorkflowManagerAuthor;
import org.mycore.frontend.workflowengine.strategies.MCRAuthorStrategy;
import org.mycore.frontend.workflowengine.strategies.MCRDefaultAuthorStrategy;
import org.mycore.frontend.workflowengine.strategies.MCRDefaultDerivateStrategy;
import org.mycore.frontend.workflowengine.strategies.MCRDefaultPermissionStrategy;
import org.mycore.frontend.workflowengine.strategies.MCRDerivateStrategy;
import org.mycore.frontend.workflowengine.strategies.MCRMetadataStrategy;
import org.mycore.frontend.workflowengine.strategies.MCRPermissionStrategy;

import com.google.inject.AbstractModule;


public class MCRAuthorWorkflowModule  extends AbstractModule {

	public void configure() {
		bind(MCRWorkflowManager.class).to(MCRWorkflowManagerAuthor.class);
		
		bind(MCRDerivateStrategy.class).to(MCRDefaultDerivateStrategy.class);
		bind(MCRPermissionStrategy.class).to(MCRDefaultPermissionStrategy.class);
		
		bind(MCRMetadataStrategy.class).to(MCRAuthorMetadataStrategy.class);
		bind(MCRAuthorStrategy.class).to(MCRDefaultAuthorStrategy.class);
	}

}
