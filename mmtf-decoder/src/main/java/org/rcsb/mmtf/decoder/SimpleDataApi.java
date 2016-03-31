package org.rcsb.mmtf.decoder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.msgpack.jackson.dataformat.MessagePackFactory;
import org.rcsb.mmtf.api.DataApiInterface;
import org.rcsb.mmtf.arraydecompressors.DeltaDeCompress;
import org.rcsb.mmtf.arraydecompressors.RunLengthDecodeInt;
import org.rcsb.mmtf.arraydecompressors.RunLengthDecodeString;
import org.rcsb.mmtf.arraydecompressors.RunLengthDelta;
import org.rcsb.mmtf.dataholders.BioAssemblyData;
import org.rcsb.mmtf.dataholders.Entity;
import org.rcsb.mmtf.dataholders.MmtfBean;
import org.rcsb.mmtf.dataholders.PDBGroup;

import com.fasterxml.jackson.databind.ObjectMapper;

public class SimpleDataApi implements DataApiInterface {
	
	
	public SimpleDataApi(byte[] inputByteArr) {
		
		
		MmtfBean inputData = null;
		try {
			inputData = new ObjectMapper(new MessagePackFactory()).readValue(inputByteArr, MmtfBean.class);
		} catch (IOException e) {
			// 
			System.out.println("Error converting Byte array to message pack. IOError");
			e.printStackTrace();
			throw new RuntimeException();
		}
		
		// Get the decompressors to build in the data structure
		DeltaDeCompress deltaDecompress = new DeltaDeCompress();
		RunLengthDelta intRunLengthDelta = new RunLengthDelta();
		RunLengthDecodeInt intRunLength = new RunLengthDecodeInt();
		RunLengthDecodeString stringRunlength = new RunLengthDecodeString();
		DecoderUtils decoderUtils = new DecoderUtils();
		
		// Get the data
		try {
			groupList = decoderUtils.bytesToInts(inputData.getGroupTypeList());
			// Read the byte arrays as int arrays
			cartnX = decoderUtils.decodeIntsToFloats(deltaDecompress.decompressByteArray(inputData.getxCoordBig(), inputData.getxCoordSmall()), MmtfBean.COORD_DIVIDER);
			cartnY = decoderUtils.decodeIntsToFloats(deltaDecompress.decompressByteArray(inputData.getyCoordBig(), inputData.getyCoordSmall()), MmtfBean.COORD_DIVIDER);
			cartnZ = decoderUtils.decodeIntsToFloats(deltaDecompress.decompressByteArray(inputData.getzCoordBig(), inputData.getzCoordSmall()), MmtfBean.COORD_DIVIDER);
			bFactor =  decoderUtils.decodeIntsToFloats(deltaDecompress.decompressByteArray(inputData.getbFactorBig(),inputData.getbFactorSmall()), MmtfBean.OCCUPANCY_BFACTOR_DIVIDER);
			occupancy = decoderUtils.decodeIntsToFloats(intRunLength.decompressByteArray(inputData.getOccList()), MmtfBean.OCCUPANCY_BFACTOR_DIVIDER);
			atomId = intRunLengthDelta.decompressByteArray(inputData.getAtomIdList());
			altId = stringRunlength.stringArrayToChar(
					(ArrayList<String>) inputData.getAltLabelList());
			// Get the insertion code
			insertionCodeList = stringRunlength.stringArrayToChar(
					(ArrayList<String>) inputData.getInsCodeList());
			// Get the groupNumber
			groupNum = intRunLengthDelta.decompressByteArray(
					inputData.getGroupIdList());
			groupMap = inputData.getGroupMap();
			// Get the seqRes groups
			seqResGroupList = intRunLengthDelta.decompressByteArray(inputData.getSeqResIdList());
			// Get the number of chains per model
			chainsPerModel = inputData.getChainsPerModel();
			groupsPerChain = inputData.getGroupsPerChain();
			// Get the internal and public facing chain ids
			publicChainIds = decoderUtils.decodeChainList(inputData.getChainNameList());
			chainList = decoderUtils.decodeChainList(inputData.getChainIdList());
			spaceGroup = inputData.getSpaceGroup();
			unitCell = inputData.getUnitCell();
			bioAssembly  = inputData.getBioAssemblyList();
			interGroupBondIndices = decoderUtils.bytesToInts(inputData.getBondAtomList());
			interGroupBondOrders = decoderUtils.bytesToByteInts(inputData.getBondOrderList());
			mmtfVersion = inputData.getMmtfVersion();
			mmtfProducer = inputData.getMmtfProducer();
			entityList = inputData.getEntityList();
			pdbId = inputData.getPdbId();

		}
		catch (IOException ioException){
			System.out.println("Error reading in byte arrays from message pack");
			ioException.printStackTrace();
			throw new RuntimeException();
		}
	}
	
	
	/** The X coordinates */
	private float[] cartnX;

	/** The Y coordinates */
	private float[] cartnY;

	/** The Z coordinates */
	private float[] cartnZ;

	/** The X coordinates */
	private float[] bFactor;

	/** The Y coordinates */
	private float[] occupancy;
	
	/** The atom id. */
	private int[] atomId;

	/** The alt id. */
	private char[] altId;

	/** The ins code. */
	private char[] insertionCodeList;

	/** The group num. */
	private int[] groupNum;

	/** The group map. */
	private Map<Integer, PDBGroup> groupMap;

	/** The group list. */
	private int[] groupList;

	/** The sequence ids of the groups */
	private int[] seqResGroupList;

	/** The public facing chain ids*/
	private String[] publicChainIds;

	/** The number of chains per model*/
	private int[] chainsPerModel;

	/** The number of groups per (internal) chain*/
	private int[] groupsPerChain;

	/** The space group of the structure*/
	private String spaceGroup;

	/** The unit cell of the structure*/
	private float[] unitCell;

	/** The bioassembly information for the structure*/
	private List<BioAssemblyData> bioAssembly;

	/** The bond indices for bonds between groups*/
	private int[] interGroupBondIndices;

	/** The bond orders for bonds between groups*/
	private int[] interGroupBondOrders;

	/** The chosen list of chain ids */
	private String[] chainList;

	/** The mmtf version */
	private String mmtfVersion;

	/** The mmtf prodcuer */
	private String mmtfProducer;

	/** A list containing pdb group names for nucleic acids */
	List<String> nucAcidList = new ArrayList<>();

	/** The list of entities in this structure. */
	private Entity[] entityList;

	/** The PDB id	 */
	private String pdbId;


	
	@Override
	public float[] getXcoords() {
		return cartnX;
	}

	@Override
	public void setXcoords(float[] xCoords) {
		this.cartnX = xCoords;
	}

	@Override
	public float[] getYcoords() {
		return cartnY;
	}

	@Override
	public void setYcoords(float[] yCoords) {
		this.cartnY = yCoords;
		
	}

	@Override
	public float[] getZcoords() {
		return cartnZ;
	}

	@Override
	public void setZcoords(float[] zCoords) {
		this.cartnZ = zCoords;
		
	}

	@Override
	public float[] getBfactors() {
		return bFactor;
	}

	@Override
	public void setBfactors(float[] bFactors) {
		this.bFactor = bFactors;
		
	}

	@Override
	public float[] getOccupancies() {
		return occupancy;
	}

	@Override
	public void setOccupancies(float[] occupancies) {
		this.occupancy = occupancies;
		
	}

	@Override
	public String[] getChainList() {
		return this.chainList;
	}

	@Override
	public void setChainList(String[] chainList) {
		this.chainList = chainList;
	}

	/* (non-Javadoc)
	 * @see org.rcsb.mmtf.decoder.DataApiInterface#getAtomId()
	 */
	@Override
	public int[] getAtomIds() {
		return atomId;
	}

	/* (non-Javadoc)
	 * @see org.rcsb.mmtf.decoder.DataApiInterface#setAtomId(int[])
	 */
	@Override
	public void setAtomIds(int[] atomId) {
		this.atomId = atomId;
	}

	/* (non-Javadoc)
	 * @see org.rcsb.mmtf.decoder.DataApiInterface#getAltId()
	 */
	@Override
	public char[] getAltLocIds() {
		return altId;
	}

	/* (non-Javadoc)
	 * @see org.rcsb.mmtf.decoder.DataApiInterface#setAltId(char[])
	 */
	@Override
	public void setAltLocIds(char[] altId) {
		this.altId = altId;
	}

	/* (non-Javadoc)
	 * @see org.rcsb.mmtf.decoder.DataApiInterface#getInsCode()
	 */
	@Override
	public char[] getInsCodes() {
		return insertionCodeList;
	}

	/* (non-Javadoc)
	 * @see org.rcsb.mmtf.decoder.DataApiInterface#setInsCode(char[])
	 */
	@Override
	public void setInsCodes(char[] insCode) {
		this.insertionCodeList = insCode;
	}

	/* (non-Javadoc)
	 * @see org.rcsb.mmtf.decoder.DataApiInterface#getGroupNum()
	 */
	@Override
	public int[] getResidueNums() {
		return groupNum;
	}

	/* (non-Javadoc)
	 * @see org.rcsb.mmtf.decoder.DataApiInterface#setGroupNum(int[])
	 */
	@Override
	public void setResidueNums(int[] groupNum) {
		this.groupNum = groupNum;
	}

	/* (non-Javadoc)
	 * @see org.rcsb.mmtf.decoder.DataApiInterface#getGroupMap()
	 */
	@Override
	public Map<Integer, PDBGroup> getGroupMap() {
		return groupMap;
	}

	/* (non-Javadoc)
	 * @see org.rcsb.mmtf.decoder.DataApiInterface#setGroupMap(java.util.Map)
	 */
	@Override
	public void setGroupMap(Map<Integer, PDBGroup> groupMap) {
		this.groupMap = groupMap;
	}

	/* (non-Javadoc)
	 * @see org.rcsb.mmtf.decoder.DataApiInterface#getGroupList()
	 */
	@Override
	public int[] getGroupIndices() {
		return groupList;
	}

	/* (non-Javadoc)
	 * @see org.rcsb.mmtf.decoder.DataApiInterface#setGroupList(int[])
	 */
	@Override
	public void setGroupIndices(int[] groupList) {
		this.groupList = groupList;
	}

	/* (non-Javadoc)
	 * @see org.rcsb.mmtf.decoder.DataApiInterface#getSeqResGroupList()
	 */
	@Override
	public int[] getSeqResGroupIndices() {
		return seqResGroupList;
	}

	/* (non-Javadoc)
	 * @see org.rcsb.mmtf.decoder.DataApiInterface#setSeqResGroupList(int[])
	 */
	@Override
	public void setSeqResGroupIndices(int[] seqResGroupList) {
		this.seqResGroupList = seqResGroupList;
	}

	/* (non-Javadoc)
	 * @see org.rcsb.mmtf.decoder.DataApiInterface#getPublicChainIds()
	 */
	@Override
	public String[] getChainNames() {
		return publicChainIds;
	}

	/* (non-Javadoc)
	 * @see org.rcsb.mmtf.decoder.DataApiInterface#setPublicChainIds(java.lang.String[])
	 */
	@Override
	public void setChainNames(String[] publicChainIds) {
		this.publicChainIds = publicChainIds;
	}

	/* (non-Javadoc)
	 * @see org.rcsb.mmtf.decoder.DataApiInterface#getChainsPerModel()
	 */
	@Override
	public int[] getChainsPerModel() {
		return chainsPerModel;
	}

	/* (non-Javadoc)
	 * @see org.rcsb.mmtf.decoder.DataApiInterface#setChainsPerModel(int[])
	 */
	@Override
	public void setChainsPerModel(int[] chainsPerModel) {
		this.chainsPerModel = chainsPerModel;
	}

	/* (non-Javadoc)
	 * @see org.rcsb.mmtf.decoder.DataApiInterface#getGroupsPerChain()
	 */
	@Override
	public int[] getGroupsPerChain() {
		return groupsPerChain;
	}

	/* (non-Javadoc)
	 * @see org.rcsb.mmtf.decoder.DataApiInterface#setGroupsPerChain(int[])
	 */
	@Override
	public void setGroupsPerChain(int[] groupsPerChain) {
		this.groupsPerChain = groupsPerChain;
	}

	/* (non-Javadoc)
	 * @see org.rcsb.mmtf.decoder.DataApiInterface#getSpaceGroup()
	 */
	@Override
	public String getSpaceGroup() {
		return spaceGroup;
	}

	/* (non-Javadoc)
	 * @see org.rcsb.mmtf.decoder.DataApiInterface#setSpaceGroup(java.lang.String)
	 */
	@Override
	public void setSpaceGroup(String spaceGroup) {
		this.spaceGroup = spaceGroup;
	}

	/* (non-Javadoc)
	 * @see org.rcsb.mmtf.decoder.DataApiInterface#getUnitCell()
	 */
	@Override
	public float[] getUnitCell() {
		return unitCell;
	}

	/* (non-Javadoc)
	 * @see org.rcsb.mmtf.decoder.DataApiInterface#setUnitCell(java.util.List)
	 */
	@Override
	public void setUnitCell(float[] inputUnitCell) {
		this.unitCell = inputUnitCell;
	}

	/* (non-Javadoc)
	 * @see org.rcsb.mmtf.decoder.DataApiInterface#getBioAssembly()
	 */
	@Override
	public List<BioAssemblyData> getBioAssemblyList() {
		return bioAssembly;
	}

	/* (non-Javadoc)
	 * @see org.rcsb.mmtf.decoder.DataApiInterface#setBioAssembly(java.util.Map)
	 */
	@Override
	public void setBioAssemblyList(List<BioAssemblyData> bioAssembly) {
		this.bioAssembly = bioAssembly;
	}

	/* (non-Javadoc)
	 * @see org.rcsb.mmtf.decoder.DataApiInterface#getInterGroupBondIndices()
	 */
	@Override
	public int[] getInterGroupBondIndices() {
		return interGroupBondIndices;
	}

	/* (non-Javadoc)
	 * @see org.rcsb.mmtf.decoder.DataApiInterface#setInterGroupBondIndices(int[])
	 */
	@Override
	public void setInterGroupBondIndices(int[] interGroupBondIndices) {
		this.interGroupBondIndices = interGroupBondIndices;
	}

	/* (non-Javadoc)
	 * @see org.rcsb.mmtf.decoder.DataApiInterface#getInterGroupBondOrders()
	 */
	@Override
	public int[] getInterGroupBondOrders() {
		return interGroupBondOrders;
	}

	/* (non-Javadoc)
	 * @see org.rcsb.mmtf.decoder.DataApiInterface#setInterGroupBondOrders(int[])
	 */
	@Override
	public void setInterGroupBondOrders(int[] interGroupBondOrders) {
		this.interGroupBondOrders = interGroupBondOrders;
	}

	/* (non-Javadoc)
	 * @see org.rcsb.mmtf.decoder.DataApiInterface#getChainList()
	 */
	@Override
	public String[] getChainIds() {
		return chainList;
	}

	/* (non-Javadoc)
	 * @see org.rcsb.mmtf.decoder.DataApiInterface#setChainList(java.lang.String[])
	 */
	@Override
	public void setChainIds(String[] chainList) {
		this.chainList = chainList;
	}

	/* (non-Javadoc)
	 * @see org.rcsb.mmtf.decoder.DataApiInterface#getMmtfVersion()
	 */
	@Override
	public String getMmtfVersion() {
		return mmtfVersion;
	}

	/* (non-Javadoc)
	 * @see org.rcsb.mmtf.decoder.DataApiInterface#setMmtfVersion(java.lang.String)
	 */
	@Override
	public void setMmtfVersion(String mmtfVersion) {
		this.mmtfVersion = mmtfVersion;
	}

	/* (non-Javadoc)
	 * @see org.rcsb.mmtf.decoder.DataApiInterface#getMmtfProducer()
	 */
	@Override
	public String getMmtfProducer() {
		return mmtfProducer;
	}

	/* (non-Javadoc)
	 * @see org.rcsb.mmtf.decoder.DataApiInterface#setMmtfProducer(java.lang.String)
	 */
	@Override
	public void setMmtfProducer(String mmtfProducer) {
		this.mmtfProducer = mmtfProducer;
	}


	/* (non-Javadoc)
	 * @see org.rcsb.mmtf.decoder.DataApiInterface#getEntityList()
	 */
	@Override
	public Entity[] getEntityList() {
		return entityList;
	}

	/* (non-Javadoc)
	 * @see org.rcsb.mmtf.decoder.DataApiInterface#setEntityList(org.rcsb.mmtf.dataholders.Entity[])
	 */
	@Override
	public void setEntityList(Entity[] entityList) {
		this.entityList = entityList;
	}

	/* (non-Javadoc)
	 * @see org.rcsb.mmtf.decoder.DataApiInterface#getPdbId()
	 */
	@Override
	public String getPdbId() {
		return pdbId;
	}

	/* (non-Javadoc)
	 * @see org.rcsb.mmtf.decoder.DataApiInterface#setPdbId(java.lang.String)
	 */
	@Override
	public void setPdbId(String pdbId) {
		this.pdbId = pdbId;
	}

	
	/* (non-Javadoc)
	 * @see org.rcsb.mmtf.decoder.DataApiInterface#getNumResiudes()
	 */
	@Override
	public int getNumResidues() {
		return this.groupList.length;
	}

	/* (non-Javadoc)
	 * @see org.rcsb.mmtf.decoder.DataApiInterface#getNumChains()
	 */
	@Override
	public int getNumChains() {
		return this.chainList.length;
	}

	
	/* (non-Javadoc)
	 * @see org.rcsb.mmtf.decoder.DataApiInterface#getNumModels()
	 */
	@Override
	public int getNumModels() {	
		return this.chainsPerModel.length;
	}

	/* (non-Javadoc)
	 * @see org.rcsb.mmtf.decoder.DataApiInterface#getNumAtoms()
	 */
	@Override
	public int getNumAtoms() {
		return this.cartnX.length;
	}



}
