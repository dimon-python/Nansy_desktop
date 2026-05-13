package com.example.Nansy_desktop;

import java.awt.Color;
import java.awt.image.BufferedImage;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class UIHandler extends Application{

    private static VBox root;
    private static ImageView qrImageView;

    @Override
    public void start(Stage stage) {

        root = new VBox(10);
        root.setAlignment(Pos.CENTER);

        qrImageView = new ImageView();
        qrImageView.setFitWidth(250);
        qrImageView.setFitHeight(250);
        qrImageView.setPreserveRatio(true);

        root.getChildren().add(qrImageView);

        Scene scene = new Scene(root, 300, 350);

        stage.setScene(scene);
        stage.show();
    }

    public static WritableImage generateQR(String data, int width, int height) throws WriterException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        BitMatrix token = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, width, height);

        MatrixToImageConfig config = new MatrixToImageConfig(new Color(65, 105, 225).getRGB(), new Color(64, 64, 64).getRGB());

        BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(token, config);

        return SwingFXUtils.toFXImage(bufferedImage, null);
    }

    public static void setQRImage(String data) {
        try {
            WritableImage qrImage = generateQR(data, 250, 250);

            Platform.runLater(() -> {
                qrImageView.setImage(qrImage);
            });
        } catch (WriterException e) {
            System.err.println(e);
        }
    }
}
