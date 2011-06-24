package zutil.net.update;

import java.io.Serializable;

/**
 * A class that contains configuration information
 * 
 * @author Ziver
 */
class UpdateConfigMessage implements Serializable{
	private static final long serialVersionUID = 1L;
	
	protected String hashAlgorithm;
	protected boolean compression;
}