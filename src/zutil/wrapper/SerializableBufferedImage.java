package zutil.wrapper;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.imageio.ImageIO;

public class SerializableBufferedImage implements Serializable{
	private static final long serialVersionUID = 1L;

	private BufferedImage image;

	public SerializableBufferedImage(BufferedImage image){
		this.image = image;
	}
	
	public BufferedImage getImage(){
		return image;
	}
	
	public void setImage(BufferedImage image){
		this.image=image;
	}

	private void writeObject(ObjectOutputStream out)throws IOException{
		ImageIO.write(image,"jpeg",ImageIO.createImageOutputStream(out));
	}

	private void readObject(ObjectInputStream in)throws IOException, ClassNotFoundException{
		image = ImageIO.read(ImageIO.createImageInputStream(in));
	}
}