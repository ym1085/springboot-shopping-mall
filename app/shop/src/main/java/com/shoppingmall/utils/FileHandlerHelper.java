package com.shoppingmall.utils;

import com.shoppingmall.constant.Category;
import com.shoppingmall.constant.FileExtension;
import com.shoppingmall.constant.FileType;
import com.shoppingmall.dto.request.FileSaveRequestDto;
import com.shoppingmall.dto.request.PostFileSaveRequestDto;
import com.shoppingmall.dto.request.ProductFileSaveRequestDto;
import com.shoppingmall.exception.FileException;
import com.shoppingmall.vo.Files;
import com.shoppingmall.vo.PostFiles;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.shoppingmall.common.code.failure.file.FileFailureCode.*;

@Slf4j
@Getter
@Component
public class FileHandlerHelper {

    @Value("${file.upload-dir}")
    private String uploadPath;

    private final ResourceLoader resourceLoader;

    public FileHandlerHelper(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    private static final Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");

    /**
     * Linux Server 특정 경로(외부 경로)에 도메인에 따라서(POSTS, PRODUCT) 파일 업로드 진행
     * @param multipartFiles 업로드 하고자 하는 파일 목록 (리스트)
     * @return
     */
    public List<FileSaveRequestDto> uploadFilesToServer(List<MultipartFile> multipartFiles, FileType fileType, String categoryName) {
        List<FileSaveRequestDto> baseFileSaveRequestDtos = new ArrayList<>();
        for (MultipartFile file : multipartFiles) {
            if (file.isEmpty()) {
                continue;
            }
            FileSaveRequestDto baseFileSaveRequestDto = this.uploadFilesToServer(file, fileType, categoryName); // 서버 상에 파일 업로드 진행
            baseFileSaveRequestDtos.add(baseFileSaveRequestDto);
        }
        return baseFileSaveRequestDtos;
    }

    private FileSaveRequestDto uploadFilesToServer(MultipartFile multipartFile, FileType fileType, String categoryName) {
        FileSaveRequestDto fileSaveRequestDto = null;
        if (multipartFile.isEmpty() || fileType == null || !StringUtils.hasText(categoryName)) {
            return null;
        }

        try {
            String originalFilename = (StringUtils.hasText(multipartFile.getOriginalFilename())) ? multipartFile.getOriginalFilename() : "";
            long fileSize = (multipartFile.getSize() > 0) ? multipartFile.getSize() : 0;

            String ext = getFileExtension(originalFilename);
            if (!validateFileExtension(ext)) {
                return null;
            }

            String storedFileName = createStoredFileName(originalFilename);
            String uploadFileFullPath = createFullFilePath(fileType, storedFileName, categoryName);
            File uploadFile = new File(uploadFileFullPath);
            log.info("물리적인 서버 파일 업로드 경로, uploadFile = {}", uploadFile);

            // 서버 상 물리적인 경로에 파일 업로드 수행
            multipartFile.transferTo(uploadFile);

            // DB에 도메인(게시판, 상품)별 데이터를 저장하기 위해 Builder 객체 생성 후 반환
            String uploadDBFilePath = createDBFilePath(fileType, storedFileName, categoryName);
            log.info("DB 파일 업로드 경로, uploadDBFilePath = {}", uploadDBFilePath);
            fileSaveRequestDto = buildFileSaveRequestDto(fileType, originalFilename, storedFileName, uploadDBFilePath, fileSize, ext);
        } catch (IOException | IllegalStateException | FileException e) {
            log.error("e = {}", e.getMessage(), e);
            throw new FileException(FAIL_SAVE_FILES);
        }
        return fileSaveRequestDto;
    }

    private FileSaveRequestDto buildFileSaveRequestDto(FileType fileType, String originalFilename, String storedFileName, String fileUploadPath, long fileSize, String ext) {
        FileSaveRequestDto fileSaveRequestDto = null;
        switch (fileType) {
            case posts:
                fileSaveRequestDto = builderPostFileSaveRequestDto(originalFilename, storedFileName, fileUploadPath, fileSize, ext);
                break;
            case products:
                fileSaveRequestDto = buildProductFileSaveRequestDto(originalFilename, storedFileName, fileUploadPath, fileSize, ext);
                break;
            default:
                log.error("Not Found FileType Error = {}", fileType);
                throw new FileException(FAIL_SAVE_FILES);
        }
        return fileSaveRequestDto;
    }

    private static PostFileSaveRequestDto builderPostFileSaveRequestDto(String originalFilename, String storedFileName, String fileUploadPath, long fileSize, String ext) {
        return PostFileSaveRequestDto
                .builder()
                .originFileName(originalFilename)
                .storedFileName(storedFileName)
                .filePath(fileUploadPath)
                .fileSize(fileSize)
                .fileExp(ext)
                .fileAttached("Y")
                .build();
    }

    private static ProductFileSaveRequestDto buildProductFileSaveRequestDto(String originalFilename, String storedFileName, String fileUploadPath, long fileSize, String ext) {
        return ProductFileSaveRequestDto
                .builder()
                .originFileName(originalFilename)
                .storedFileName(storedFileName)
                .filePath(fileUploadPath)
                .fileSize(fileSize)
                .fileExp(ext)
                .fileAttached("Y")
                .build();
    }

    /**
     * UUID 생성
     */
    public String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 서버상에 저장될 파일명 생성 후 반환
     * @param originalFilename UUID.png
     */
    public String createStoredFileName(String originalFilename) {
        String uuid = generateUUID();
        return uuid + "." + getFileExtension(originalFilename);
    }

    /**
     * 현재 날짜 반환(yyyy-MM-dd 형식)
     */
    private String getCurrentDate() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    /**
     * 서버상의 물리적 경로와 파일명을 반환
     * ex) /var/www/shoppingmall/upload/UUID.파일명
     */
    private String createFullFilePath(FileType fileType, String storedFileName, String categoryName) {
        String today = getCurrentDate();
        String directoryPath = getDirectoryPath(fileType, categoryName);

        String uploadFullFilePath = uploadPath + directoryPath + File.separator + today; // -> /Users/...
        log.debug("uploadFullFilePath = {}", uploadFullFilePath);

        createDirectory(uploadFullFilePath); // 디렉토리 경로 생성
        return uploadFullFilePath + File.separator + storedFileName; // 서버 업로드 경로/UUID.파일명
    }

    /**
     * DB에 저장하기 위한 File Path 생성
     */
    private String createDBFilePath(FileType fileType, String storedFileName, String categoryName) {
        String today = getCurrentDate();
        String directoryPath = getDirectoryPath(fileType, categoryName);
        return File.separator + directoryPath + File.separator + today + File.separator + storedFileName;
    }

    /**
     * 물리 디렉토리 경로 생성
     */
    private void createDirectory(String uploadFileFullPath) {
        File dir = new File(uploadFileFullPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    private String getDirectoryPath(FileType fileType, String categoryName) {
        Category category = Category.fromCategoryName(categoryName);

        switch (fileType) {
            case posts:
                return getDirectoryPathByPosts(fileType, category);
            case products:
                return getDirectoryPathByProducts(fileType, category);
            default:;
                log.error("존재하지 않는 파일 타입 입니다.");
                throw new FileException(FAIL_SAVE_FILES);
        }
    }

    /**
     * 게시판 이미지 저장 시, 카테고리 구분을 위한 함수
     * @param fileType
     * @param category
     * @return
     */
    private String getDirectoryPathByPosts(FileType fileType, Category category) {
        switch (category) {
            case GBOARD:
                return fileType + File.separator + Category.GBOARD.getLowerCaseName();
            case MBOARD:
                return fileType + File.separator + Category.MBOARD.getLowerCaseName();
            default:
                log.error("존재하지 않는 카테고리입니다.");
                throw new FileException(FAIL_SAVE_FILES);
        }
    }

    /**
     * 상품 이미지 저장 시, 카테고리 구분을 위한 함수
     * @param fileType
     * @param category
     * @return
     */
    private String getDirectoryPathByProducts(FileType fileType, Category category) {
        switch (category) {
            case PHONE:
                return fileType + File.separator + Category.PHONE.getLowerCaseName();
            case WATCH:
                return fileType + File.separator + Category.WATCH.getLowerCaseName();
            case TABLET:
                return fileType + File.separator + Category.TABLET.getLowerCaseName();
            case LAPTOP:
                return fileType + File.separator + Category.LAPTOP.getLowerCaseName();
            case ACCESSORY:
                return fileType + File.separator + Category.ACCESSORY.getLowerCaseName();
            default:
                log.error("존재하지 않는 제품 카테고리 입니다.");
                throw new FileException(FAIL_SAVE_FILES);
        }
    }

    /**
     * 원본 파일명에서, 확장자 추출 후 반환
     */
    private String getFileExtension(String originalFilename) {
        return StringUtils.getFilenameExtension(originalFilename);
    }

    /**
     * 파일 확장자 여부 체크
     * OK: jpg, jpeg, png, gif, pdf, mp4, svg
     */
    private boolean validateFileExtension(String ext) {
        return FileExtension.isAcceptFileExtension(ext);
    }

    /**
     * 파일 삭제
     */
    public <T extends Files> void deleteFiles(List<T> files) {
        for (Files file : files) {
            File deleteFile = new File(file.getFilePath());

            if (!isExistsFile(deleteFile)) {
                log.error("파일 경로가 존재하지 않습니다. filePath = {}", file.getFilePath());
                continue;
            }

            if (!deleteFile(deleteFile)) {
                log.error("파일 삭제 실패. filePath = {}", file.getFilePath());
            } else {
                log.info("파일 삭제 성공. filePath = {}", file.getFilePath());
            }
        }
    }

    private boolean isExistsFile(File file) {
        return file.exists();
    }

    private boolean deleteFile(File file) {
        return file.delete();
    }

    public String extractFileDateTimeByFilePath(String filePath) {
        if (!StringUtils.hasText(filePath)) {
            return "";
        }

        Matcher matcher = pattern.matcher(filePath);
        return matcher.find() ? matcher.group(0) : "";
    }

    public File getDownloadFile(String uploadPath, String domain, String fileCreatedDate, String storedFileName) {
        return new File(uploadPath + domain + File.separator + fileCreatedDate + File.separator + storedFileName);
    }

    public Resource getDownloadFileResource(String uploadPath) {
        return resourceLoader.getResource("file:" + uploadPath);
    }

    public InputStream getDownloadFileInputStream(Resource resource) {
        try {
            return resource.getInputStream();
        } catch (IOException e) {
            log.error(FAIL_DOWNLOAD_FILES.getMessage());
            throw new FileException(FAIL_DOWNLOAD_FILES);
        }
    }

    public HttpHeaders getHttpHeadersByDownloadFile(PostFiles files, Resource resource, InputStream inputStream) {
        HttpHeaders httpHeaders = new HttpHeaders();
        try {
            httpHeaders.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + files.getOriginFileName() + "\"");
            httpHeaders.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(resource.contentLength()));
            httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE.toString());
        } catch (IOException e) {
            log.error(FAIL_DOWNLOAD_FILES.getMessage());
            throw new FileException(FAIL_DOWNLOAD_FILES);
        }
        return httpHeaders;
    }

    public void responseZipFromAttachments(HttpServletResponse response, List<File> files) {
        // Todo: try with resource 소스 리팩터링 + gzip 안에 있는 파일명은 originalFileName 으로 보여주는게 좋을 것 같으니 추후 수정
        try (ZipOutputStream zos = new ZipOutputStream(response.getOutputStream())) {
            for (File file : files) {
                if (!file.exists() || !file.isFile()) {
                    continue;
                }
                handleZipOutPutStreamByFile(file, zos);
            }
            zos.finish();
        } catch (IOException e) {
            log.error(FAIL_DOWNLOAD_FILES.getMessage());
            throw new FileException(FAIL_DOWNLOAD_FILES);
        }
    }

    private void handleZipOutPutStreamByFile(File file, ZipOutputStream zos) throws IOException {
        FileSystemResource fsr = new FileSystemResource(file.getPath());
        ZipEntry zipEntry = new ZipEntry(Objects.requireNonNull(fsr.getFilename()));
        zipEntry.setSize(fsr.contentLength());
        zipEntry.setTime(System.currentTimeMillis());
        zos.putNextEntry(zipEntry);
        StreamUtils.copy(fsr.getInputStream(), zos);
        zos.closeEntry();
    }
}
