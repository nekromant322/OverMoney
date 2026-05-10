package com.override.recognizer_service.service.receipt;

import lombok.extern.slf4j.Slf4j;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatVector;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.Size;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Service
public class ImagePreprocessingService {

    private final String CANNY_FILES_DIRECTORY_NAME = "canny_images";

    public void preprocessImage(Path path) {
        try {
            processCannyDetector(path);
            processROIDetector(path);
            processAdaptiveThreshold(path);
        } catch (Exception e) {
            log.error("Receipt image preprocessing failed with error: ", e);
        } finally {
            deleteCannyImage(path);
        }
    }

    private void processCannyDetector(Path path) {
        String originalFilePath = path.toString();

        // Загружаем изображение в градиентах серого
        Mat image = opencv_imgcodecs.imread(originalFilePath, opencv_imgcodecs.IMREAD_GRAYSCALE);

        Mat edges = new Mat();

        // Размытие для подавления шума
        opencv_imgproc.GaussianBlur(image, image, new Size(5, 5), 1.5);

        // Детектор Канни
        opencv_imgproc.Canny(image, edges, 50, 150);

        Path outputDir = Path.of(CANNY_FILES_DIRECTORY_NAME);
        if (!Files.exists(outputDir)) {
            try {
                Files.createDirectories(outputDir);
            } catch (IOException e) {
                log.warn("Failed to create output directory {}", outputDir, e);
            }
        }

        Path outputPath = outputDir.resolve(path.getFileName());

        // запись результата Canny
        opencv_imgcodecs.imwrite(outputPath.toString(), edges);
    }

    private void processROIDetector(Path path) {
        String cannyFilePath = CANNY_FILES_DIRECTORY_NAME + File.separator + path.getFileName();

        // изображение с границами (результат Canny)
        Mat edges = opencv_imgcodecs.imread(cannyFilePath, opencv_imgcodecs.IMREAD_GRAYSCALE);

        // определение контуров
        MatVector contours = new MatVector();
        Mat hierarchy = new Mat();
        opencv_imgproc.findContours(
                edges.clone(),
                contours,
                hierarchy,
                opencv_imgproc.RETR_EXTERNAL,
                opencv_imgproc.CHAIN_APPROX_SIMPLE
        );

        // самый большой прямоугольный контур - чек
        Rect bestRect = null;
        double maxArea = 0;

        for (int i = 0; i < contours.size(); i++) {
            Mat contour = contours.get(i);
            Rect rect = opencv_imgproc.boundingRect(contour);

            double area = rect.width() * rect.height();

            if (area > maxArea) {
                maxArea = area;
                bestRect = rect;
            }
        }

        if (bestRect != null) {
            // оригинальное изображение (до Canny!)
            Mat original = opencv_imgcodecs.imread(path.toString());

            // отрезание ROI
            Mat roi = new Mat(original, bestRect);

            // перезапись оригинального фото на обрезанное
            opencv_imgcodecs.imwrite(path.toString(), roi);
        }
    }

    private void processAdaptiveThreshold(Path path) {
        String filePath = path.toString();

        // считывание файла в градиентах серого
        Mat image = opencv_imgcodecs.imread(filePath, opencv_imgcodecs.IMREAD_GRAYSCALE);

        // размытие для уменьшения шума
        opencv_imgproc.GaussianBlur(image, image, new Size(3, 3), 0);

        // адаптивная бинаризация (выделение текста)
        Mat binary = new Mat();
        opencv_imgproc.adaptiveThreshold(
                image,
                binary,
                255,
                opencv_imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
                opencv_imgproc.THRESH_BINARY,
                5,  // размер окна
                3    // смещение
        );

        // увеличение размеров изображения
        opencv_imgproc.resize(binary, binary, new Size(binary.cols() * 4, binary.rows() * 4));

        // перезапись файла
        opencv_imgcodecs.imwrite(filePath, binary);
    }

    private void deleteCannyImage(Path path) {
        Path cannyImagePath = Path.of(CANNY_FILES_DIRECTORY_NAME).resolve(path.getFileName());
        try {
            Files.deleteIfExists(cannyImagePath);
        } catch (IOException e) {
            log.warn("Failed to delete temporary Canny image: {}", cannyImagePath, e);
        }
    }
}
