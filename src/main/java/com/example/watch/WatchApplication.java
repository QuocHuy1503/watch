package com.example.watch;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Map;

@SpringBootApplication
public class WatchApplication {

    public static void loadEnv() {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        Cloudinary cloudinary = new Cloudinary(dotenv.get("CLOUDINARY_URL"));
        dotenv.entries().forEach(entry ->
                System.setProperty(entry.getKey(), entry.getValue())
        );
    }
    Map params1 = ObjectUtils.asMap(
            "use_filename", true,
            "unique_filename", false,
            "overwrite", true
    );

    public static void main(String[] args) {
        loadEnv(); // Gọi hàm ở chính class này
        SpringApplication.run(WatchApplication.class, args);
    }

}
