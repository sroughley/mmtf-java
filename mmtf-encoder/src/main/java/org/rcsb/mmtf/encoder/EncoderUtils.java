package org.rcsb.mmtf.encoder;

import java.util.ArrayList;
import java.util.List;

import org.rcsb.mmtf.api.StructureDataInterface;
import org.rcsb.mmtf.dataholders.BioAssemblyData;
import org.rcsb.mmtf.dataholders.BioAssemblyTransformation;
import org.rcsb.mmtf.dataholders.Entity;
import org.rcsb.mmtf.dataholders.Group;
import org.rcsb.mmtf.dataholders.MmtfStructure;
import org.rcsb.mmtf.utils.CodecUtils;

/**
 * A class of static utility functions to aid encoding of data.
 * Convert from {@link StructureDataInterface} into a format useable by
 * the {@link MmtfStructure}.
 * @author Anthony Bradley
 *
 */
public class EncoderUtils {

	/**
	 * Regenerate a group map from the {@link StructureDataInterface}.
	 * @param structureDataInterface the input interface
	 * @return a list of all the groups in the molecule
	 */
	public static Group[] generateGroupMap(StructureDataInterface structureDataInterface) {
		int[] groupTypeIndices = structureDataInterface.getGroupTypeIndices();
		int maxIndex = CodecUtils.findMaxInIntArray(groupTypeIndices);
		Group[] outGroupList = new Group[maxIndex+1];
		for (int i=0; i<maxIndex+1; i++) {
			// Generate this PDBGroup
			Group pdbGroup = new Group();
			pdbGroup.setFormalChargeList(structureDataInterface.getGroupAtomCharges(i));
			pdbGroup.setAtomNameList(structureDataInterface.getGroupAtomNames(i));
			pdbGroup.setBondAtomList(structureDataInterface.getGroupBondIndices(i));
			pdbGroup.setBondOrderList(structureDataInterface.getGroupBondOrders(i));
			pdbGroup.setChemCompType(structureDataInterface.getGroupChemCompType(i));
			pdbGroup.setElementList(structureDataInterface.getGroupElementNames(i));
			pdbGroup.setGroupName(structureDataInterface.getGroupName(i));
			pdbGroup.setSingleLetterCode(structureDataInterface.getGroupSingleLetterCode(i));
			outGroupList[i] = pdbGroup;
		}
		return outGroupList;
	}

	/**
	 * Find the bioassembly information as a list from the {@link StructureDataInterface}.
	 * @param structureDataInterface the interface from where to find the data
	 * @return a list of bioassembly information
	 */
	public static List<BioAssemblyData> generateBioassemblies(StructureDataInterface structureDataInterface) {
		int numBioassemblies = structureDataInterface.getNumBioassemblies();
		List<BioAssemblyData> outList = new ArrayList<>();
		for (int i=0; i<numBioassemblies; i++) {
			BioAssemblyData bioAssemblyData = new BioAssemblyData();
			outList.add(bioAssemblyData);
			List<BioAssemblyTransformation> transformList = new ArrayList<>();
			bioAssemblyData.setTransformList(transformList);
			int numTrans = structureDataInterface.getNumTransInBioassembly(i);
			for (int j=0; j<numTrans; j++) {
				BioAssemblyTransformation bioAssemblyTrans = new BioAssemblyTransformation();
				transformList.add(bioAssemblyTrans);
				bioAssemblyTrans.setChainIndexList(
						structureDataInterface.getChainIndexListForTransform(i, j));
				bioAssemblyTrans.setMatrix(
						structureDataInterface.getMatrixForTransform(i,j));
			}
		}
		return outList;
	}

	/**
	 * Generate the entity level information from the {@link StructureDataInterface}.
	 * @param structureDataInterface the input interface
	 * @return the list of entities
	 */
	public static Entity[] generateEntityList(StructureDataInterface structureDataInterface) {
		int numEntities =  structureDataInterface.getNumEntities();
		Entity[] outList = new Entity[numEntities];
		for (int i=0; i<numEntities;i++) {
			Entity entity = new Entity();
			entity.setChainIndexList(structureDataInterface.getEntityChainIndexList(i));
			entity.setDescription(structureDataInterface.getEntityDescription(i));
			entity.setSequence(structureDataInterface.getEntitySequence(i));
			entity.setType(structureDataInterface.getEntityType(i));
			outList[i] = entity;
		}
		return outList;
	}

}
