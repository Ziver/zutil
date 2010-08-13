package zutil.network.ws;
public class WSParameterDef{
	/** The class type of the parameter **/
	protected Class<?> paramClass;
	/** The web service name of the parameter **/
	protected String name;
	/** Developer documentation **/
	protected String doc;
	/** If this parameter is optional **/
	protected boolean optional;
	/** Is it an header parameter **/
	//boolean header;
	
	
	public Class<?> getParamClass() {
		return paramClass;
	}
	protected void setParamClass(Class<?> paramClass) {
		this.paramClass = paramClass;
	}
	
	public String getName() {
		return name;
	}
	protected void setName(String name) {
		this.name = name;
	}
	
	public String getDoc() {
		return doc;
	}
	protected void setDoc(String doc) {
		this.doc = doc;
	}
	
	public boolean isOptional() {
		return optional;
	}
	protected void setOptional(boolean optional) {
		this.optional = optional;
	}
}