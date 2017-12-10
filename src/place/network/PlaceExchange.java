package place.network;

import place.PlaceException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class PlaceExchange {
    public static void send(ObjectOutputStream out, PlaceRequest req)
            throws PlaceException {
        try {
            out.writeUnshared(req);
            out.flush();
        } catch (IOException e) {
            throw new PlaceException(e);
        }
    }

    public static PlaceRequest receive(ObjectInputStream in)
            throws PlaceException {
        try {
            return (PlaceRequest) in.readUnshared();
        } catch (IOException | ClassNotFoundException e) {
            throw new PlaceException(e);
        }
    }
}
