package zutil.network.nio.message;


public class GraphicsSyncMessage extends SyncMessage{
	private static final long serialVersionUID = 1L;
	
	public float locX;
	public float locY;
	public float locZ;
	
	public float rotX;
	public float rotY;
	public float rotZ;
	public float rotW;
	
	public GraphicsSyncMessage(String id){
		this.type = MessageType.SYNC;
		this.id = id;
	}
	
	public boolean equals(Object obj){
		if(obj instanceof GraphicsSyncMessage){
			GraphicsSyncMessage tmp = (GraphicsSyncMessage)obj;
			return (tmp.locX == locX &&	tmp.locY == locY &&
					tmp.locZ == locZ &&	tmp.rotX == rotX &&
					tmp.rotY == rotY && tmp.rotZ == rotZ &&
					tmp.rotW == rotW);
		}
		return false;
	}
}
