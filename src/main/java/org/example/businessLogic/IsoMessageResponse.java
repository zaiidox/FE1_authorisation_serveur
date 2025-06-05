package org.example.businessLogic;

import org.example.util.IsoMessagePrinter;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

@Component
public class IsoMessageResponse {


    public void sendAuthResponse(ISOMsg req,
                                 Socket clientSocket,
                                 String codeResponse,
                                 String errorDetail)
            throws ISOException, IOException {

        ISOMsg resp = buildResponseFromRequest(req, codeResponse, errorDetail);

        IsoMessagePrinter.printISOMessage(resp, "Réponse Envoyée");

        OutputStream os = clientSocket.getOutputStream();
        byte[] data = resp.pack();
        os.write(data);
        os.flush();
    }

    private ISOMsg buildResponseFromRequest(ISOMsg req, String codeResponse, String errorDetail) throws ISOException {
        ISOMsg resp = new ISOMsg();
        resp.setPackager(req.getPackager());
        resp.setMTI("0110");

        int[] toCopy = {
                2, 3, 4, 7, 11, 12, 13, 14, 17, 18,
                22, 24, 27, 32, 35, 37, 41, 42, 43,
                49, 60, 61, 63, 121, 123, 126
        };
        for (int f : toCopy) {
            if (req.hasField(f)) {
                resp.set(f, req.getString(f));
            }
        }

        resp.set(39, codeResponse);



        return resp;
    }
}
