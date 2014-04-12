package org.mycore.frontend.workflowengine.guice;

import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowManager;
import org.mycore.frontend.workflowengine.jbpm.publication.MCRDocumentDerivateStrategy;
import org.mycore.frontend.workflowengine.jbpm.publication.MCRDocumentMetadataStrategy;
import org.mycore.frontend.workflowengine.jbpm.publication.MCRWorkflowManagerPublication;
import org.mycore.frontend.workflowengine.strategies.MCRDefaultPermissionStrategy;
import org.mycore.frontend.workflowengine.strategies.MCRDerivateStrategy;
import org.mycore.frontend.workflowengine.strategies.MCRIdentifierStrategy;
import org.mycore.frontend.workflowengine.strategies.MCRMetadataStrategy;
import org.mycore.frontend.workflowengine.strategies.MCRPermissionStrategy;
import org.mycore.frontend.workflowengine.strategies.MCRURNIdentifierStrategy;

import com.google.inject.AbstractModule;

public class MCRPublicationWorkflowModule  extends AbstractModule {

	public void configure() {
		bind(MCRWorkflowManager.class).to(MCRWorkflowManagerPublication.class);
		
		bind(MCRPermissionStrategy.class).to(MCRDefaultPermissionStrategy.class);
		
		bind(MCRDerivateStrategy.class).to(MCRDocumentDerivateStrategy.class);
		bind(MCRMetadataStrategy.class).to(MCRDocumentMetadataStrategy.class);
		
		bind(MCRIdentifierStrategy.class).to(MCRURNIdentifierStrategy.class);
		
	
	}

}
