package eu.interopehrate.mr2da.provenance;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Provenance;
import org.hl7.fhir.r4.model.Resource;

/**
 *  Author: Engineering S.p.A. (www.eng.it)
 *  Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description:
 */
public class ResourceNode {
	
	protected ResourceNode parent;
	protected Resource resource;
	protected List<ResourceNode> children = new ArrayList<ResourceNode>();
		
	ResourceNode (Resource resource) {
		this.resource = resource;
	}
	
	Resource getResource() {
		return resource;
	}

	void setParent(ResourceNode parent) {
		if (this.parent != null)
			this.parent.removeChild(this);			
		
		this.parent = parent;
		parent.addChild(this);
	}
		
	List<ResourceNode> getChildren() {
		return children;
	}
	
	void addChild (ResourceNode n) {
		children.add(n);
	}
	
	void removeChild(ResourceNode n) {
		children.remove(n);
	}
	
	/*
	 * 
	 */
	ResourceNode searchNodeByResourceId(String id) {
		if (resource != null && id.equals(resource.getId()))
			return this;
		else {
			ResourceNode n;
			for (ResourceNode c : children) {
				n = c.searchNodeByResourceId(id);
				if (n != null)
					return n;
			}	
		}
		
		return null;
	}
	
	/*
	 * Default implementation for node that does not have children
	 */
	void loadChildren(ResourceNode root) {}

	
	void printTree(int level) {
		for (int i = 1; i < level; i++)
			System.out.print("   ");
				
		System.out.println(resource.getId());
		
		level++;
		for (ResourceNode n : children)
			n.printTree(level);	
	}

}
