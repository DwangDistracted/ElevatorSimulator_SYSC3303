package elevatorsim.common.requests;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public abstract class Request<T extends Request <T>> implements Serializable {
	private static final long serialVersionUID = -404468803509482396L;

	/**
	 * Serialize the object into bytes in order to send the data over a 
	 * datagram socket 
	 * @param baos
	 */
	public void serialize(ByteArrayOutputStream baos) {
		try {
			ObjectOutputStream stream = new ObjectOutputStream(baos);
			stream.writeObject(this);
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Deserialize an ElevatorRequest object based of a given array
	 * of data values
	 * @param data The data to be deserialized into a message object
	 * @return return an object of the deserialized type
	 */
	@SuppressWarnings("unchecked")
	public static <T> T deserialize(byte[] data) {
		ByteArrayInputStream barr = new ByteArrayInputStream(data);
		try {
			ObjectInputStream in = new ObjectInputStream(barr);
			T unpackedRequest = (T)in.readObject();
			return (T)unpackedRequest;
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
