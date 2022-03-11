package pl.wolskak.mycomputerservice.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class FileConverter {
    public static byte[] convertFileToByteArray(MultipartFile file) {
        byte[] byteArray;

        try {
            byteArray = file.getBytes();

            if (file.getBytes().length == 0) {
                return null;
            }

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return byteArray;
    }
}
