package org.mycore.frontend.workflowengine.guice;

import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowManager;
import org.mycore.frontend.workflowengine.jbpm.institution.MCRInstitutionMetadataStrategy;
import org.mycore.frontend.workflowengine.jbpm.institution.MCRWorkflowManagerInstitution;
import org.mycore.frontend.workflowengine.strategies.MCRDefaultDerivateStrategy;
import org.mycore.frontend.workflowengine.strategies.MCRDefaultInstitutionStrategy;
import org.mycore.frontend.workflowengine.strategies.MCRDefaultPermissionStrategy;
import org.mycore.frontend.workflowengine.strategies.MCRDerivateStrategy;
import org.mycore.frontend.workflowengine.strategies.MCRInstitutionStrategy;
import org.mycore.frontend.workflowengine.strategies.MCRMetadataStrategy;
import org.mycore.frontend.workflowengine.strategies.MCRPermissionStrategy;

import com.google.inject.AbstractModule;

public class MCRInstitutionWorkflowModule  extends AbstractModule {

	public void configure() {
		bind(MCRWorkflowManager.class).to(MCRWorkflowManagerInstitution.class);
		
		bind(MCRDerivateStrategy.class).to(MCRDefaultDerivateStrategy.class);
		bind(MCRPermissionStrategy.class).to(MCRDefaultPermissionStrategy.class);
		
		bind(MCRMetadataStrategy.class).to(MCRInstitutionMetadataStrategy.class);
		bind(MCRInstitutionStrategy.class).to(MCRDefaultInstitutionStrategy.class);
	}

}
 