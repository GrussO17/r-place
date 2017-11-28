package place.network;

import place.PlaceException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class PlaceExchange {
    public static void send(PlaceRequest req, ObjectOutputStream out)
            throws PlaceException {
        try {
            out.writeObject(req);
            out.flush();
        } catch (IOException e) {
            throw new PlaceException(e);
        }
    }

    public static PlaceRequest recieve(ObjectInputStream in)
            throws PlaceException {
        try {
            return (PlaceRequest) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new PlaceException(e);
        }
    }
}
