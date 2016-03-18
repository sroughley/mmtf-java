package org.rcsb.mmtf.dataholders;

import java.util.List;

/**
 * Representation of a Biological Assembly annotation as provided by the PDB.
 * Contains all the information required to build the Biological Assembly from
 * the asymmetric unit.
 * Note that the PDB allows for 1 or more Biological Assemblies for a given
 * entry.
 * They are identified by the id field.
 * Modified by Anthony Bradley for message pack.
 * @author duarte_j
 * @author Anthony Bradley
 */
public class BioAssemblyData {
	/**  The number of oligmers (parsed from the mmCif file).*/
	private int macroMolecularSize;

	/**
	 * The specific transformations of this bioassembly.
	 */
	private List<BioAssemblyTrans> transforms;


	/**
	 * Gets the transforms.
	 *
	 * @return the transforms
	 */
	public final List<BioAssemblyTrans> getTransforms() {
		return transforms;
	}

	/**
	 * Sets the transforms.
	 *
	 * @param inputTransforms the new transforms
	 */
	public final void setTransforms(final
			List<BioAssemblyTrans> inputTransforms) {
		this.transforms = inputTransforms;
	}

	/**
	 * @return the macroMolecularSize
	 */
	public int getMacroMolecularSize() {
		return macroMolecularSize;
	}

	/**
	 * @param macroMolecularSize the macroMolecularSize to set
	 */
	public void setMacroMolecularSize(int macroMolecularSize) {
		this.macroMolecularSize = macroMolecularSize;
	}
}
