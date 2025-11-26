package com.medbook.appointmentservice.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class QRService {

    public byte[] generateQRCode(String text) {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matrix = writer.encode(text, BarcodeFormat.QR_CODE, 300, 300);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, "PNG", stream);
            return stream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi tạo QR", e);
        }
    }
}
