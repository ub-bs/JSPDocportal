package org.mycore.frontend.workflowengine.guice;

import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowManager;
import org.mycore.frontend.workflowengine.jbpm.xmetadiss.MCRDisshabDerivateStrategy;
import org.mycore.frontend.workflowengine.jbpm.xmetadiss.MCRDisshabMetadataStrategy;
import org.mycore.frontend.workflowengine.jbpm.xmetadiss.MCRWorkflowManagerXmetadiss;
import org.mycore.frontend.workflowengine.strategies.MCRAuthorStrategy;
import org.mycore.frontend.workflowengine.strategies.MCRDefaultAuthorStrategy;
import org.mycore.frontend.workflowengine.strategies.MCRDefaultPermissionStrategy;
import org.mycore.frontend.workflowengine.strategies.MCRDerivateStrategy;
import org.mycore.frontend.workflowengine.strategies.MCRIdentifierStrategy;
import org.mycore.frontend.workflowengine.strategies.MCRMetadataStrategy;
import org.mycore.frontend.workflowengine.strategies.MCRPermissionStrategy;
import org.mycore.frontend.workflowengine.strategies.MCRURNIdentifierStrategy;

import com.google.inject.AbstractModule;

public class MCRXmetadissWorkflowModule  extends AbstractModule {

	public void configure() {
		bind(MCRWorkflowManager.class).to(MCRWorkflowManagerXmetadiss.class);
		
		bind(MCRPermissionStrategy.class).to(MCRDefaultPermissionStrategy.class);
		
		bind(MCRDerivateStrategy.class).to(MCRDisshabDerivateStrategy.class);
		bind(MCRMetadataStrategy.class).to(MCRDisshabMetadataStrategy.class);
	
		bind(MCRIdentifierStrategy.class).to(MCRURNIdentifierStrategy.class);
		bind(MCRAuthorStrategy.class).to(MCRDefaultAuthorStrategy.class);
	}

}
