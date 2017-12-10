package place.network;

import place.PlaceException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * PlaceExchange class; provides methods for sending and receiving PlaceRequests,
 * converting IOExceptions into PlaceExceptions
 */
public class PlaceExchange {
    /**
     * Send a PlaceRequest to the specified ObjectOutputStream
     *
     * @param out the output stream to send on
     * @param req the request to send
     * @throws PlaceException if there is an error sending the request
     */
    public static void send(ObjectOutputStream out, PlaceRequest req)
            throws PlaceException {
        try {
            out.writeUnshared(req);
            out.flush();
        } catch (IOException e) {
            throw new PlaceException(e);
        }
    }

    /**
     * Receives a PlaceRequest from the specified input stream
     *
     * @param in the ObjectInputStream to send on
     * @return the received PlaceRequest
     * @throws PlaceException if there is an error receiving a request
     */
    public static PlaceRequest receive(ObjectInputStream in)
            throws PlaceException {
        try {
            return (PlaceRequest) in.readUnshared();
        } catch (IOException | ClassNotFoundException e) {
            throw new PlaceException(e);
        }
    }
}
