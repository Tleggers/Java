package repository;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {
    List<String> uploadImages(MultipartFile[] images) throws Exception;
}
