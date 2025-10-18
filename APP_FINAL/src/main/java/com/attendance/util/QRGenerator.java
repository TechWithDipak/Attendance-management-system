package com.attendance.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class QRGenerator {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static class SessionPayload {
        public String sessionId;
        public int teacherId;
        public String course;
        public long timestamp;
        public double teacherLatitude;
        public double teacherLongitude;
    }

    public static SessionPayload createPayload(int teacherId, String course, double teacherLat, double teacherLon) {
        SessionPayload payload = new SessionPayload();
        payload.sessionId = UUID.randomUUID().toString();
        payload.teacherId = teacherId;
        payload.course = course;
        payload.timestamp = Instant.now().toEpochMilli();
        payload.teacherLatitude = teacherLat;
        payload.teacherLongitude = teacherLon;
        return payload;
    }

    public static Path generateQrPng(SessionPayload payload, int size, Path outputPath) throws IOException {
        try {
            String json = OBJECT_MAPPER.writeValueAsString(payload);
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.MARGIN, 1);
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix bitMatrix = writer.encode(json, BarcodeFormat.QR_CODE, size, size, hints);
            BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                    image.setRGB(x, y, bitMatrix.get(x, y) ? Color.BLACK.getRGB() : Color.WHITE.getRGB());
                }
            }
            File file = outputPath.toFile();
            file.getParentFile().mkdirs();
            ImageIO.write(image, "png", file);
            return outputPath;
        } catch (Exception e) {
            if (e instanceof IOException) throw (IOException) e;
            throw new IOException("Failed to generate QR", e);
        }
    }
}


