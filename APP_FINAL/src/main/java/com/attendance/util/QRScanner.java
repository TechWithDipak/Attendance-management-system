package com.attendance.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class QRScanner {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static QRGenerator.SessionPayload parsePayload(String json) throws IOException {
        try {
            return OBJECT_MAPPER.readValue(json, QRGenerator.SessionPayload.class);
        } catch (Exception e) {
            if (e instanceof IOException) throw (IOException) e;
            throw new IOException("Failed to parse QR payload", e);
        }
    }

    public static String decodeFromImage(File file) throws IOException, NotFoundException {
        try {
            BufferedImage bufferedImage = ImageIO.read(file);
            LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            Result result = new MultiFormatReader().decode(bitmap);
            return result.getText();
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            if (e instanceof IOException) throw (IOException) e;
            throw new IOException("Failed to decode QR image", e);
        }
    }
}


